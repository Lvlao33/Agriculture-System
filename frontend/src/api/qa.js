import { request } from '../utils/request'

// 获取问答列表（分页，按最后更新时间排序）
export function getQuestionsList(params) {
  return request({
    method: 'get',
    url: '/api/qa/questions',
    params: {
      pageNum: params.pageNum,
      pageSize: params.pageSize,
      mine: params.mine,
      keyword: params.keyword
    },
    headers: {
      'Authorization': localStorage.getItem('token')
    }
  })
}

// 获取问题详情
export function getQuestionDetail(params) {
  return request({
    method: 'get',
    url: `/api/qa/question/${params.id}`,
    headers: {
      'Authorization': localStorage.getItem('token')
    }
  })
}

// 获取回答列表
export function getAnswersList(params) {
  return request({
    method: 'get',
    url: `/api/qa/answers`,
    params: {
      questionId: params.questionId
    },
    headers: {
      'Authorization': localStorage.getItem('token')
    }
  })
}

// 提交问题（支持附件：图片/视频）
export function submitQuestion(formData) {
  return request({
    method: 'post',
    url: '/api/qa/questions',
    data: formData,
    headers: {
      'Authorization': localStorage.getItem('token'),
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 提交回答（专家使用）
export function submitAnswer(data) {
  return request({
    method: 'post',
    url: '/qa/answer',
    data: data,
    headers: {
      'Authorization': localStorage.getItem('token')
    }
  })
}

// 获取专家列表（用于提问时选择）
export function getExpertList() {
  return request({
    method: 'get',
    url: '/api/qa/experts',
    headers: {
      'Authorization': localStorage.getItem('token')
    }
  })
}

