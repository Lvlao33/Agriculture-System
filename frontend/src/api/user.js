import { request } from '../utils/request'

// �һ�����
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

// �û���¼
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

// �û�ע��
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

// �û���¼֮�󣬸����û���չʾ���˻�����Ϣ
export function loginSelectByUsername(params) {
    return request({
        method: 'get',
        url: '/api/user/' + (params.username || params.user_name || ''),
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// �û���¼֮�󣬸����û����޸ĸ��˻�����Ϣ
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

// �����û�����ѯ�û�
export function selectUserByUsername(params) {
    return request({
        method: 'get',
        url: '/api/user/' + params.user_name,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// �����û���ɾ���û�
export function deleteUserByUsername(params) {
    return request({
        method: 'delete',
        url: '/api/user/' + params.user_name,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// �����û����޸��û���Ϣ
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

// ��ҳ��ѯ�����û�
export function selectAllUserPage(params) {
    return request({
        method: 'get',
        url: '/api/user/search/' + params.pageNum,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// �޸��û�����
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
