const path = require('path')

module.exports = {
  // 关闭 ESLint 报错阻断（否则一堆报错导致无法启动）
  lintOnSave: false,

  // 转译依赖
  transpileDependencies: [],

  // 配置静态资源
  publicPath: '/',
  assetsDir: 'static',

  // 开发服务器配置
  devServer: {
    port: 5173, // 前端运行端口号，可以改为你想要的
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        pathRewrite: { '^/api': '' } // 可选，去掉 /api 前缀
      },
      '/order': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      },
      '/img': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      },
      '/kn': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      }
    }
  }
}
