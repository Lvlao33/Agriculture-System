import Vue from 'vue'
import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/zh-CN' // 引入中文语言包
import 'element-ui/lib/theme-chalk/index.css'
import 'bootstrap/dist/css/bootstrap.css'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import './less/reset.less'
import preventMultiClick from './utils/preventMultiClick'

Vue.use(preventMultiClick)
Vue.config.productionTip = false
Vue.prototype.axios = axios
Vue.use(ElementUI, { locale }) // 使用中文语言包

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')