import { request } from '../utils/request'

// 忘记密码
export function forgetPassword(params) {
    return request({
        method: 'post',
        url: '/api/user/forgetPassword',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 登录
export function userLogin(params) {
    return request({
        method: 'post',
        url: '/api/auth/login',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 注册
export function userRegister(params) {
    return request({
        method: 'post',
        url: '/api/auth/register',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 根据用户名查询登录信息
export function loginSelectByUsername(params) {
    return request({
        method: 'get',
        url: '/api/user/' + (params.username || params.user_name || ''),
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 根据用户名修改登录信息
export function loginUpdateByUsername(params) {
    return request({
        method: 'post',
        url: '/api/user/loginUpdateByUsername',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 根据用户名查询用户信息
export function selectUserByUsername(params) {
    return request({
        method: 'get',
        url: '/api/user/' + params.user_name,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 根据用户名删除用户
export function deleteUserByUsername(params) {
    return request({
        method: 'delete',
        url: '/api/user/' + params.user_name,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 根据用户名修改用户信息
export function updateUserByUsername(params) {
    return request({
        method: 'put',
        url: '/api/user/' + params.user_name,
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 查询所有用户信息
export function selectAllUserPage(params) {
    return request({
        method: 'get',
        url: '/api/user/search/' + params.pageNum,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 修改用户密码
export function updateUserPassword(params) {
    return request({
        method: 'post',
        url: '/api/user/loginUpdatePassword',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 根据userID查询用户
export function searchUserById(id) {
    return request({
        method: 'get',
        url: '/api/user/multi-Inviter/' + id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
