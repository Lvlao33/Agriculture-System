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
<<<<<<< HEAD
=======

// 鑾峰彇鐭ヨ瘑鍒楄〃锛堝垎椤碉級
export function getKnowledgeList(params) {
  return request({
      method: 'get',
      url: '/knowledge/' + (params.pageNum || 1),
      headers: {
          'Authorization': window.localStorage.token,
      },
  })
}
>>>>>>> 37ae0d5f64f366a622603654ebc7ad944ef7d1a0
