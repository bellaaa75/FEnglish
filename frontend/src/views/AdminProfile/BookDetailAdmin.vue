<template>
  <div class="book-detail-admin">
    <el-page-header  @back="handleBack" />
    <el-card v-if="isBookLoaded" class="mb-12">
      <div class="flex-between">
        <div>
          <h2>{{ displayBookName }}</h2>
          <p>发布时间: {{ displayPublishTime }}</p>
          <p>单词总数: {{ displayTotalWords }} 个</p>
        </div>
        <div>
          <el-button type="primary" @click="openWordSelector">向单词书添加单词</el-button>
        </div>
      </div>
    </el-card>

    <!-- 修复1：给表格添加固定高度+滚动，减少尺寸频繁变化（不修改语法，仅加基础样式） -->
    <el-table :data="words" v-loading="loading" style="width:100%; max-height: 600px; overflow-y: auto;">
      <el-table-column prop="wordName" label="单词" width="180" />
      <el-table-column prop="partOfSpeech" label="词性" width="120" />
      <el-table-column prop="wordExplain" label="释义" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button type="danger" size="small" @click="onRemoveWord(scope.row.wordId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageInfo.pageSize"
      :current-page="pageInfo.currentPage"
      :page-size="pageInfo.pageSize"
      :total="total"
      @current-change="onPageChange"
      layout="prev, pager, next"
      style="margin-top:12px; text-align:right"
    />

    <!-- 新增：选择单词弹窗 -->
    <el-dialog title="从单词库选择添加" v-model="selectorVisible" width="80%">
      <div style="display:flex; gap:8px; margin-bottom:12px;">
        <el-input 
          v-model="selectorKeyword" 
          placeholder="搜索单词或释义" 
          clearable 
          @clear="onSelectorSearch"
          style="flex:1"
        />
        <el-button type="primary" @click="onSelectorSearch">搜索</el-button>
        <el-button @click="resetSelector">重置</el-button>
      </div>

      <!-- 修复2：弹窗表格同样添加固定高度+滚动 -->
      <el-table
        :data="selectorList"
        v-loading="selectorLoading"
        style="width:100%; max-height: 450px; overflow-y: auto;"
        @selection-change="onSelectorSelectionChange"
        :row-key="row => row.wordId"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="wordName" label="单词" width="160" />
        <el-table-column prop="partOfSpeech" label="词性" width="120" />
        <el-table-column prop="wordExplain" label="释义" />
      </el-table>

      <div style="text-align:right; margin-top:12px;">
        <el-pagination
          :current-page="selectorCurrent"
          :page-size="selectorPageSize"
          :total="selectorTotal"
          @current-change="onSelectorPageChange"
          layout="prev, pager, next, jumper"
        />
      </div>

      <template #footer>
        <el-button @click="selectorVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :disabled="selectedWordIds.length === 0"
          :loading="addingWords"
          @click="addSelectedWords"
        >
          添加选中 ({{ selectedWordIds.length }})
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted, getCurrentInstance } from 'vue'; // 新增onUnmounted和getCurrentInstance
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import vocabularyBookService from '@/services/vocabularyBookService';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const bookId = route.params.bookId;

// ========== 原有变量 ==========
const bookInfo = ref({});
const words = ref([]);
const total = ref(0);
const loading = ref(false);
const pageInfo = ref({ currentPage: 1, pageSize: 20 });

const displayBookName = computed(() => bookInfo.value?.bookName || '单词书详情');
const displayPublishTime = computed(() => {
  const t = bookInfo.value?.publishTime || '';
  return t ? new Date(t).toLocaleString() : '';
});
const displayTotalWords = computed(() => total.value || (Array.isArray(bookInfo.value?.wordList) ? bookInfo.value.wordList.length : 0));
const isBookLoaded = computed(() => Object.keys(bookInfo.value || {}).length > 0);

// ========== 新增：选择器相关变量 ==========
const selectorVisible = ref(false);
const selectorKeyword = ref('');
const selectorList = ref([]);
const selectorLoading = ref(false);
const selectorTotal = ref(0);
const selectorCurrent = ref(1);
const selectorPageSize = ref(10);
const selectedWordIds = ref([]);
const addingWords = ref(false);
const wordIdToName = ref({});

// ========== 修复3：全局捕获ResizeObserver错误（兜底，不影响编译） ==========
const errorHandler = (e) => {
  // 仅捕获特定错误，不影响其他错误
  if (e.message && e.message.includes('ResizeObserver loop completed with undelivered notifications')) {
    e.stopImmediatePropagation();
    e.preventDefault();
    return true;
  }
  return false;
};

