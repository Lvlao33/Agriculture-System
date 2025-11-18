import { request } from '../utils/request'

// ���ӵ��˴���
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
// ������ϴ���
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
// ���в�ѯ�ӿ�
export function selectBank() {
    return request({
        method: 'get',
        url: '/finance/selectbank',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// ��ѯ��������
export function selectIntention() {
    return request({
        method: 'get',
        url: '/finance/selectIntentionByName',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// ������������
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
// �޸���������
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
// ɾ����������
export function deleteIntention() {
    return request({
        method: 'delete',
        url: '/finance/deleteIntentionByName',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// ��ѯ�Ƽ���
export function selectRecommned() {
    return request({
        method: 'get',
        url: '/finance/selectRecommendByName',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ��ѯ���˴�������Ϣ
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
// ��ѯ��ϴ�������Ϣ
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
// ��ѯ�����������
export function selectByName() {
    return request({
        method: 'get',
        url: '/finance/selectByName/',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// �޸Ĵ����������
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
// ɾ����������
export function deleteById(financeId) {
    return request({
        method: 'delete',
        url: '/finance/delete/'+financeId.financeId,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
// ����id��ѯ������Ϣ����Ϣ����
export function selectFinaceById(params) {
    return request({
        method: 'get',
        url: 'finance/selectById/' + params.id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ��ȡ�����Ʒ�б�
export function getLoanProducts() {
    return request({
        method: 'get',
        url: '/api/finance/loan-products',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ��ȡ�����Ʒ����
export function getLoanProductDetail(productId) {
    return request({
        method: 'get',
        url: `/api/finance/loan-products/${productId}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// // �������
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

// �������ϴ����� (multipart/form-data)
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

// �ϴ������ļ�
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

// ��ȡ���������б�
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
