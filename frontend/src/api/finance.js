import { request } from '../utils/request'

// 增加单人贷款
export function addFinance(params) {
    return request({
        method: 'post',
        url: '/finance/add',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 增加组合贷款
export function addFinanceMulti(params) {
    return request({
        method: 'post',
        url: '/finance/addFinanceMulti',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 银行查询接口
export function selectBank() {
    return request({
        method: 'get',
        url: '/finance/selectbank',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 查询融资意向
export function selectIntention() {
    return request({
        method: 'get',
        url: '/finance/selectIntentionByName',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 添加融资意向
export function insertIntention(params) {
    return request({
        method: 'post',
        url: '/finance/insertIntentionByName',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 修改融资意向
export function updateIntention(params) {
    return request({
        method: 'put',
        url: '/finance/updateIntentionByName',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 删除融资意向
export function deleteIntention() {
    return request({
        method: 'delete',
        url: '/finance/deleteIntentionByName',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 查询推荐人
export function selectRecommned() {
    return request({
        method: 'get',
        url: '/finance/selectRecommendByName',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 查询单人贷款人信息
export function selectFinaceUser(bank_id) {
    return request({
        method: 'get',
        url: '/finance/selectFinaceUser/' + bank_id.bank_id,
        data: bank_id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 查询组合贷款人信息
export function selectTwoUser(bank_id) {
    return request({
        method: 'get',
        url: '/finance/selectTwoUser/' + bank_id.bank_id,
        data: bank_id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 查询贷款申请情况
export function selectByName() {
    return request({
        method: 'get',
        url: '/finance/selectByName/',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 修改贷款申请情况
export function updateById(params) {
    return request({
        method: 'put',
        url: '/finance/update/',
        data:params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 删除贷款申请
export function deleteById(financeId) {
    return request({
        method: 'delete',
        url: '/finance/delete/'+financeId.financeId,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 根据id查询融资信息，信息回显
export function selectFinaceById(params) {
    return request({
        method: 'get',
        url: 'finance/selectById/' + params.id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 获取贷款产品列表
export function getLoanProducts() {
    return request({
        method: 'get',
        url: '/api/finance/loan-products',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 获取贷款产品详情
export function getLoanProductDetail(productId) {
    return request({
        method: 'get',
        url: `/api/finance/loan-products/${productId}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// // 申请贷款
// export function applyLoan(params) {
//     return request({
//         method: 'post',
//         url: '/api/finance/loans/apply',
//         data: params,
//         headers: {
//             'Authorization': window.localStorage.token,
//             'Content-Type': 'application/json',
//         },
//     })
// }

// 申请贷款并上传资料 (multipart/form-data)
export function applyLoanWithFiles(formData) {
    return request({
        method: 'post',
        url: '/api/finance/loans/apply',
        data: formData,
        headers: {
            'Authorization': window.localStorage.token,
            // Let the browser/axios set Content-Type with boundary
        },
    })
}

// 上传贷款文件
// export function uploadLoanFile(loanId, file) {
//     const formData = new FormData();
//     formData.append('file', file);
//     return request({
//         method: 'post',
//         url: `/api/finance/loans/${loanId}/upload`,
//         data: formData,
//         headers: {
//             'Authorization': window.localStorage.token,
//             // Do not set Content-Type; let axios set the correct boundary
//         },
//     })
// }

// 获取贷款申请列表
export function getLoanList() {
    return request({
        method: 'get',
        url: '/api/finance/loans',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 授权用户
export function toAuthorizationUser(params) {
    return request({
        method: 'post',
        url: '/finance/toAuthorizationUser',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}