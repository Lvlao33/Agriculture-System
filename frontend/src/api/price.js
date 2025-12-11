import { request } from '../utils/request'

// º€∏Ò‘§≤‚
export function getPriceForecast(params) {
  const { commodity, horizon = 7 } = params || {}
  return request({
    method: 'get',
    url: `/price/forecast`,
    params: { commodity, horizon },
    headers: {
      Authorization: window.localStorage.token
    }
  })
}

