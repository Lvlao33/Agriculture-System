// ����axiosʵ�������û���URL
// ������������baseURL��axios��ֱ����Զ�̷������������󣬲��ᾭ�������������Ĵ���
import axios from 'axios'

export function request(config) {
    // ����axios��ʵ��
    const instance = axios.create({
        // baseURL: 'http://3958b99l28.zicp.vip',
        //baseURL: 'http://119.3.180.117:9090',
        // baseURL: process.env.VUE_APP_Address,
        timeout: 100000,
        // ����Ĭ������ͷ��ȷ��ʹ��UTF-8����
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    })
    // axios.interceptors  ȫ������
    // ��������
    instance.interceptors.request.use(config => { // ��������
        // ȷ������ͷ����UTF-8����
        if (config.headers) {
            config.headers['Content-Type'] = config.headers['Content-Type'] || 'application/json;charset=UTF-8';
        }
        return config // �����ص���Ҫԭ�ⲻ���ķ���
    }, err => { })
    // ��Ӧ����
    instance.interceptors.response.use(res => {
        return res.data
    }, err => {
        console.error('请求失败:', err);
        // 如果有响应，返回响应数据；否则抛出错误
        if (err.response) {
            console.error('响应状态:', err.response.status);
            console.error('响应数据:', err.response.data);
            // 返回错误响应数据，让调用方可以处理
            return Promise.reject(err.response.data || err);
        }
        // 网络错误或其他错误
        return Promise.reject(err);
    })
    // 3.������������������
    return instance(config)

}
