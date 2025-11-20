//配置开发服务器和代理
//只在请求发送到开发服务器时生效
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
        target: 'http://localhost:8081', // 后端服务地址
        changeOrigin: true
        // 不重写路径，因为后端接口路径已经包含 /api
      },
      '/order': {
        target: 'http://localhost:8081', // 后端服务地址
        changeOrigin: true
      },
      '/img': {
        target: 'http://localhost:8081', // 后端服务地址
        changeOrigin: true,
        bypass: function() {
          // 如果请求的是本地静态资源，跳过代理
          // public 目录下的文件会优先被 devServer 处理
          return null;
        }
      },
      '/kn': {
        target: 'http://localhost:8081', // 后端服务地址
        changeOrigin: true
      }
    }
  }
}