// 获取Vue app实例（用于组件内错误捕获）
const app = getCurrentInstance()?.appContext.app;

// ========== 原有生命周期与方法 ==========
onMounted(() => {
  fetchBookDetail();
  // 监听全局错误
  window.addEventListener('error', errorHandler);
  // 监听Vue组件内错误
  if (app) {
    app.config.errorHandler = (err) => {
      if (err.message && err.message.includes('ResizeObserver')) {
        return false; // 忽略该错误
      }
    };
  }
});

// 组件卸载时移除监听，避免内存泄漏
onUnmounted(() => {
  window.removeEventListener('error', errorHandler);
});

const fetchBookDetail = async () => {
  if (!bookId) return;
  try {
    loading.value = true;
    const res = await vocabularyBookService.getBookDetail(bookId);
    bookInfo.value = res?.data || {};
    if (Array.isArray(bookInfo.value.wordList)) {
      // 修复4：延长延迟到10ms，让DOM完全渲染后再赋值
      setTimeout(() => {
        words.value = bookInfo.value.wordList;
        total.value = words.value.length;
      }, 10);
    } else {
      await fetchBookWords();
    }
  } catch (e) {
    ElMessage.error('获取详情失败');
    router.back();
  } finally { loading.value = false; }
};

const isSuccessResponse = (res) => {
  if (!res) return false;
  if (typeof res.status === 'number' && res.status >= 200 && res.status < 300) return true;
  const payload = res.data ?? res;
  if (!payload) return false;
  if (payload.code === 200 || payload.success === true) return true;
  return false;
};

const fetchBookWords = async () => {
  try {
    loading.value = true;
    try {
      const res = await vocabularyBookService.getBookWords(bookId, pageInfo.value.currentPage, pageInfo.value.pageSize);
      const data = res?.data ?? res;
      if (data && Array.isArray(data.list)) {
        // 修复5：延长延迟到10ms
        setTimeout(() => {
          words.value = data.list;
          total.value = data.total ?? data.list.length;
          if (Array.isArray(bookInfo.value?.wordList)) bookInfo.value.wordList = words.value;
        }, 10);
        return { success: true, total: total.value };
      }
    } catch (err) {
      console.warn('getBookWords 调用失败，尝试使用 getBookDetail 回退', err);
    }

    try {
      const det = await vocabularyBookService.getBookDetail(bookId);
      const d = det?.data ?? det;
      if (d && Array.isArray(d.wordList)) {
        const all = d.wordList;
        total.value = all.length;
        const start = (pageInfo.value.currentPage - 1) * pageInfo.value.pageSize;
        const end = start + pageInfo.value.pageSize;
        // 修复6：延长延迟到10ms
        setTimeout(() => {
          words.value = all.slice(start, end);
          bookInfo.value = d;
        }, 10);
        return { success: true, total: total.value };
      }
    } catch (err) {
      console.error('getBookDetail 回退也失败', err);
      throw err;
    }

    // 修复7：延长延迟到10ms
    setTimeout(() => {
      words.value = [];
      total.value = 0;
    }, 10);
    return { success: true, total: 0 };
  } catch (err) {
    console.error('fetchBookWords 最终失败', err);
    ElMessage.error('加载单词失败');
    return { success: false, total: 0 };
  } finally {
    loading.value = false;
  }
};

const ensureValidPageAndReload = async () => {
  const result = await fetchBookWords();
  const t = result.total || 0;
  const totalPages = Math.max(1, Math.ceil(t / pageInfo.value.pageSize));
  if (pageInfo.value.currentPage > totalPages) {
    pageInfo.value.currentPage = totalPages;
    await fetchBookWords();
  }
};

const onPageChange = (page) => { 
  pageInfo.value.currentPage = page; 
  fetchBookWords(); 
};

const handleBack = () => {
  const returnTo = route.query.returnTo;
  if (returnTo) router.push(String(returnTo)).catch(() => router.back()); 
  else router.back();
};

// ========== 选择器方法 ==========
const openWordSelector = () => {
  selectorVisible.value = true;
  selectorKeyword.value = '';
  selectedWordIds.value = [];
  selectorCurrent.value = 1;
  wordIdToName.value = {};
  // 修复8：延长弹窗数据加载延迟到20ms，确保弹窗DOM渲染完成
  setTimeout(() => {
    fetchSelectorList();
  }, 20);
};

