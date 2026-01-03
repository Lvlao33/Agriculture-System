import { request } from '../utils/request'

// ����֪ʶ��ר�ҷ�����
export function addKnowledge(params) {
    return request({
        method: 'post',
        url: '/api/knowledge',
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ��ҳ��ѯ����֪ʶ��ũҵ֪ʶҳ�б���
export function selectKnowledgesPage(params) {
    return request({
        method: 'get',
        url: '/api/knowledge/' + params.pageNum,
        // ���䴫�� pageSize������ǰ�˸���ÿҳ������˲���Ч
        params: {
            pageSize: params.pageSize
        },
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ����id�޸�֪ʶ
export function updateKnowledgeById(params) {
    return request({
        method: 'put',
        url: '/api/knowledge/' + params.knowledgeId,
        data: params,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ����idɾ��֪ʶ
export function deleteKnowledgeById(params) {
    return request({
        method: 'delete',
        url: '/api/knowledge/' + params.knowledgeId,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ����id��ѯ֪ʶ��Ϣ (����)
export function selectKnowledgeById(params) {
    return request({
        method: 'get',
        url: '/api/knowledge/selectById/' + params.knowledgeId,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ���ݵ�¼�û���ѯ֪ʶ (�ҵ�֪ʶ)
export function selectKnowledgeByUsername(params) {
    return request({
        method: 'get',
        url: '/api/knowledge/selectByUsername',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ����֪ʶid��ѯ����
export function selectComment(params) {
    return request({
        method: 'get',
        url: `/api/knowledge/selectByKnowledge/${params.knowledgeId}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ��������
export function addComment(params) {
    return request({
        method: 'post',
        url: `/api/knowledge/addByKnowledge/${params.knowledgeId}/${params.content}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}