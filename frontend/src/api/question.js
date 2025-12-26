import { request } from '../utils/request'

// �����û�����ѯ�ʴ�  kind����ͨ�û���questioner��ר�ң�expert
export function selectQuestionByUser(params) {
  return request({
      method: 'get',
      url: `/api/question/selectByKind/` + params.role,
      headers: {
          'Authorization': window.localStorage.token,
      },
      // data:params ��ͼƬ�ص㣬һ��ɳĮΪһ��һ��
  })
}

// ��ѯ�ʴ����id
export function selectQuestionByUserId(params) {
  return request({
      method: 'get',
      url: `/api/question/selectId/${params.id}`,
      headers: {
          'Authorization': window.localStorage.token,
      },
      // data:params
  })
}

// ����id�޸�
export function reviseQuestionByUserId(params) {
  return request({
      method: 'put',
      url: '/api/question/update',
      headers: {
          'Authorization': window.localStorage.token,
      },
      data:params
  })
}

// ����idɾ��
export function delQuestionByUserId(params) {
  return request({
      method: 'delete',
      url: `/api/question/delete/${params.id}`,
      headers: {
          'Authorization': window.localStorage.token,
      },
      data:params
  })
}

// �����û�����ѯԤԼ  kind����ͨ�û���questioner��ר�ң�expert
export function selectAppointByUser(params) {
  return request({
      method: 'get',
      url: `/api/reserve/selectByKind/${params.type}`,
      headers: {
          'Authorization': window.localStorage.token,
      },
      // data:params
  })
}

// ����id�޸�ԤԼ
export function reviseAppointByUserId(params) {
  return request({
      method: 'put',
      url: '/api/reserve/update',
      headers: {
          'Authorization': window.localStorage.token,
      },
      data:params
  })
}

// ����idɾ��ԤԼ
export function delAppointByUserId(params) {
  return request({
      method: 'delete',
      url: `/api/reserve/delete/${params.id}`,
      headers: {
          'Authorization': window.localStorage.token,
      },
      data:params
  })
}
