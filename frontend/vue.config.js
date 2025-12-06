//配置开发服务器和代�?
//叜�请求发送到开发服务器时生�?
module.exports = {
  // 关闭 ESLint 报错阻断（否则一堆报错�致无法启劼�
  lintOnSave: false,

  // 软�依赖
  transpileDependencies: [],

  // 配置静态资�?
  publicPath: '/',
  assetsDir: 'static',

  // 开发服务器配置
  devServer: {
    port: 5173, // 前�运�竏�号，叻�改为你想要的
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true,
        secure: false, // 如果� https 接口，需要配罿�丏��
        ws: true, // 代理 websockets
        logLevel: 'debug' // 开吰�试日�
        // 不重写路径，因为后�接口跾�已经包含 /api
      },
      '/order': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true
      },
      '/img': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true,
        bypass: function() {
          // 如果请求的是朜�静态资源，跳过代理
          // public 盽�下的文件会优先�处理
          return null;
        }
      },
      '/kn': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true
      },
      '/question': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true
      },
      '/price': {
        target: 'http://localhost:8080', // 后�服务地址
        changeOrigin: true
      }
    }
  }
}
