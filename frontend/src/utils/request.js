/**
 * Axios请求模块
 * 提供统一的HTTP请求接口
 * 自动处理JWT token的请求头添加和响应拦截
 */

import axios from 'axios'
import { getToken, clear } from './tokenManager'

export function request(config) {
    // 创建axios实例
    const instance = axios.create({
        timeout: 100000,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    })

    // ============ 请求拦截器 ============
    // 自动添加JWT token到Authorization header
    instance.interceptors.request.use(
        config => {
            // 确保请求头包含UTF-8编码
            if (config.headers) {
                config.headers['Content-Type'] = config.headers['Content-Type'] || 'application/json;charset=UTF-8'
            }

            // 从tokenManager获取token并添加到Authorization header
            const token = getToken()
            if (token) {
                // 如果 token 已经包含 Bearer 前缀，则直接使用，避免重复添加
                const headerValue = token.startsWith('Bearer ') ? token : `Bearer ${token}`
                config.headers['Authorization'] = headerValue
                // 打印掩码化信息以便调试（不输出完整token）
                try {
                    const raw = headerValue.replace(/^Bearer\s*/i, '')
                    const preview = raw.length > 6 ? raw.substring(0, 6) + '...' : raw
                    console.log(`✓ 自动添加JWT token到请求头 (preview=${preview}, len=${raw.length})`)
                } catch (e) {
                    console.log('✓ 自动添加JWT token到请求头')
                }
            }

            return config
        },
        err => {
            console.error('请求配置错误:', err)
            return Promise.reject(err)
        }
    )

    // ============ 响应拦截器 ============
    // 处理响应和token过期等错误
    instance.interceptors.response.use(
        res => {
            // 响应成功
            return res.data
        },
        err => {
            console.error('请求失败:', err)

            if (err.response) {
                const status = err.response.status
                const data = err.response.data

                console.error('响应状态:', status)
                console.error('响应数据:', data)

                // ============ 处理特定的HTTP状态码 ============
                if (status === 401) {
                    // 401: 未授权（Token过期或无效）
                    console.warn('⚠️ Token过期或无效，即将清除本地token并重定向到登录页')
                    clear() // 清除本地的token和用户信息

                    // 重定向到登录页（如果不是登录页本身）
                    if (window.location.pathname !== '/login') {
                        window.location.href = '/login'
                    }

                    return Promise.reject(new Error('Token已过期，请重新登录'))
                }

                if (status === 403) {
                    // 403: 禁止访问（无权限）
                    console.warn('⚠️ 无权限访问此资源')
                    return Promise.reject(new Error('无权限访问此资源'))
                }

                if (status === 404) {
                    // 404: 资源不存在
                    console.error('✗ 请求的资源不存在')
                    return Promise.reject(new Error('请求的资源不存在'))
                }

                if (status === 500) {
                    // 500: 服务器错误
                    console.error('✗ 服务器内部错误')
                    return Promise.reject(new Error('服务器内部错误'))
                }

                // 返回错误响应数据，让调用方可以处理
                return Promise.reject(data || err)
            }

            // 网络错误或其他错误
            console.error('✗ 网络请求失败或其他错误')
            return Promise.reject(err)
        }
    )

    // 执行请求
    return instance(config)
}

export default { request }
