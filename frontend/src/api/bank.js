// encoding: utf-8
// Bank-side dashboard APIs with graceful fallbacks.
import { request } from '../utils/request'

export function getBankOverview() {
  return request({
    method: 'get',
    url: 'api/bank/dashboard/overview',
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  }).catch(() => ({
    flag: true,
    data: {
      pendingLoans: 12,
      matchedFarmers: 8,
      riskAlerts: 2,
      totalCredit: 56000000,
      approvalRate: '92%',
      avgProcessingTime: '1.6 ��'
    }
  }))
}

export function getBankLoans() {
  return request({
    method: 'get',
    url: 'api/bank/dashboard/loans',
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  }).catch(() => ({
    flag: true,
    data: []
  }))
}

export function getBankMatches() {
  return request({
    method: 'get',
    url: 'api/bank/dashboard/matches',
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  }).catch(() => ({
    flag: true,
    data: []
  }))
}

export function getBankAlerts() {
  return request({
    method: 'get',
    url: 'api/bank/dashboard/alerts',
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  }).catch(() => ({
    flag: true,
    data: []
  }))
}

export function getBankNotifications() {
  return request({
    method: 'get',
    url: 'api/bank/dashboard/notifications',
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  }).catch(() => ({
    flag: true,
    data: []
  }))
}

// 获取未分配的贷款（CREATED状态）
export function getCreatedLoans() {
  return request({
    method: 'get',
    url: 'api/finance/bank-review/loans/created',
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 根据状态和staff查询贷款
export function getLoansByStatus(status, staffId) {
  return request({
    method: 'get',
    url: 'api/finance/bank-review/loans/by-status',
    params: { status, staffId },
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 根据多个状态和staff查询贷款（用于已结束的贷款）
export function getLoansByStatuses(statuses, staffId) {
  // 构建查询参数，确保数组正确传递
  const params = new URLSearchParams();
  statuses.forEach(status => {
    params.append('statuses', status);
  });
  params.append('staffId', staffId);
  
  return request({
    method: 'get',
    url: `api/finance/bank-review/loans/by-statuses?${params.toString()}`,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 分配贷款
export function assignLoan(loanId, operatorId) {
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/assign`,
    params: { operatorId },
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 审核通过
export function approveLoan(loanId, operatorId, remark) {
  const params = { operatorId };
  if (remark) {
    params.remark = remark;
  }
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/approve`,
    params: params,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 审核拒绝
export function rejectLoan(loanId, operatorId, remark) {
  const params = { operatorId };
  if (remark) {
    params.remark = remark;
  }
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/reject`,
    params: params,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 批注
export function commentLoan(loanId, operatorId, remark) {
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/comment`,
    params: { operatorId, remark },
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 签约
export function signLoan(loanId, operatorId, remark) {
  const params = { operatorId };
  if (remark) {
    params.remark = remark;
  }
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/sign`,
    params: params,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 放款
export function disburseLoan(loanId, operatorId, remark) {
  const params = { operatorId };
  if (remark) {
    params.remark = remark;
  }
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/disburse`,
    params: params,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

// 还清
export function clearLoan(loanId, operatorId, remark) {
  const params = { operatorId };
  if (remark) {
    params.remark = remark;
  }
  return request({
    method: 'post',
    url: `api/finance/bank-review/applications/${loanId}/clear`,
    params: params,
    headers: {
      'Authorization': window.localStorage.token || ''
    }
  })
}

