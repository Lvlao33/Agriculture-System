import { request } from '../utils/request'

// // 添加贷款产品
// export function addFinance(params) {
//     return request({
//         method: 'post',
//         url: '/finance/add',
//         data: params,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 添加联合贷款
// export function addFinanceMulti(params) {
//     return request({
//         method: 'post',
//         url: '/finance/addFinanceMulti',
//         data: params,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 查询银行
// export function selectBank() {
//     return request({
//         method: 'get',
//         url: '/finance/selectbank',
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// 查询意向
export function selectIntention() {
    return request({
        method: 'get',
        url: '/api/finance/matching/intention',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 插入意向
export function insertIntention(params) {
    return request({
        method: 'post',
        url: '/api/finance/matching/intention',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 更新意向
export function updateIntention(params) {
    return request({
        method: 'post',
        url: '/api/finance/matching/intention',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 删除意向
export function deleteIntention() {
    return request({
        method: 'delete',
        url: '/api/finance/matching/intention',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// 查找推荐人
export function selectRecommned() {
    return request({
        method: 'get',
        url: '/api/finance/matching/recommend',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 联合贷款推荐产品
export function selectComboRecommend(partnerId) {
    return request({
        method: 'get',
        url: '/api/finance/matching/recommend-combo',
        params: { partnerId },
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// // 查找同个银行的贷款证人
// export function selectFinaceUser(bank_id) {
//     return request({
//         method: 'get',
//         url: '/finance/selectFinaceUser/' + bank_id.bank_id,
//         data: bank_id,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 查找同个银行的两个贷款人
// export function selectTwoUser(bank_id) {
//     return request({
//         method: 'get',
//         url: '/finance/selectTwoUser/' + bank_id.bank_id,
//         data: bank_id,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 根据名字搜索
// export function selectByName() {
//     return request({
//         method: 'get',
//         url: '/finance/selectByName/',
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 根据id更新贷款产品
// export function updateById(params) {
//     return request({
//         method: 'put',
//         url: '/finance/update/',
//         data:params,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 根据id删除贷款产品
// export function deleteById(financeId) {
//     return request({
//         method: 'delete',
//         url: '/finance/delete/'+financeId.financeId,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }
// // 根据id查询贷款产品信息
// export function selectFinaceById(params) {
//     return request({
//         method: 'get',
//         url: 'finance/selectById/' + params.id,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }

// 获得贷款产品列表
export function getLoanProducts() {
    return request({
        method: 'get',
        url: '/api/finance/loan-products',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 获得贷款详情
export function getLoanProductDetail(productId) {
    return request({
        method: 'get',
        url: `/api/finance/loan-products/${productId}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}


// 申请贷款(multipart/form-data)
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

// 获得贷款申请列表
export function getLoanList(userId) {
    const config = {
        method: 'get',
        url: '/api/finance/loans',
        headers: {
            'Authorization': window.localStorage.token,
        },
    }
    if (userId) {
        config.params = { userId }
    }
    return request(config)
}

// 根据贷款ID获取已上传文件
export function getLoanFiles(loanId) {
    return request({
        method: 'get',
        url: `/api/finance/loans/${loanId}/files`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 更新贷款申请
export function updateLoan(loanId, payload) {
    return request({
        method: 'post',
        url: `/api/finance/loans/${loanId}/update`,
        data: payload,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 补充贷款文件
export function uploadLoanFile(loanId, formData) {
    return request({
        method: 'post',
        url: `/api/finance/loans/${loanId}/upload`,
        data: formData,
        headers: {
            'Authorization': window.localStorage.token,
            // Content-Type is handled automatically for FormData
        },
    })
}

// 授权用户
// export function toAuthorizationUser(params) {
//     return request({
//         method: 'post',
//         url: '/finance/toAuthorizationUser',
//         data: params,
//         headers: {
//             'Authorization': window.localStorage.token,
//         },
//     })
// }

// 获取贷款用户状态列表
export function getLoanUserStatuses(loanId) {
    return request({
        method: 'get',
        url: `/api/finance/loans/${loanId}/user-statuses`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 获取贷款处理记录列表
export function getLoanRecords(loanId) {
    return request({
        method: 'get',
        url: `/api/finance/loans/${loanId}/records`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// 更新联合贷款人信息
export function updateApplicantInfo(data) {
    return request({
        method: 'post',
        url: '/api/finance/loans/applicant/info',
        data: data,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
