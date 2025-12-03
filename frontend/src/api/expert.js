// encoding: utf-8
// Expert API helpers. Replace后端地址时保持 UTF-8 输出

// 专家侧接口
import { request } from '../utils/request'

const authHeader = () => ({ 'Authorization': window.localStorage.token || '' })

export function getExpertOverview() {
  return request({
    method: 'get',
    url: 'api/expert/dashboard/overview',
    headers: authHeader()
  })
}

export function getExpertQuestions() {
  return request({
    method: 'get',
    url: 'api/expert/dashboard/questions',
    headers: authHeader()
  })
}

export function getExpertAppointments() {
  return request({
    method: 'get',
    url: 'api/expert/dashboard/appointments',
    headers: authHeader()
  })
}

export function getExpertKnowledge() {
  return request({
    method: 'get',
    url: 'api/expert/dashboard/knowledge',
    headers: authHeader()
  })
}

export function getExpertNotifications() {
  return request({
    method: 'get',
    url: 'api/expert/dashboard/notifications',
    headers: authHeader()
  })
}

// 个人资料（可根据后端完成度开启真实接口）
export function getProfile() {
  return request({
    method: 'get',
    url: '/api/expert/profile',
    headers: authHeader()
  }).catch(() => ({ data: { name: '示例专家', title: '农业技术顾问' } }))
}

export function updateProfile(payload) {
  return request({
    method: 'put',
    url: '/api/expert/profile',
    data: payload,
    headers: authHeader()
  })
}

export function getAppointments(params) {
  return request({
    method: 'get',
    url: '/api/expert/appointments',
    params,
    headers: authHeader()
  })
}

export function acceptAppointment(id) {
  return request({
    method: 'post',
    url: `/api/expert/appointments/${id}/accept`,
    headers: authHeader()
  })
}

export function getKnowledges(params) {
  return request({
    method: 'get',
    url: '/api/expert/knowledges',
    params,
    headers: authHeader()
  })
}

export function publishKnowledge(payload) {
  return request({
    method: 'post',
    url: '/api/expert/knowledges',
    data: payload,
    headers: authHeader()
  })
}

export function getExpertInfo(params) {
  return request({
    method: 'get',
    url: '/user/searchexpert',
    data: params,
    headers: authHeader()
  })
}

export function editExpertInfo(params) {
  return request({
    method: 'post',
    url: '/user/addUpdexpert',
    data: params,
    headers: authHeader()
  })
}

export function getKnowledgeList(params) {
  return request({
    method: 'get',
    url: '/knowledge/' + (params.pageNum || 1),
    headers: authHeader()
  })
}
