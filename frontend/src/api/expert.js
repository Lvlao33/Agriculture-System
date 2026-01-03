// encoding: utf-8
import { request } from '../utils/request'

const authHeader = () => ({ 'Authorization': window.localStorage.token || '' })

// ==========================================
// 1. Dashboard 仪表盘接口
// ==========================================

export function getExpertOverview() {
  return request({
    method: 'get',
    url: '/api/expert/dashboard/overview',
    headers: authHeader()
  })
}

export function getExpertQuestions() {
  return request({
    method: 'get',
    url: '/api/expert/dashboard/questions', // 修复：添加开头的 /
    headers: authHeader()
  })
}

export function getExpertAppointments() {
  return request({
    method: 'get',
    url: '/api/expert/dashboard/appointments', // 修复：添加开头的 /
    headers: authHeader()
  })
}

export function getExpertKnowledge() {
  return request({
    method: 'get',
    url: '/api/expert/dashboard/knowledge', // 修复：添加开头的 /
    headers: authHeader()
  })
}

export function getExpertNotifications() {
  return request({
    method: 'get',
    url: '/api/expert/dashboard/notifications', // 修复：添加开头的 /
    headers: authHeader()
  })
}

// ==========================================
// 2. 专家个人信息 & 预约管理
// ==========================================

// 个人资料
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

// 预约列表
export function getAppointments(params) {
  return request({
    method: 'get',
    url: '/api/expert/appointments',
    params,
    headers: authHeader()
  })
}

// 接受预约
export function acceptAppointment(id) {
  return request({
    method: 'post',
    url: `/api/expert/appointments/${id}/accept`,
    headers: authHeader()
  })
}

// ==========================================
// 3. 知识相关 (修正为调用 KnowledgeController)
// ==========================================

// 获取专家的知识列表 (旧名 getKnowledges，重定向到真实的 selectByUsername)
export function getKnowledges(params) {
  return request({
    method: 'get',
    url: '/api/knowledge/selectByUsername', // 修正：指向真实的查询接口
    params,
    headers: authHeader()
  })
}

// 发布知识 (修正：指向 KnowledgeController 的 create)
export function publishKnowledge(payload) {
  return request({
    method: 'post',
    url: '/api/knowledge', // 修正：指向真实的发布接口
    data: payload,
    headers: authHeader()
  })
}

// 获取知识分页列表 (ExpertKnowledgeList.vue 使用)
export function getKnowledgeList(params) {
  return request({
    method: 'get',
    url: '/api/knowledge/' + (params.pageNum || 1),
    params: {
        pageSize: params.pageSize || 10
    }
  })
}

// ==========================================
// 4. 公共查询 (ExpertGuide.vue 使用)
// ==========================================

// 专家列表查询
export function selectExperts(params) {
  return request({
    method: 'get',
    url: '/api/expert/infoList',
    params: params
  })
}

// ==========================================
// 5. 历史遗留接口 (建议确认后端是否有 UserController)
// ==========================================

export function getExpertInfo(params) {
  return request({
    method: 'get',
    url: '/user/searchexpert', // 注意：请确认后端是否有此接口，否则会 404
    data: params,
    headers: authHeader()
  })
}

export function editExpertInfo(params) {
  return request({
    method: 'post',
    url: '/user/addUpdexpert', // 注意：请确认后端是否有此接口
    data: params,
    headers: authHeader()
  })
}