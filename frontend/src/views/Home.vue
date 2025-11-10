<template>
  <Layout>
    <div style="padding: 20px;">
      <h2>欢迎使用英语单词学习系统</h2>
      <p>当前用户：{{ $store.state.user?.username }}</p>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import Layout from '../components/Layout/index.vue'; // 引入布局组件
</script>