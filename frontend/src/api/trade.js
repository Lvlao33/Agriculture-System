import { request } from '../utils/request'

// ==================== 商品相关API ====================

/**
 * 获取商品列表
 * @param {Object} params - 查询参数
 * @param {Number} params.pageNum - 页码（可选，后端暂不支持分页，但保留参数）
 * @param {String} params.keys - 搜索关键词（可选）
 * @param {Number} params.sellerId - 卖家ID（可选）
 */
export function getProducts(params = {}) {
  const queryParams = {};
  if (params.keys) {
    queryParams.keyword = params.keys;
  }
  if (params.sellerId) {
    queryParams.sellerId = params.sellerId;
  }
  
  return request({
    method: 'get',
    url: '/api/trade/products',
    params: queryParams,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 获取商品详情
 * @param {Number} productId - 商品ID
 */
export function getProductDetail(productId) {
  return request({
    method: 'get',
    url: `/api/trade/products/${productId}`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 创建商品
 * @param {Object} product - 商品信息
 */
export function createProduct(product) {
  return request({
    method: 'post',
    url: '/api/trade/products',
    data: product,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 更新商品
 * @param {Number} productId - 商品ID
 * @param {Object} product - 商品信息
 */
export function updateProduct(productId, product) {
  return request({
    method: 'put',
    url: `/api/trade/products/${productId}`,
    data: product,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 删除商品
 * @param {Number} productId - 商品ID
 */
export function deleteProduct(productId) {
  return request({
    method: 'delete',
    url: `/api/trade/products/${productId}`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

// ==================== 采购需求相关API ====================

/**
 * 获取采购需求列表
 * @param {Object} params - 查询参数
 * @param {Number} params.pageNum - 页码（可选，后端暂不支持分页，但保留参数）
 * @param {String} params.keys - 搜索关键词（可选）
 */
export function getDemands(params = {}) {
  const queryParams = {};
  if (params.keys) {
    queryParams.keyword = params.keys;
  }
  if (params.userId) {
    queryParams.userId = params.userId;
  }

  return request({
    method: 'get',
    url: '/api/trade/demands',
    params: queryParams,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 获取采购需求详情
 * @param {Number} demandId - 需求ID
 */
export function getDemandDetail(demandId) {
  return request({
    method: 'get',
    url: `/api/trade/demands/${demandId}`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 删除采购需求
 * @param {Number} demandId - 需求ID
 */
export function deleteDemand(demandId) {
  return request({
    method: 'delete',
    url: `/api/trade/demands/${demandId}`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 创建采购需求
 * @param {Object} demand - 需求信息
 */
export function createDemand(demand) {
  return request({
    method: 'post',
    url: '/api/trade/demands',
    data: demand,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

// ==================== 订单相关API ====================

/**
 * 获取订单列表
 * @param {Object} params - 查询参数
 * @param {Number} params.userId - 用户ID（可选）
 * @param {String} params.status - 订单状态（可选）
 * @param {Number} params.pageNum - 页码（可选，后端暂不支持分页，但保留参数）
 */
export function getOrders(params = {}) {
  const queryParams = {};
  if (params.userId) {
    queryParams.userId = params.userId;
  }
  if (params.status) {
    queryParams.status = params.status;
  }
  
  return request({
    method: 'get',
    url: '/api/trade/orders',
    params: queryParams,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 获取订单详情
 * @param {Number} orderId - 订单ID
 */
export function getOrderDetail(orderId) {
  return request({
    method: 'get',
    url: `/api/trade/orders/${orderId}`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 创建订单
 * @param {Object} order - 订单信息
 */
export function createOrder(order) {
  return request({
    method: 'post',
    url: '/api/trade/orders',
    data: order,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 更新订单
 * @param {Number} orderId - 订单ID
 * @param {Object} order - 订单信息
 */
export function updateOrder(orderId, order) {
  return request({
    method: 'put',
    url: `/api/trade/orders/${orderId}`,
    data: order,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 删除订单
 * @param {Number} orderId - 订单ID
 */
export function deleteOrder(orderId) {
  return request({
    method: 'delete',
    url: `/api/trade/orders/${orderId}`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 从购物车结算创建订单
 * @param {Object} params - 结算参数
 * @param {Array<Number>} params.productIds - 商品ID列表
 * @param {String} params.shippingAddress - 收货地址
 * @param {String} params.receiverName - 收货人姓名
 * @param {String} params.receiverPhone - 收货人电话
 */
export function checkout(params) {
  return request({
    method: 'post',
    url: '/api/trade/orders/checkout',
    data: params,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 订单付款，将待付款订单移入待发货
 * @param {Number} orderId - 订单ID
 */
export function payOrder(orderId) {
  return request({
    method: 'put',
    url: `/api/trade/orders/${orderId}/pay`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 确认发货，将待发货订单移入待收货
 * @param {Number} orderId - 订单ID
 */
export function shipOrder(orderId) {
  return request({
    method: 'put',
    url: `/api/trade/orders/${orderId}/ship`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 确认收货，将订单状态置为待评价
 * @param {Number} orderId - 订单ID
 */
export function confirmOrderReceive(orderId) {
  return request({
    method: 'put',
    url: `/api/trade/orders/${orderId}/confirm`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

/**
 * 完成评价，将订单移入退款/售后
 * @param {Number} orderId - 订单ID
 */
export function completeReview(orderId) {
  return request({
    method: 'put',
    url: `/api/trade/orders/${orderId}/complete-review`,
    headers: {
      'Authorization': localStorage.getItem('token') || '',
    },
  })
}

// ==================== 兼容旧API的函数（用于平滑迁移） ====================

/**
 * 兼容旧的selectGoodsPage函数
 * 将旧API调用转换为新API
 */
export function selectGoodsPage(params) {
  return getProducts({
    pageNum: params.pageNum,
    keys: params.keys || '',
  }).then(res => {
    // 转换数据格式并补充前端需要的字段（picture、title/content、orderId）
    if (res.flag && res.data) {
      const rawList = Array.isArray(res.data)
        ? res.data
        : (res.data.list || []);

      const mapped = rawList.map(p => ({
        // 兼容前端字段
        orderId: p.id,
        id: p.id,
        title: p.name,
        name: p.name,
        content: p.description,
        description: p.description,
        price: p.price,
        picture: p.imageUrl,
        image: p.imageUrl,
        category: p.category,
        stock: p.stock,
        sellerId: p.sellerId,
        isAvailable: p.isAvailable,
      }));

      return {
        flag: true,
        data: {
          list: mapped,
          total: mapped.length
        }
      };
    }
    return res;
  });
}

/**
 * 兼容旧的selectNeedsPage函数
 * 将旧API调用转换为新API
 * 注意：后端DemandController返回的data是数组，不是{list, total}格式
 */
export function selectNeedsPage(params) {
  return getDemands({
    pageNum: params.pageNum,
    keys: params.keys || '',
    userId: params.userId,
  }).then(res => {
    // 转换数据格式以兼容旧代码
    if (res.flag && res.data) {
      // 后端返回的data直接是数组或 {list,total}
      const rawList = Array.isArray(res.data)
        ? res.data
        : (res.data.list || []);
      const mapped = rawList.map(d => ({
        orderId: d.id,
        id: d.id,
        title: d.title,
        name: d.title,
        content: d.description,
        description: d.description,
        price: d.price, // 需求无价格，保持占位
        picture: null,
        image: null,
        category: d.category,
        quantity: d.quantity,
        unit: d.unit,
        userId: d.userId,
        status: d.status,
        createTime: d.createTime,
      }));
      return {
        flag: true,
        data: {
          list: mapped,
          total: mapped.length
        }
      };
    }
    return res;
  });
}

/**
 * 兼容旧的selectAllPage函数
 * 将旧API调用转换为新API
 */
export function selectAllPage(params) {
  return getOrders({
    pageNum: params.pageNum,
    keys: params.keys || '',
  }).then(res => {
    // 转换数据格式以兼容旧代码
    if (res.flag && res.data) {
      // 如果返回的是list数组，直接使用
      if (Array.isArray(res.data)) {
        return {
          flag: true,
          data: {
            list: res.data,
            total: res.data.length
          }
        };
      }
      // 如果返回的是包含list的对象，直接使用
      if (res.data.list) {
        return res;
      }
      // 否则包装成list格式
      return {
        flag: true,
        data: {
          list: [res.data],
          total: 1
        }
      };
    }
    return res;
  });
}

/**
 * 兼容旧的selectBuyByUserName函数
 * 获取当前用户的购买订单
 */
export function selectBuyByUserName(params) {
  // 从token或store中获取userId，这里需要根据实际情况调整
  // 暂时返回所有订单，前端可以过滤
  return getOrders({}).then(res => {
    if (res.flag && res.data) {
      let orders = Array.isArray(res.data) ? res.data : (res.data.list || []);
      // 这里应该根据实际userId过滤，暂时返回所有
      return {
        flag: true,
        data: orders
      };
    }
    return res;
  });
}

/**
 * 兼容旧的deleteOrderById函数
 * 删除订单
 */
export function deleteOrderById(params) {
  const orderId = params.order_id || params.id || params;
  return deleteOrder(orderId);
}

// ==================== 商品评论相关API ====================

/**
 * 获取商品评论列表
 * @param {Number} productId - 商品ID
 */
export function getProductReviews(productId) {
  return request({
    method: 'get',
    url: `/api/trade/products/${productId}/reviews`,
    // 不需要手动设置 Authorization，request.js 的拦截器会自动添加
  })
}

/**
 * 创建商品评论
 * @param {Number} productId - 商品ID
 * @param {Object} review - 评论信息
 * @param {String} review.content - 评论内容
 * @param {Number} review.rating - 评分（1-5分，可选）
 */
export function createProductReview(productId, review) {
  return request({
    method: 'post',
    url: `/api/trade/products/${productId}/reviews`,
    data: review,
    // 不需要手动设置 Authorization，request.js 的拦截器会自动添加
  })
}

/**
 * 删除商品评论
 * @param {Number} productId - 商品ID
 * @param {Number} reviewId - 评论ID
 */
export function deleteProductReview(productId, reviewId) {
  return request({
    method: 'delete',
    url: `/api/trade/products/${productId}/reviews/${reviewId}`,
    // 不需要手动设置 Authorization，request.js 的拦截器会自动添加
  })
}

