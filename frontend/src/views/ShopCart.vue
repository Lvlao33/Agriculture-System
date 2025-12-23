<template>
  <div class="shop-cart-container">
    <!-- 页面头部 -->
    <div class="cart-header">
      <h2 class="page-title">
        <i class="el-icon-shopping-cart-2"></i>
        购物车
      </h2>
      <div class="cart-info">
        <span class="cart-count">共 <strong>{{ cartItems.length }}</strong> 件商品</span>
      </div>
    </div>

    <!-- 购物车内容 -->
    <div class="cart-content" v-if="cartItems.length > 0">
      <!-- 商品列表 -->
      <div class="cart-table-wrapper">
        <el-table
          :data="cartItems"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          ref="cartTable"
          :row-key="getRowKey"
        >
          <el-table-column type="selection" width="55" :reserve-selection="true"></el-table-column>
          
          <el-table-column label="商品信息" min-width="400">
            <template v-slot="scope">
              <div class="goods-info">
                <div class="goods-image">
                  <img 
                    :src="getImageUrl(scope.row.picture || scope.row.image)" 
                    :alt="scope.row.title || scope.row.name"
                    @error="handleImageError"
                  />
                </div>
                <div class="goods-details">
                  <h4 class="goods-title">{{ scope.row.title || scope.row.name || '商品名称' }}</h4>
                  <p class="goods-desc">{{ scope.row.content || scope.row.description || '商品描述' }}</p>
                  <div class="goods-spec" v-if="scope.row.spec">
                    <span>规格：{{ scope.row.spec }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="单价" width="120" align="center">
            <template v-slot="scope">
              <span class="price">¥{{ formatPrice(scope.row.price) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="数量" width="180" align="center">
            <template v-slot="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                :max="9999"
                @change="handleQuantityChange(scope.row)"
                size="small"
              ></el-input-number>
            </template>
          </el-table-column>

          <el-table-column label="小计" width="120" align="center">
            <template v-slot="scope">
              <span class="subtotal">¥{{ formatPrice(calculateSubtotal(scope.row)) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <el-button
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                class="delete-btn"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 底部操作栏 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-button @click="toggleSelectAll">{{ isAllSelected ? '取消全选' : '全选' }}</el-button>
          <el-button @click="handleBatchDelete" :disabled="selectedItems.length === 0">
            删除选中
          </el-button>
        </div>
        <div class="footer-right">
          <div class="price-info">
            <span class="total-label">已选 <strong>{{ selectedItems.length }}</strong> 件商品，合计：</span>
            <span class="total-price">¥{{ formatPrice(totalPrice) }}</span>
          </div>
          <el-button
            type="danger"
            size="medium"
            @click="handleCheckout"
            :disabled="selectedItems.length === 0"
            class="checkout-btn"
          >
            结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-cart" v-else>
      <div class="empty-icon">
        <i class="el-icon-shopping-cart-2"></i>
      </div>
      <p class="empty-text">购物车是空的</p>
      <p class="empty-desc">快去挑选心仪的商品吧~</p>
      <el-button type="primary" @click="goShopping">去购物</el-button>
    </div>
  </div>
</template>

<script>
// 导入购物车相关的 API 接口
import { cartShow, cartDeleteOrder, updateGoodsCount } from '@/api/cart'
import { checkout } from '@/api/trade'

export default {
  name: 'ShopCart',
  data() {
    return {
      cartItems: [], // 购物车商品列表
      selectedItems: [], // 选中的商品
      isAllSelected: false, // 是否全选
      loading: false, // 加载状态
    }
  },
  computed: {
    // 计算总价
    totalPrice() {
      return this.selectedItems.reduce((total, item) => {
        return total + (parseFloat(item.price) || 0) * (item.quantity || 1)
      }, 0)
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '6')
    // TODO: 调用后端接口获取购物车列表
    this.loadCartItems()
  },
  methods: {
    // 获取购物车列表
    async loadCartItems() {
      this.loading = true
      try {
        const res = await cartShow({})
        if (res && res.flag) {
          // 将后端返回的数据转换为前端需要的格式
          // 后端返回的数据已经包含商品详情，直接使用
          this.cartItems = (res.data || []).map(item => ({
            id: item.cartId || item.id,
            cartId: item.cartId || item.id,
            productId: item.productId,
            title: item.title || item.name,
            name: item.title || item.name,
            content: item.content || item.description,
            description: item.content || item.description,
            picture: item.picture || item.image || item.imageUrl,
            image: item.picture || item.image || item.imageUrl,
            price: item.price || 0,
            quantity: item.count || item.quantity || 1,
            count: item.count || item.quantity || 1,
            spec: item.spec || '',
            category: item.category || ''
          }))
        } else {
          this.cartItems = []
          if (res && res.message) {
            this.$message.warning(res.message)
          }
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
        this.cartItems = []
        // 如果未登录，提示登录
        if (error.response && error.response.status === 401) {
          this.$message.warning('请先登录')
          this.$router.push('/login').catch(err => err)
        } else if (error.response && error.response.status !== 401) {
          this.$message.error('加载购物车失败，请稍后重试')
        }
      } finally {
        this.loading = false
      }
    },

    // 更新商品数量
    async handleQuantityChange(item) {
      try {
        // 使用 productId 更新数量
        await updateGoodsCount({
          id: item.productId,
          productId: item.productId,
          count: item.quantity
        })
        // 同步更新 count 字段
        item.count = item.quantity
        this.$message.success('数量已更新')
        // 重新计算总价
        this.$forceUpdate()
      } catch (error) {
        console.error('更新数量失败:', error)
        this.$message.error('更新数量失败')
        // 恢复原值，重新加载购物车
        this.loadCartItems()
      }
    },

    // 删除单个商品
    async handleDelete(item) {
      this.$confirm('确定要删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 使用 productId 删除
          await cartDeleteOrder({
            order_id: item.productId,
            productId: item.productId
          })
          this.$message.success('删除成功')
          // 从选中列表中移除
          this.selectedItems = this.selectedItems.filter(i => i.id !== item.id)
          // 重新加载购物车列表
          this.loadCartItems()
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },

    // 批量删除
    async handleBatchDelete() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请选择要删除的商品')
        return
      }
      
      this.$confirm(`确定要删除选中的 ${this.selectedItems.length} 件商品吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 使用循环调用单个删除接口
          const deletePromises = this.selectedItems.map(item => 
            cartDeleteOrder({
              order_id: item.productId,
              productId: item.productId
            })
          )
          await Promise.all(deletePromises)
          this.$message.success('删除成功')
          this.selectedItems = []
          this.loadCartItems()
        } catch (error) {
          console.error('批量删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },

    // 结算：为每种商品创建独立的待付款订单
    async handleCheckout() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请选择要结算的商品')
        return
      }
      
      // 检查是否登录
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login').catch(err => err)
        return
      }

      try {
        // 获取收货地址（可以从用户信息或地址管理获取，这里先使用默认值）
        // TODO: 可以弹出一个对话框让用户选择或输入收货地址
        const productIds = this.selectedItems.map(item => item.productId)
        
        const res = await checkout({
          productIds: productIds,
          shippingAddress: '请填写收货地址', // 可以从地址管理获取
          receiverName: '请填写收货人姓名',
          receiverPhone: '请填写收货人电话'
        })
        
        if (res && res.flag) {
          // 后端会为每种商品创建一个独立的订单，返回订单列表
          const orders = res.data || []
          const orderCount = Array.isArray(orders) ? orders.length : 1
          this.$message.success(`成功创建 ${orderCount} 个待付款订单，请前往订单管理付款`)
          
          // 从购物车中删除已结算的商品
          const deletePromises = this.selectedItems.map(item => 
            cartDeleteOrder({
              order_id: item.productId,
              productId: item.productId
            })
          )
          await Promise.all(deletePromises)
          this.selectedItems = []
          // 重新加载购物车
          this.loadCartItems()
          // 跳转到订单管理页面
          this.$router.push('/home/orderInfo').catch(err => err)
        } else {
          this.$message.error(res?.message || '结算失败')
        }
      } catch (error) {
        console.error('结算失败:', error)
        if (error && typeof error === 'object') {
          if (error.flag === false) {
            const errorMsg = error.message || '结算失败'
            if (errorMsg.includes('登录') || error.status === 401) {
              this.$message.warning('请先登录')
              this.$router.push('/login').catch(err => err)
            } else {
              this.$message.error(errorMsg)
            }
          } else {
            this.$message.error(error.message || '结算失败，请检查网络连接')
          }
        } else {
          this.$message.error('结算失败')
        }
      }
    },

    // 选择变化
    handleSelectionChange(selection) {
      this.selectedItems = selection
      this.isAllSelected = selection.length === this.cartItems.length && this.cartItems.length > 0
    },

    // 全选/取消全选
    toggleSelectAll() {
      if (this.isAllSelected) {
        this.$refs.cartTable.clearSelection()
      } else {
        this.cartItems.forEach(row => {
          this.$refs.cartTable.toggleRowSelection(row, true)
        })
      }
    },

    // 计算小计
    calculateSubtotal(item) {
      return (parseFloat(item.price) || 0) * (item.quantity || 1)
    },

    // 格式化价格
    formatPrice(price) {
      if (!price && price !== 0) return '0.00'
      return parseFloat(price).toFixed(2)
    },

    // 获取图片 URL
    getImageUrl(picture) {
      if (!picture) return '/order/wutu.gif'
      if (picture.startsWith('http')) return picture
      if (picture.startsWith('/')) return picture
      return `${this.$store.state.imgShowRoad || ''}/file/${picture}`
    },

    // 图片加载错误处理
    handleImageError(event) {
      event.target.src = '/order/wutu.gif'
    },

    // 获取行 key
    getRowKey(row) {
      return row.productId || row.id || row.cartId || Math.random()
    },

    // 去购物
    goShopping() {
      this.$router.push('/home/goods')
    }
  }
}
</script>

<style lang="less" scoped>
.shop-cart-container {
  width: 1100px;
  margin: 0 auto;
  padding: 20px 0;
  min-height: calc(100vh - 200px);

  .cart-header {
    background: #fff;
    border-radius: 8px;
    padding: 20px 30px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;

    .page-title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 10px;

      i {
        color: #f56c6c;
        font-size: 28px;
      }
    }

    .cart-info {
      .cart-count {
        font-size: 14px;
        color: #666;

        strong {
          color: #f56c6c;
          font-size: 18px;
        }
      }
    }
  }

  .cart-content {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .cart-table-wrapper {
      margin-bottom: 20px;

      .goods-info {
        display: flex;
        align-items: center;
        gap: 15px;

        .goods-image {
          width: 100px;
          height: 100px;
          border-radius: 6px;
          overflow: hidden;
          background: #f5f5f5;
          flex-shrink: 0;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .goods-details {
          flex: 1;
          min-width: 0;

          .goods-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin: 0 0 8px 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .goods-desc {
            font-size: 14px;
            color: #999;
            margin: 0 0 8px 0;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            line-height: 1.5;
          }

          .goods-spec {
            font-size: 12px;
            color: #999;
          }
        }
      }

      .price {
        font-size: 16px;
        font-weight: 600;
        color: #f56c6c;
      }

      .subtotal {
        font-size: 16px;
        font-weight: 600;
        color: #f56c6c;
      }

      .delete-btn {
        color: #f56c6c;

        &:hover {
          color: #ff8c94;
        }
      }
    }

    .cart-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      background: #f9f9f9;
      border-radius: 6px;
      margin-top: 20px;

      .footer-left {
        display: flex;
        gap: 15px;
      }

      .footer-right {
        display: flex;
        align-items: center;
        gap: 20px;

        .price-info {
          display: flex;
          align-items: baseline;
          gap: 10px;

          .total-label {
            font-size: 14px;
            color: #666;

            strong {
              color: #f56c6c;
            }
          }

          .total-price {
            font-size: 24px;
            font-weight: bold;
            color: #f56c6c;
          }
        }

        .checkout-btn {
          padding: 12px 40px;
          font-size: 16px;
        }
      }
    }
  }

  .empty-cart {
    background: #fff;
    border-radius: 8px;
    padding: 80px 20px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .empty-icon {
      font-size: 80px;
      color: #ddd;
      margin-bottom: 20px;

      i {
        font-size: 80px;
      }
    }

    .empty-text {
      font-size: 18px;
      color: #666;
      margin: 0 0 10px 0;
    }

    .empty-desc {
      font-size: 14px;
      color: #999;
      margin: 0 0 30px 0;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .shop-cart-container {
    width: 95%;
    padding: 20px 10px;
  }
}

@media (max-width: 768px) {
  .shop-cart-container {
    .cart-footer {
      flex-direction: column;
      gap: 20px;

      .footer-right {
        width: 100%;
        justify-content: space-between;
      }
    }
  }
}
</style>
