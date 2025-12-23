import Vue from 'vue'
import Vuex from 'vuex'
import { getRole, clear as clearToken } from '../utils/tokenManager'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    role: [],
    userRole: localStorage.getItem('userRole') || 'farmer',
    detailData: [],
    orderId: 0,
    changedOrderId: 0,
    changedKnowledgeId: 0,
    user: '',
    loginUserNickname: localStorage.getItem('loginUserNickname') ? localStorage.getItem('loginUserNickname') : '',
    loginUserAvatar: localStorage.getItem('loginUserAvatar') ? localStorage.getItem('loginUserAvatar') : '',
    loginUserId: localStorage.getItem('loginUserId') ? Number(localStorage.getItem('loginUserId')) : '',
    activeIndex: '1',
    publishActiveIndex: '1',
    userActiveIndex: '1-1',
    token: localStorage.getItem('token') ? localStorage.getItem('token') : '',
    paymentInfo: '',
    // 使用相对路径，自动匹配当前域名和端口
    // 如果后端运行在 localhost:8080，前端运行在 localhost:5173，需要配置代理或使用完整URL
    imgShowRoad: process.env.NODE_ENV === 'production' ? 'http://119.3.180.117:9090' : 'http://localhost:8080',
    fileUploadRoad: process.env.NODE_ENV === 'production' ? 'http://119.3.180.117:9090' : 'http://localhost:8080',
    mutiFile: '',
  },
  getters: {
    getStorage(state) {
      if (!state.token) {
        state.token = JSON.parse(localStorage.getItem(key))
        return state.token
      }
    },
    isExpert(state) {
      for (var k in state.role) {
        if (state.role[k].authority === "expert") {
          return true;
        }
      }
      return false;
    },
    isAdmin(state) {
      for (var k in state.role) {
        if (state.role[k].authority === "admin") {
          return true;
        }
      }
      return false;
    },
    currentRole(state) {
      return state.userRole || 'farmer'
    },
    isFarmerRole(state) {
      return state.userRole === 'farmer'
    },
    isExpertRole(state) {
      return state.userRole === 'expert'
    },
    isBankRole(state) {
      return state.userRole === 'bank'
    }
  },
  mutations: {
    updateOrderId(state, value) {
      state.orderId = value
    },
    updateDetailData(state, value) {
      state.detailData = value
      console.log('state.detailData', state.detailData)
    },
    updateChangedOrderId(state, value) {
      state.changedOrderId = value
    },
    updateChangedKnowledgeId(state, value) {
      state.changedKnowledgeId = value
    },
    updateLoginUserNickname(state, value) {
      state.loginUserNickname = value
      if (value) {
        localStorage.setItem('loginUserNickname', value)
      } else {
        localStorage.removeItem('loginUserNickname')
      }
    },
    updateLoginUserAvatar(state, value) {
      state.loginUserAvatar = value
      if (value) {
        localStorage.setItem('loginUserAvatar', value)
      } else {
        localStorage.removeItem('loginUserAvatar')
      }
    },
    updateLoginUserId(state, value) {
      state.loginUserId = value
      if (value || value === 0) {
        localStorage.setItem('loginUserId', value)
      } else {
        localStorage.removeItem('loginUserId')
      }
    },
    updateRole(state, value) {
      state.role = value
    },
    setUserRole(state, value) {
      const role = value || 'farmer'
      state.userRole = role
      localStorage.setItem('userRole', role)
    },
    updatePaymentInfo(state, value) {
      state.paymentInfo = value
    },
    updateActiveIndex(state, value) {
      state.activeIndex = value
    },
    updatePublishActiveIndex(state, value) {
      state.publishActiveIndex = value
    },
    updateUserActiveIndex(state, value) {
      state.userActiveIndex = value
    },
    // 设置存储token
    setToken(state, value) {
      state.token = value;
      localStorage.setItem('token', value)
    },
    // 删除token并清除所有用户信息
    removeStorage(state) {
      state.token = ''
      state.loginUserNickname = ''
      state.loginUserAvatar = ''
      state.loginUserId = ''
      state.userRole = 'farmer'
      localStorage.removeItem('token');
      localStorage.removeItem('loginUserNickname')
      localStorage.removeItem('loginUserAvatar')
      localStorage.removeItem('loginUserId')
      localStorage.removeItem('userRole')
      clearToken() // 清除tokenManager中的所有数据
    },
    // 从tokenManager同步角色
    syncRoleFromToken(state) {
      const role = getRole() || 'farmer'
      state.userRole = role
      localStorage.setItem('userRole', role)
      console.log('✓ 从JWT Token同步角色:', role)
    }
  },
  actions: {
    // 应用启动时从localStorage恢复用户状态
    restoreUserState({ commit }) {
      const savedRole = localStorage.getItem('userRole')
      if (savedRole) {
        commit('setUserRole', savedRole)
        console.log('✓ 从localStorage恢复用户角色:', savedRole)
      }
    },
    // 清除所有用户数据
    clearAllUserData({ commit }) {
      commit('removeStorage')
      console.log('✓ 已清除所有用户数据')
    }
  },
  modules: {
  }
})
