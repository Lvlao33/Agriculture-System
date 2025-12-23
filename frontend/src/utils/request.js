import axios from 'axios'
import { getToken, clear } from './tokenManager'

export function request(config) {
    // 1. 创建 axios 实例
    const instance = axios.create({
        timeout: 100000,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    })

    // 2. 请求拦截器：处理 UTF-8 编码并自动注入 JWT Token
    instance.interceptors.request.use(
        config => {
            // 确保请求头包含 UTF-8 编码
            if (config.headers) {
                config.headers['Content-Type'] = config.headers['Content-Type'] || 'application/json;charset=UTF-8';
            }

            // 从 tokenManager 获取 token 并添加到 Authorization header
            const token = getToken()
            if (token) {
                // 如果 token 已经包含 Bearer 前缀则直接使用，否则手动拼接
                const headerValue = token.startsWith('Bearer ') ? token : `Bearer ${token}`
                config.headers['Authorization'] = headerValue
                
                // 调试日志（掩码处理）
                try {
                    const raw = headerValue.replace(/^Bearer\s*/i, '')
                    const preview = raw.length > 6 ? raw.substring(0, 6) + '...' : raw
                    console.log(`✓ 自动添加JWT token (预览: ${preview})`)
                } catch (e) {
                    console.log('✓ 自动添加JWT token')
                }
            }
            return config
        },
        err => {
            console.error('请求配置错误:', err)
            return Promise.reject(err)
        }
    )

    // 3. 响应拦截器：处理数据返回及各种异常状态（Token过期、无权限等）
    instance.interceptors.response.use(
        res => {
            // 请求成功，直接返回数据部分
            return res.data
        },
        err => {
            console.error('请求失败:', err)

            if (err.response) {
                const status = err.response.status
                const data = err.response.data

                console.error('响应状态:', status)
                console.error('响应数据:', data)

                // --- 核心业务逻辑控制 ---
                if (status === 401) {
                    // 401: 未授权（Token过期或无效）
                    console.warn('⚠️ Token过期或无效，即将清理并重定向到登录页')
                    clear() // 清除本地存储的 token 和用户信息
                    if (window.location.pathname !== '/login') {
                        window.location.href = '/login'
                    }
                    return Promise.reject(new Error('Token已过期，请重新登录'))
                }

                if (status === 403) {
                    console.warn('⚠️ 无权限访问此资源')
                    return Promise.reject(new Error('无权限访问此资源'))
                }

                if (status === 404) {
                    console.error('✗ 请求的资源不存在')
                    return Promise.reject(new Error('请求的资源不存在'))
                }

                if (status === 500) {
                    console.error('✗ 服务器内部错误')
                    return Promise.reject(new Error('服务器内部错误'))
                }

                // 其他带响应体的错误处理
                const errorData = data || {}
                if (typeof errorData === 'object' && errorData !== null) {
                    errorData.status = status
                }
                return Promise.reject(errorData)
            }

            // 网络错误（无响应体的情况）
            const networkError = {
                message: err.message || '网络连接失败，请检查网络设置',
                code: err.code,
                isNetworkError: true
            }
            return Promise.reject(networkError)
        }
    )

    // 4. 执行并返回请求实例
    return instance(config)
}

export default { request }