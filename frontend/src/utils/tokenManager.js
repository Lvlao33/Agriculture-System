/**
 * Token管理器
 * 负责管理JWT token的保存、读取、删除等操作
 * 所有与token相关的操作都通过此模块进行
 */

// Token存储的key
const TOKEN_KEY = 'jwtToken'
const USER_KEY = 'user'
const ROLE_KEY = 'userRole'
const USERNAME_KEY = 'username'
const USERID_KEY = 'userId'

/**
 * 保存token和用户信息到localStorage
 * @param {string} token JWT token
 * @param {object} user 用户对象 {id, username, nickname, avatar}
 * @param {string} role 用户角色 (farmer, expert, bank)
 */
export function setToken(token, user, role) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
    // 兼容旧代码：有些地方使用 localStorage['token'] 保存/读取
    localStorage.setItem('token', token)
  }
  if (user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
    if (user.username) {
      localStorage.setItem(USERNAME_KEY, user.username)
    }
    if (user.id) {
      localStorage.setItem(USERID_KEY, user.id)
    }
  }
  if (role) {
    localStorage.setItem(ROLE_KEY, role)
  }
}

/**
 * 获取保存的token
 * @returns {string|null} token字符串或null
 */
export function getToken() {
  // 优先返回统一的 TOKEN_KEY；若旧代码使用 'token' 键则兼容返回
  return localStorage.getItem(TOKEN_KEY) || localStorage.getItem('token')
}

/**
 * 获取保存的用户角色
 * @returns {string|null} 角色字符串 (farmer, expert, bank) 或null
 */
export function getRole() {
  return localStorage.getItem(ROLE_KEY) || 'farmer'
}

/**
 * 获取保存的用户信息
 * @returns {object|null} 用户对象或null
 */
export function getUser() {
  const userStr = localStorage.getItem(USER_KEY)
  return userStr ? JSON.parse(userStr) : null
}

/**
 * 获取保存的用户ID
 * @returns {number|null} 用户ID或null
 */
export function getUserId() {
  const id = localStorage.getItem(USERID_KEY)
  return id ? Number(id) : null
}

/**
 * 获取保存的用户名
 * @returns {string|null} 用户名或null
 */
export function getUsername() {
  return localStorage.getItem(USERNAME_KEY)
}

/**
 * 检查token是否存在
 * @returns {boolean}
 */
export function isTokenValid() {
  return !!getToken()
}

/**
 * 检查用户是否已认证
 * @returns {boolean}
 */
export function isAuthenticated() {
  return isTokenValid() && !!getUser()
}

/**
 * 清除所有认证信息（登出时调用）
 */
export function clear() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(ROLE_KEY)
  localStorage.removeItem(USERNAME_KEY)
  localStorage.removeItem(USERID_KEY)
  // 同步清理兼容键
  localStorage.removeItem('token')
}

/**
 * 检查用户是否具有指定角色
 * @param {string} role 要检查的角色
 * @returns {boolean}
 */
export function hasRole(role) {
  const currentRole = getRole()
  return currentRole && currentRole.toLowerCase() === role.toLowerCase()
}

/**
 * 检查用户是否是农户
 * @returns {boolean}
 */
export function isFarmer() {
  return hasRole('farmer')
}

/**
 * 检查用户是否是专家
 * @returns {boolean}
 */
export function isExpert() {
  return hasRole('expert')
}

/**
 * 检查用户是否是银行
 * @returns {boolean}
 */
export function isBank() {
  return hasRole('bank')
}

export default {
  setToken,
  getToken,
  getRole,
  getUser,
  getUserId,
  getUsername,
  isTokenValid,
  isAuthenticated,
  clear,
  hasRole,
  isFarmer,
  isExpert,
  isBank
}
