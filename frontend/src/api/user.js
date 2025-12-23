import { request } from '../utils/request'

// 忘记密码
export function forgetPassword(params) {
    return request({
        method: 'post',
        url: '/api/user/forgetPassword',
        data: params,
    })
}

// 登录
export function userLogin(params) {
    return request({
        method: 'post',
        url: '/api/auth/login',
        data: params,
    })
}

// 注册
export function userRegister(params) {
    return request({
        method: 'post',
        url: '/api/auth/register',
        data: params,
    })
}

// 根据用户名查询登录信息
export function loginSelectByUsername(params = {}) {
    // 优先走后端的 loginSelectByUsername 接口，后端会使用 Authorization header 中的 JWT
    return request({
        method: 'get',
        url: '/api/user/loginSelectByUsername',
        params: {
            username: params.username || params.user_name || undefined,
        },
    })
}

// 根据用户名修改登录信息
export function loginUpdateByUsername(params) {
    return request({
        method: 'post',
        url: '/api/user/loginUpdateByUsername',
        data: params,
    })
}

// 根据用户名查询用户信息
export function selectUserByUsername(params) {
    return request({
        method: 'get',
        url: '/api/user/' + params.user_name,
    })
}

// 根据用户名删除用户
export function deleteUserByUsername(params) {
    return request({
        method: 'delete',
        url: '/api/user/' + params.user_name,
    })
}

// 根据用户名修改用户信息
export function updateUserByUsername(params) {
    return request({
        method: 'put',
        url: '/api/user/' + params.user_name,
        data: params,
    })

}
// 查询所有用户信息
export function selectAllUserPage(params) {
    return request({
        method: 'get',
        url: '/api/user/search/' + params.pageNum,
    })

}
// 修改用户密码
export function updateUserPassword(params) {
    return request({
        method: 'post',
        url: '/api/user/loginUpdatePassword',
        data: params,
    })

}

// 根据userID查询用户
export function searchUserById(id) {
    return request({
        method: 'get',
        url: '/api/user/multi-Inviter/' + id,
    })
}
