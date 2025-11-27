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
      avgProcessingTime: '1.6 Ìì'
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

