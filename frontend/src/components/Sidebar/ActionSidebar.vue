<!-- 右侧操作栏组件 -->
 <template>
  <el-aside width="150px" class="action-sidebar">
    <div class="user-avatar">{{ usernameInitial }}</div>
    <el-menu class="action-menu" router>
      <el-menu-item index="/profile/userinfo">个人信息</el-menu-item>
      <el-menu-item index="/profile/infoedit">编辑信息</el-menu-item>
      <el-menu-item index="/profile/changepassword">修改密码</el-menu-item>
      <el-menu-item index="/profile/deleteuser">注销账号</el-menu-item>
      <el-menu-item @click="handleLogout">退出登录</el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
/* import { ref, computed } from 'vue' */
 import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const store = useStore()

// 计算用户名首字母,这里貌似出错了？
const usernameInitial = ref('A')
const getUsernameInitial = () => {
  const username = store.getters['user/userInfo?.userName']
  if (username) {
    usernameInitial.value = username.charAt(0).toUpperCase()
  }
}
getUsernameInitial()

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
        
    // 调用 Vuex 的退出登录 action
    await store.dispatch('user/logout')
        
    ElMessage.success('退出登录成功')
        
    // 跳转到登录页
    router.push('/user/login')
        
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
      ElMessage.error('退出登录失败')
    }
  }
}

</script>

<style scoped>
.action-sidebar {
  background-color: #f5f7fa;
  border-left: 1px solid #eaecef;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #409eff;
  color: #fff;
  font-size: 24px;
  font-weight: bold;
  line-height: 60px;
  text-align: center;
  margin: 30px 0;
}
.action-menu {
  border-right: none;
  width: 100%;
}
.action-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  text-align: center;
  padding: 0;
}
</style>