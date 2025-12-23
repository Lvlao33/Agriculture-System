//配置开发服务器和代理
//当前端请求发送到开发服务器时生成代理
module.exports = {
  // 关闭 ESLint 报错阻断（否则一堆报错导致无法启动）
  lintOnSave: false,

  // 软件依赖
  transpileDependencies: [],

  // 配置静态资源
  publicPath: '/',
  assetsDir: 'static',


  // 开发服务器配置
  devServer: {
    port: 5173, // 前端运行端口号，可改为你想要的
    // 设置响应头，确保UTF-8编码
    headers: {
      'Content-Type': 'text/html; charset=utf-8'
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        secure: false, // 如果是 https 接口，需要配置这个选项
        ws: true, // 代理 websockets
        logLevel: 'debug' // 开启调试日志
        // 不重写路径，因为后端接口路径已经包含 /api
      },
      '/order': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      },
      '/img': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        bypass: function() {
          // 如果请求的是静态资源，跳过代理
          // public 目录下的文件会优先被处理
          return null;
        }
      },
      '/kn': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      },
      // '/user' 代理已移除，统一使用 '/api' 前缀进行后端代理，避免与静态资源路径冲突
      '/question': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      },
      '/price': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      },
      '/cart': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        secure: false,
        ws: true
      }
    }
  }
}
