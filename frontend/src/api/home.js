import { request } from '../utils/request'

/**
 * 获取首页整合数据
 * 包括：新闻、金融产品、专家团队、专家问答、农业知识、助农电商
 */
export function getHomeData() {
    return request({
        url: '/api/home/data',
        method: 'get'
    })
}
