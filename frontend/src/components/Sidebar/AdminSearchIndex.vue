<!-- 左侧导航栏组件 -->
<template>
  <el-aside width="200px" class="sidebar">
    <div class="logo">FEnglish</div>
    <!-- 默认选中「我的账号」对应的路由 -->
    <el-menu
      default-active="/profile/admin/userbyadmin"
      class="sidebar-menu"
      router
      :unique-opened="true"
    >
      <el-menu-item index="/profile/admin/userbyadmin">
        <!-- <el-icon><User /></el-icon> -->
        <span>用户账号管理</span>
      </el-menu-item>
      <!-- <el-menu-item index="/plaza">
        <el-icon><Grid /></el-icon>
        <span>学习广场</span>
      </el-menu-item>
      <el-menu-item index="/favorites">
        <el-icon><Star /></el-icon>
        <span>收藏单词</span>
      </el-menu-item>
      <el-menu-item index="/wordbook">
        <el-icon><Book /></el-icon>
        <span>我的单词书</span>
      </el-menu-item>
      <el-menu-item index="/record">
        <el-icon><EditPen /></el-icon>
        <span>学习记录</span>
      </el-menu-item> -->
    </el-menu>
  </el-aside>
</template>

<style scoped>
.sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #eaecef;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  border-bottom: 1px solid #eaecef;
}
.sidebar-menu {
  border-right: none;
  flex: 1;
  padding-top: 20px;
}
.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}
</style>