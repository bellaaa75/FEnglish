const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 7011, // 保持你原来的前端端口,8081,7000-8
    proxy: {
      // 这里的 '/api' 是一个标识，用来匹配所有以 /api 开头的请求
      '/api': {
        target: 'http://localhost:8080', // 你的后端 API 基础地址
        changeOrigin: true, // 改变源地址，解决跨域问题
      }
    }
  }
})