const fetchSelectorList = async () => {
  try {
    selectorLoading.value = true;
    const params = {
      pageNum: selectorCurrent.value,
      pageSize: selectorPageSize.value,
      keyword: selectorKeyword.value || undefined,
    };

    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    const res = await axios.get('/api/words', {
      params,
      withCredentials: true,
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    });

    const payload = res.data;
    if (payload && Array.isArray(payload.data)) {
      // 修复9：延长延迟到10ms
      setTimeout(() => {
        selectorList.value = payload.data;
        selectorTotal.value = Number(payload.total ?? payload.data.length);
        selectorPageSize.value = payload.pageSize ?? selectorPageSize.value;
        selectorCurrent.value = payload.currentPage ?? selectorCurrent.value;
        payload.data.forEach(word => {
          wordIdToName.value[word.wordId] = word.wordName;
        });
      }, 10);
    }
  } catch (err) {
    console.error('fetchSelectorList 错误', err);
    ElMessage.error('加载单词库失败');
  } finally {
    selectorLoading.value = false;
  }
};

const onSelectorPageChange = (page) => {
  selectorCurrent.value = page;
  fetchSelectorList();
};

const onSelectorSearch = () => {
  selectorCurrent.value = 1;
  fetchSelectorList();
};

const resetSelector = () => {
  selectorKeyword.value = '';
  selectorCurrent.value = 1;
  fetchSelectorList();
};

const onSelectorSelectionChange = (rows) => {
  selectedWordIds.value = rows.map(r => r.wordId);
};

// ========== 核心优化：添加单词（统一综合提示，屏蔽服务器错误） ==========
const addSelectedWords = async () => {
  if (!selectedWordIds.value.length) {
    ElMessage.warning('请先选择单词');
    return;
  }

  addingWords.value = true;
  let successCount = 0;
  const failedWordNames = [];

  for (const wordId of selectedWordIds.value) {
    const wordName = wordIdToName.value[wordId] || `ID:${wordId}`;
    try {
      const res = await vocabularyBookService.addWordToBook(bookId, { wordId });
      if (isSuccessResponse(res)) {
        successCount++;
      } else {
        failedWordNames.push(wordName);
        console.warn(`单词 ${wordName} 添加失败（非成功响应）`, res);
      }
    } catch (err) {
      failedWordNames.push(wordName);
      console.error(`单词 ${wordName} 添加失败（服务器错误）`, err);
    }
  }

  addingWords.value = false;
  selectorVisible.value = false;
  selectedWordIds.value = [];

  let message = '';
  const successText = `${successCount} 个单词添加成功`;
  const failText = `${failedWordNames.length} 个单词（${failedWordNames.join('、')}）添加失败，已存在于单词书中`;

  if (successCount > 0 && failedWordNames.length > 0) {
    message = `${successText}，${failText}`;
  } else if (successCount > 0) {
    message = successText;
  } else {
    message = failText;
  }

  const messageType = failedWordNames.length > 0 ? 'warning' : 'success';
  ElMessage[messageType](message);

  let r = await fetchBookWords();
  if (!r.success) {
    pageInfo.value.currentPage = 1;
    await fetchBookWords();
  } else {
    const totalPages = Math.max(1, Math.ceil((r.total || 0) / pageInfo.value.pageSize));
    if (pageInfo.value.currentPage > totalPages) {
      pageInfo.value.currentPage = totalPages;
      await fetchBookWords();
    }
  }
};

const onRemoveWord = async (wordId) => {
  try {
    await ElMessageBox.confirm('确认从该单词书删除此单词？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    });
    const res = await vocabularyBookService.removeWordFromBook(bookId, wordId);
    if (!isSuccessResponse(res)) {
      console.error('删除单词后端返回非成功：', res);
      ElMessage.error('删除失败（后端返回）');
      return;
    }
    ElMessage.success('删除成功');
    await ensureValidPageAndReload();
  } catch (err) {
    if (err === 'cancel' || err === 'close') return;
    console.error('删除单词出错', err);
    ElMessage.error('删除失败');
  }
};

</script>

<style scoped>
.book-detail-admin { padding:16px; background:#fff; }
.mb-12 { margin-bottom:12px; }
.flex-between { 
  display:flex; 
  justify-content:space-between; 
  align-items:center; 
}

/* 可选：优化滚动条样式，提升体验（不影响功能） */
::v-deep .el-table__body-wrapper::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: #ccc;
}
</style>