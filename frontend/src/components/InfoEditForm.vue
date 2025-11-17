<template>
  <div class="profile-info">
    <h3 class="section-title">编辑个人信息</h3>
    <el-row :gutter="30" class="info-row">
      <!-- 用户ID（不可修改） -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">用户ID</label>
          <div class="info-value">{{ userInfo?.userId }}</div>
        </div>
      </el-col>
      <!-- 用户名（可修改） -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">用户名</label>
          <div class="info-value editable" @click="openEditDialog('userName')">
            {{ userInfo?.userName }}
          </div>
        </div>
      </el-col>
      <!-- 联系电话（可修改） -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">联系电话</label>
          <div class="info-value editable" @click="openEditDialog('phoneNumber')">
            {{ userInfo?.phoneNumber }}
          </div>
        </div>
      </el-col>
      <!-- 邮箱地址（可修改） -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">邮箱地址</label>
          <div class="info-value editable" @click="openEditDialog('userMailbox')">
            {{ userInfo?.userMailbox }}
          </div>
        </div>
      </el-col>
      <!-- 性别（可修改） -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">性别</label>
          <div class="info-value editable" @click="openEditDialog('gender')">
            {{ userInfo?.gender }}
          </div>
        </div>
      </el-col>
      <!-- 注册时间（不可修改） -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">注册时间</label>
          <div class="info-value">{{ formatRegisterTime(userInfo?.registerTime) }}</div>
        </div>
      </el-col>
    </el-row>
  </div>

  <!-- 编辑信息对话框 -->
  <el-dialog v-model="editDialogVisible" :title="`修改${getFieldName()}`" width="400" center>
    <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
      <!-- 动态渲染当前编辑的字段表单 -->
      <el-form-item :prop="currentField" :label="getFieldName()">
        <template v-if="currentField === 'gender'">
          <el-select v-model="editForm.gender" placeholder="请选择性别">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
            <el-option label="保密" value="保密"></el-option>
          </el-select>
        </template>
        <template v-else-if="currentField === 'phoneNumber'">
          <el-input v-model="editForm.phoneNumber" placeholder="请输入联系电话"></el-input>
        </template>
        <template v-else-if="currentField === 'userMailbox'">
          <el-input v-model="editForm.userMailbox" placeholder="请输入邮箱地址"></el-input>
        </template>
        <template v-else>
          <el-input v-model="editForm[currentField]" :placeholder="`请输入${getFieldName()}`"></el-input>
        </template>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditForm">确认修改</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
// 从Vuex获取用户信息
const userInfo = computed(() => store.getters['user/userInfo'])

// 编辑相关响应式变量
const editDialogVisible = ref(false) // 编辑对话框显示控制
const currentField = ref('') // 当前正在编辑的字段（userName/phoneNumber等）
const editFormRef = ref(null) // 编辑表单引用

// 编辑表单数据（与后端要求的提交格式一致）
const editForm = ref({
  userName: '',
  phoneNumber: '',
  userMailbox: '',
  gender: ''
})

// 编辑表单校验规则
const editRules = ref({
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 2, max: 10, message: '用户名长度2-10个字符', trigger: 'blur' }],
  phoneNumber: [{ required: true, message: '请输入联系电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
  userMailbox: [{ required: true, message: '请输入邮箱地址', trigger: 'blur' }, { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
})

// 打开编辑对话框：传入当前要修改的字段
const openEditDialog = (field) => {
  currentField.value = field
  // 给编辑表单赋值（从用户信息中获取当前值）
  editForm.value.userName = userInfo.value?.userName || ''
  editForm.value.phoneNumber = userInfo.value?.phoneNumber || ''
  editForm.value.userMailbox = userInfo.value?.userMailbox || ''
  editForm.value.gender = userInfo.value?.gender || ''
  editDialogVisible.value = true
}

// 获取字段的中文名称（用于对话框标题和表单标签）
const getFieldName = () => {
  const fieldMap = {
    userName: '用户名',
    phoneNumber: '联系电话',
    userMailbox: '邮箱地址',
    gender: '性别'
  }
  return fieldMap[currentField.value] || ''
}

// 格式化注册时间（将ISO格式转为友好显示）
const formatRegisterTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 提交编辑表单
const submitEditForm = async () => {
    try {
        // 表单校验
        await editFormRef.value.validate()
    
        // 构造提交数据（严格按照要求格式：只包含可修改字段）
        const submitData = {
            phoneNumber: editForm.value.phoneNumber,
            userMailbox: editForm.value.userMailbox,
            userName: editForm.value.userName,
            gender: editForm.value.gender
        }
    
        console.log('提交修改的数据：', submitData) // 可查看最终提交格式

        await store.dispatch('user/updateUserInfo', submitData)
    
        ElMessage.success('修改成功')
        editDialogVisible.value = false // 关闭对话框
    } catch (error) {
        console.error('修改失败：', error)
        ElMessage.error('修改失败，请重试')
    }
}
</script>

<style scoped>
.profile-info {
  padding: 30px;
  overflow-y: auto;
}
.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #409eff;
}
.info-row {
  margin-bottom: 25px;
}
.info-item {
  margin-bottom: 15px;
}
.info-label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}
.info-value {
  padding: 8px 12px;
  border: 1px solid #eaecef;
  border-radius: 4px;
  font-size: 14px;
  background-color: #f5f7fa;
}
.editable {
  background-color: #fff;
  cursor: pointer;
  transition: border-color 0.3s;
}
.editable:hover {
  border-color: #409eff;
}
/* 对话框表单间距优化 */
.el-form-item {
  margin-bottom: 20px;
}
</style>