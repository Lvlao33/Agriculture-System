import { request } from '../utils/request'

// 提交预约
export function addAppointment(params) {
    return request({
        method: 'post',
        url: 'api/appointment/add',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 获取预约列表
export function getAppointmentList(params) {
    return request({
        method: 'get',
        url: '/appointment/list',
        params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 获取预约详情
export function getAppointmentDetail(id) {
    return request({
        method: 'get',
        url: `/appointment/detail/${id}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 删除预约
export function deleteAppointment(id) {
    return request({
        method: 'delete',
        url: `/appointment/delete/${id}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}