import { request } from '../utils/request'

// ==================== 收藏夹相关API ====================

/**
 * 添加到收藏夹
 * @param {Object} params - 参数对象
 * @param {Number} params.order_id - 商品ID
 */
export function addOrderToCollect(params) {
    return request({
        method: 'post',
        url: 'cart/addcollect/' + params.order_id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

/**
 * 展示收藏夹列表
 */
export function showcollect(params) {
    return request({
        method: 'get',
        url: 'cart/showcollect',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

/**
 * 收藏夹删除商品
 * @param {Object} params - 参数对象
 * @param {Number} params.order_id - 商品ID
 */
export function collectdelete(params) {
    return request({
        method: 'delete',
        url: 'cart/collectdelete/' + params.order_id,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

// ==================== 购物车相关API ====================

/**
 * 添加商品到购物车
 * @param {Object} params - 参数对象
 * @param {Number} params.order_id - 商品ID（productId）
 * @param {Number} params.quantity - 数量（可选，默认为1）
 */
export function addOrderToCart(params) {
    const productId = params.order_id || params.productId;
    const quantity = params.quantity || 1;
    return request({
        method: 'post',
        url: `cart/add/${productId}?quantity=${quantity}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

/**
 * 购物车删除商品
 * @param {Object} params - 参数对象
 * @param {Number} params.order_id - 商品ID（productId）
 */
export function cartDeleteOrder(params) {
    const productId = params.order_id || params.productId;
    return request({
        method: 'delete',
        url: 'cart/delete/' + productId,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

/**
 * 展示购物车列表（包含商品详情）
 */
export function cartShow(params) {
    return request({
        method: 'get',
        url: 'cart/show',
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}

/**
 * 更新购物车商品数量
 * @param {Object} params - 参数对象
 * @param {Number} params.id - 商品ID（productId）
 * @param {Number} params.count - 新的数量
 */
export function updateGoodsCount(params) {
    const productId = params.id || params.productId;
    const count = params.count || params.quantity || 1;
    return request({
        method: 'put',
        url: `/cart/update/${productId}/${count}`,
        headers: {
            'Authorization': window.localStorage.token,
        },
    })
}
