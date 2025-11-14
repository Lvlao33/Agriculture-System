import { request } from '../utils/request'

// 查询专家信息
export function getExpertInfo(params) {
  return request({
      method: 'get',
      url: '/user/searchexpert',
      data: params,
      headers: {
          'Authorization': window.localStorage.token,
      },
  })
}

// 修改专家信息
export function editExpertInfo(params) {
  return request({
      method: 'post',
      url: '/user/addUpdexpert',
      data: params,
      headers: {
          'Authorization': window.localStorage.token,
      },
  })
}

// 获取知识列表（分页）
export function getKnowledgeList(params) {
  return request({
      method: 'get',
      url: '/knowledge/' + (params.pageNum || 1),
      headers: {
          'Authorization': window.localStorage.token,
      },
  })
}