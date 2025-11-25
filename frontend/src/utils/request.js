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
        console.log(err);
    })
    // 3.������������������
    return instance(config)

}
