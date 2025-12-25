<template>
  <div class="evaluate-container">
    <div class="evaluate-header">
      <h2>订单评价</h2>
      <p class="subtitle">请对您的订单进行评价</p>
    </div>

    <!-- 订单信息 -->
    <div class="order-info-card" v-if="order">
      <div class="order-header">
        <span class="order-id">订单号：{{ order.orderId || order.order_id }}</span>
        <span class="order-date">{{ formatDate(order.createTime || order.create_time) }}</span>
      </div>
      <div class="order-content">
        <div class="product-image">
          <img 
            :src="getImageUrl(order.picture || order.image)" 
            :alt="order.productName || order.title"
            @error="handleImageError"
          />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ order.productName || order.title || '商品名称' }}</h3>
          <p class="product-desc" v-if="order.description || order.content">
            {{ order.description || order.content }}
          </p>
          <div class="product-price">
            <span class="price-label">订单金额：</span>
            <span class="price-value">¥{{ formatPrice(order.totalPrice || order.price || 0) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评价表单 -->
    <div class="evaluate-form-card">
      <el-form :model="evaluateForm" label-width="100px">
        <!-- 商品评分 -->
        <el-form-item label="商品评分" required>
          <div class="rating-section">
            <el-rate
              v-model="evaluateForm.rating"
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              :max="5"
              show-text
              :texts="['很差', '较差', '一般', '满意', '非常满意']"
            />
            <span class="rating-text" v-if="evaluateForm.rating > 0">
              {{ getRatingText(evaluateForm.rating) }}
            </span>
          </div>
        </el-form-item>

        <!-- 服务评分 -->
        <el-form-item label="服务评分" required>
          <div class="rating-section">
            <el-rate
              v-model="evaluateForm.serviceRating"
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              :max="5"
              show-text
              :texts="['很差', '较差', '一般', '满意', '非常满意']"
            />
            <span class="rating-text" v-if="evaluateForm.serviceRating > 0">
              {{ getRatingText(evaluateForm.serviceRating) }}
            </span>
          </div>
        </el-form-item>

        <!-- 物流评分 -->
        <el-form-item label="物流评分" required>
          <div class="rating-section">
            <el-rate
              v-model="evaluateForm.logisticsRating"
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              :max="5"
              show-text
              :texts="['很差', '较差', '一般', '满意', '非常满意']"
            />
            <span class="rating-text" v-if="evaluateForm.logisticsRating > 0">
              {{ getRatingText(evaluateForm.logisticsRating) }}
            </span>
          </div>
        </el-form-item>

        <!-- 评价内容 -->
        <el-form-item label="评价内容" required>
          <el-input
            type="textarea"
            v-model="evaluateForm.content"
            :rows="6"
            placeholder="请详细描述您的购物体验，您的评价对其他用户很有帮助..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>

        <!-- 上传图片（可选） -->
        <el-form-item label="上传图片">
          <el-upload
            class="evaluate-upload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :limit="3"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :file-list="imageList"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <p class="upload-tip">最多上传3张图片，支持jpg、png格式</p>
        </el-form-item>
      </el-form>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <el-button @click="goBack">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitEvaluate"
          :loading="submitting"
          :disabled="!canSubmit"
        >
          提交评价
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getOrderDetail, completeReview, createProductReview } from '../api/trade'
import { selectOrderById } from '../api/order'

export default {
  name: 'OrderEvaluate',
  data() {
    return {
      orderId: null,
      order: null,
      productId: null, // 商品ID，用于创建评论
      submitting: false,
      evaluateForm: {
        rating: 0,
        serviceRating: 0,
        logisticsRating: 0,
        content: '',
        images: []
      },
      imageList: [],
      uploadUrl: this.$store.state.fileUploadRoad + '/file/upload/order',
      uploadHeaders: {
        Authorization: window.localStorage.token
      }
    }
  },
  computed: {
    canSubmit() {
      return this.evaluateForm.rating > 0 &&
             this.evaluateForm.serviceRating > 0 &&
             this.evaluateForm.logisticsRating > 0 &&
             this.evaluateForm.content.trim().length > 0
    }
  },
  created() {
    this.orderId = this.$route.query.orderId
    if (!this.orderId) {
      this.$message.error('订单ID缺失')
      this.goBack()
      return
    }
    this.loadOrderDetail()
  },
  methods: {
    async loadOrderDetail() {
      try {
        // 尝试使用新API，如果失败则使用旧API
        let res
        try {
          res = await getOrderDetail(this.orderId)
        } catch (e) {
          // 如果新API失败，尝试使用旧API
          res = await selectOrderById({ order_id: this.orderId })
        }
        
        if (res && res.flag && res.data) {
          const orderData = res.data
          // 转换订单数据格式
          this.order = {
            id: orderData.id || orderData.orderId || orderData.order_id,
            orderId: orderData.id || orderData.orderId || orderData.order_id,
            order_id: orderData.id || orderData.orderId || orderData.order_id,
            orderNumber: orderData.orderNumber || orderData.order_number,
            title: (orderData.orderItems && orderData.orderItems.length > 0 
              ? orderData.orderItems[0].productName 
              : null) || orderData.title || orderData.productName || '商品名称',
            productName: (orderData.orderItems && orderData.orderItems.length > 0 
              ? orderData.orderItems[0].productName 
              : null) || orderData.title || orderData.productName || '商品名称',
            price: orderData.totalAmount || orderData.total_amount || orderData.price || 0,
            totalPrice: orderData.totalAmount || orderData.total_amount || orderData.price || 0,
            createTime: orderData.createTime || orderData.create_time,
            create_time: orderData.createTime || orderData.create_time,
            picture: orderData.picture || (orderData.orderItems && orderData.orderItems.length > 0 
              ? orderData.orderItems[0].imageUrl : null) || orderData.imageUrl,
            image: orderData.image || orderData.imageUrl || orderData.picture,
            description: orderData.description || orderData.content,
            content: orderData.content || orderData.description,
            orderItems: orderData.orderItems || []
          }
          
          // 获取商品ID（优先从orderItems中获取，支持多种字段名）
          let rawProductId = null
          
          // 详细日志：检查订单项数据
          console.log('=== 订单数据解析 ===')
          console.log('完整订单数据:', JSON.stringify(orderData, null, 2))
          console.log('订单项数组:', orderData.orderItems)
          console.log('订单项数量:', orderData.orderItems ? orderData.orderItems.length : 0)
          
          if (orderData.orderItems && Array.isArray(orderData.orderItems) && orderData.orderItems.length > 0) {
            const firstItem = orderData.orderItems[0]
            console.log('第一个订单项:', JSON.stringify(firstItem, null, 2))
            console.log('第一个订单项的所有字段:', Object.keys(firstItem))
            
            // 尝试多种可能的字段名（驼峰和下划线格式）
            rawProductId = firstItem.productId || firstItem.product_id || firstItem.productId || null
            
            console.log('从订单项中提取的商品ID:', rawProductId)
            
            // 如果还是没有，尝试从订单项的其他可能字段
            if (!rawProductId && firstItem.id) {
              // 注意：firstItem.id 可能是订单项ID，不是商品ID，但作为最后尝试
              console.warn('警告：使用订单项ID作为商品ID（可能不正确）:', firstItem.id)
            }
          } else {
            console.warn('警告：订单项为空或不是数组')
          }
          
          // 如果从 orderItems 中获取失败，尝试从订单数据中获取
          if (!rawProductId) {
            rawProductId = orderData.productId || orderData.product_id || null
            console.log('从订单数据中提取的商品ID:', rawProductId)
          }
          
          // 注意：不要使用 orderData.id 或 orderData.orderId，因为那是订单ID而不是商品ID
          
          // 保存商品ID，用于创建评论（确保是数字类型）
          // 转换为数字类型，如果转换失败则为 null
          this.productId = rawProductId != null ? Number(rawProductId) : null
          if (isNaN(this.productId) || this.productId <= 0) {
            this.productId = null
          }
          
          console.log('最终获取到的商品ID（原始）:', rawProductId)
          console.log('最终获取到的商品ID（转换后）:', this.productId)
          
          if (!this.productId) {
            console.error('❌ 错误：无法从订单数据中获取有效的商品ID')
            console.error('订单项详情:', JSON.stringify(orderData.orderItems, null, 2))
            console.error('订单数据中的可能字段:', {
              productId: orderData.productId,
              product_id: orderData.product_id,
              id: orderData.id,
              orderId: orderData.orderId,
              order_id: orderData.order_id
            })
            this.$message.error('无法获取商品ID，请联系客服')
          } else {
            console.log('✅ 成功获取商品ID:', this.productId)
          }
        } else {
          this.$message.error('获取订单信息失败')
          this.goBack()
        }
      } catch (error) {
        console.error('加载订单详情失败:', error)
        this.$message.error('加载订单详情失败')
        this.goBack()
      }
    },
    async submitEvaluate() {
      if (!this.canSubmit) {
        this.$message.warning('请完成所有必填项')
        return
      }

      if (!this.productId) {
        this.$message.error('无法获取商品ID，无法提交评价')
        return
      }

      // 确保 productId 是有效的数字
      const productId = Number(this.productId)
      if (isNaN(productId) || productId <= 0) {
        this.$message.error('商品ID无效，无法提交评价')
        return
      }

      // 确保评论内容不为空
      const content = this.evaluateForm.content?.trim()
      if (!content || content.length === 0) {
        this.$message.error('评论内容不能为空')
        return
      }

      this.submitting = true
      try {
        // 1. 先创建商品评论
        let reviewCreated = false
        let reviewErrorMsg = null
        try {
          console.log('准备创建评论，商品ID:', productId, '内容:', content, '评分:', this.evaluateForm.rating)
          const reviewRes = await createProductReview(productId, {
            content: content,
            rating: this.evaluateForm.rating || 5 // 使用商品评分，如果没有则默认5分
          })
          if (reviewRes && reviewRes.flag) {
            reviewCreated = true
            console.log('商品评论创建成功')
          } else {
            reviewErrorMsg = reviewRes?.message || '评论创建失败'
            console.warn('商品评论创建失败:', reviewErrorMsg)
            // 如果评论创建失败，仍然继续完成订单评价流程
          }
        } catch (reviewError) {
          console.error('创建商品评论时出错:', reviewError)
          // 提取错误信息
          if (reviewError && typeof reviewError === 'object') {
            reviewErrorMsg = reviewError.message || reviewError.msg || '评论创建失败'
            // 如果是已评论过的错误，给用户更明确的提示
            if (reviewErrorMsg.includes('已经评论') || reviewErrorMsg.includes('已评论')) {
              reviewErrorMsg = '您已经评论过该商品'
            }
          } else if (typeof reviewError === 'string') {
            reviewErrorMsg = reviewError
          } else {
            reviewErrorMsg = '评论创建失败，请稍后重试'
          }
          // 如果评论创建失败，仍然继续完成订单评价流程
        }

        // 2. 提交评价（调用完成评价接口，将订单状态改为退款/售后）
        const res = await completeReview(this.orderId)
        if (res && res.flag) {
          if (reviewCreated) {
            this.$message.success('评价提交成功，感谢您的反馈！')
          } else {
            // 显示具体的错误信息
            const errorMsg = reviewErrorMsg || '评论保存失败'
            this.$message.warning(`订单评价完成，但${errorMsg}，请稍后重试`)
          }
          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            this.$router.push('/home/orderInfo').catch(() => {})
          }, 1500)
        } else {
          this.$message.error(res?.message || '提交评价失败')
        }
      } catch (error) {
        console.error('提交评价失败:', error)
        let errorMsg = '提交评价失败'
        
        if (error && typeof error === 'object') {
          if (error.flag === false) {
            errorMsg = error.message || error.msg || '提交评价失败'
            if (errorMsg.includes('登录') || error.status === 401) {
              this.$message.warning('请先登录')
              this.$router.push('/login').catch(() => {})
              return
            }
          } else {
            errorMsg = error.message || error.msg || '提交评价失败，请检查网络连接'
          }
        } else if (typeof error === 'string') {
          errorMsg = error
        }
        
        this.$message.error(errorMsg)
      } finally {
        this.submitting = false
      }
    },
    getRatingText(rating) {
      const texts = ['', '很差', '较差', '一般', '满意', '非常满意']
      return texts[rating] || ''
    },
    handleUploadSuccess(response, file, fileList) {
      if (response && response.flag) {
        const imageUrl = typeof response.data === 'string' ? response.data : String(response.data || '')
        this.evaluateForm.images.push(imageUrl)
        this.imageList = fileList
        this.$message.success('图片上传成功')
      } else {
        this.$message.error('图片上传失败')
      }
    },
    handleRemove(file, fileList) {
      this.imageList = fileList
      // 从images数组中移除对应的图片
      if (file.response && file.response.data) {
        const imageUrl = typeof file.response.data === 'string' ? file.response.data : String(file.response.data)
        this.evaluateForm.images = this.evaluateForm.images.filter(img => img !== imageUrl)
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatPrice(price) {
      if (!price && price !== 0) return '0.00'
      return parseFloat(price).toFixed(2)
    },
    getImageUrl(picture) {
      if (!picture) return '/order/wutu.gif'
      if (picture.startsWith('http')) return picture
      if (picture.startsWith('/')) return picture
      return `${this.$store.state.imgShowRoad || ''}/file/${picture}`
    },
    handleImageError(event) {
      event.target.src = '/order/wutu.gif'
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="less" scoped>
.evaluate-container {
  width: 1100px;
  margin: 20px auto;
  background: #fff;
  padding: 30px;
  min-height: calc(100vh - 200px);

  .evaluate-header {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #e8e8e8;

    h2 {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin: 0 0 10px 0;
    }

    .subtitle {
      font-size: 14px;
      color: #666;
      margin: 0;
    }
  }

  .order-info-card {
    background: #f9f9f9;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 30px;

    .order-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;
      padding-bottom: 10px;
      border-bottom: 1px solid #e8e8e8;

      .order-id {
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }

      .order-date {
        font-size: 14px;
        color: #999;
      }
    }

    .order-content {
      display: flex;
      gap: 20px;

      .product-image {
        width: 120px;
        height: 120px;
        border-radius: 6px;
        overflow: hidden;
        background: #fff;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .product-info {
        flex: 1;

        .product-name {
          font-size: 18px;
          font-weight: 600;
          color: #333;
          margin: 0 0 10px 0;
        }

        .product-desc {
          font-size: 14px;
          color: #666;
          margin: 0 0 10px 0;
          line-height: 1.5;
        }

        .product-price {
          font-size: 16px;

          .price-label {
            color: #666;
          }

          .price-value {
            color: #ff5000;
            font-weight: bold;
            font-size: 20px;
          }
        }
      }
    }
  }

  .evaluate-form-card {
    background: #fff;
    border-radius: 8px;
    padding: 30px;

    .rating-section {
      display: flex;
      align-items: center;
      gap: 20px;

      .rating-text {
        font-size: 14px;
        color: #ff5000;
        font-weight: 600;
      }
    }

    .evaluate-upload {
      /deep/ .el-upload--picture-card {
        width: 100px;
        height: 100px;
        line-height: 100px;
      }

      /deep/ .el-upload-list--picture-card .el-upload-list__item {
        width: 100px;
        height: 100px;
      }
    }

    .upload-tip {
      font-size: 12px;
      color: #999;
      margin-top: 10px;
    }

    .submit-section {
      margin-top: 30px;
      text-align: center;
      padding-top: 20px;
      border-top: 1px solid #e8e8e8;

      .el-button {
        padding: 12px 40px;
        font-size: 16px;
        margin: 0 10px;
      }
    }
  }
}
</style>

