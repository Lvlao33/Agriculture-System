import { request } from '../utils/request'

// 根据贷款ID获取文件列表
export function getLoanFiles(loanId) {
  return request({
    method: 'get',
    url: `api/finance/loans/${loanId}/files`,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

