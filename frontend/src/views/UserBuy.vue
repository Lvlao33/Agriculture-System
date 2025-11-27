<template>
  <div class="user-buy-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <i class="el-icon-shopping-cart-full"></i>
        我买的
      </h2>
      <p class="page-subtitle">查看您作为买家的所有订单</p>
    </div>

    <!-- 状态筛选标签 -->
    <div class="status-tabs">
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'all' }"
        @click="switchStatus('all')"
      >
        全部订单
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'pending_payment' }"
        @click="switchStatus('pending_payment')"
      >
        待付款
        <span v-if="statusCounts.pending_payment > 0" class="count-badge">{{ statusCounts.pending_payment }}</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'pending_shipment' }"
        @click="switchStatus('pending_shipment')"
      >
        待发货
        <span v-if="statusCounts.pending_shipment > 0" class="count-badge">{{ statusCounts.pending_shipment }}</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'pending_receipt' }"
        @click="switchStatus('pending_receipt')"
      >
        待收货
        <span v-if="statusCounts.pending_receipt > 0" class="count-badge">{{ statusCounts.pending_receipt }}</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'pending_review' }"
        @click="switchStatus('pending_review')"
      >
        待评价
        <span v-if="statusCounts.pending_review > 0" class="count-badge">{{ statusCounts.pending_review }}</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'completed' }"
        @click="switchStatus('completed')"
      >
        已完成
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentStatus === 'refund' }"
        @click="switchStatus('refund')"
      >
        退款/售后
        <span v-if="statusCounts.refund > 0" class="count-badge">{{ statusCounts.refund }}</span>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索订单号、商品名称"
        class="search-input"
        clearable
        @keyup.enter.native="handleSearch"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
      </el-input>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="yyyy-MM-dd"
        value-format="yyyy-MM-dd"
        class="date-picker"
        @change="handleDateChange"
      ></el-date-picker>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-if="filteredOrders.length === 0" class="empty-state">
        <i class="el-icon-box"></i>
        <p>暂无订单</p>
        <p class="empty-tip">您还没有相关订单</p>
      </div>

      <div 
        v-for="order in filteredOrders" 
        :key="order.orderId || order.id"
        class="order-card"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderId || order.order_id || order.id }}</span>
            <span class="order-date">{{ formatDate(order.createTime || order.create_time) }}</span>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.status || order.type)" size="small">
              {{ getStatusText(order.status || order.type) }}
            </el-tag>
          </div>
        </div>

        <!-- 订单内容 -->
        <div class="order-content">
          <!-- 商品信息 -->
          <div class="product-section">
            <div class="product-image">
              <img 
                :src="getProductImage(order)" 
                :alt="order.productName || order.title"
                @error="handleImageError"
              />
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ order.productName || order.title || '商品名称' }}</h4>
              <p class="product-desc" v-if="order.description || order.content">
                {{ order.description || order.content }}
              </p>
              <div class="product-meta">
                <span class="seller-name">
                  <i class="el-icon-user"></i>
                  卖家：{{ order.sellerName || order.ownName || '未知' }}
                </span>
              </div>
            </div>
          </div>

          <!-- 价格和操作 -->
          <div class="order-right">
            <div class="price-section">
              <div class="price-item">
                <span class="label">商品金额</span>
                <span class="value">￥{{ formatPrice(order.totalPrice || order.price || order.uninPricee) }}</span>
              </div>
              <div class="price-item total">
                <span class="label">实付款</span>
                <span class="value">￥{{ formatPrice(order.totalPrice || order.price || order.uninPricee) }}</span>
              </div>
            </div>
            <div class="action-section">
              <el-button 
                v-if="(order.status === 0 || order.type === 0) && (order.status !== 'pending_payment')"
                type="primary" 
                size="small"
                @click="handlePayment(order)"
              >
                立即付款
              </el-button>
              <el-button 
                v-if="order.status === 'pending_receipt' || order.type === 1"
                type="success" 
                size="small"
                @click="handleConfirmReceipt(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.status === 'pending_review' || order.status === 'completed'"
                type="primary" 
                size="small"
                plain
                @click="handleReview(order)"
              >
                评价
              </el-button>
              <el-button 
                size="small"
                @click="handleViewDetail(order)"
              >
                查看详情
              </el-button>
              <el-button 
                v-if="order.status !== 'completed' && order.status !== 'refund'"
                type="danger" 
                size="small"
                plain
                @click="handleRefund(order)"
              >
                申请退款
              </el-button>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="order-footer" v-if="order.address">
          <i class="el-icon-location-outline"></i>
          <span>收货地址：{{ order.address }}</span>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="filteredOrders.length > 0">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalOrders"
        layout="total, prev, pager, next, jumper"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import { selectBuyByUserName } from "../api/order";

export default {
  name: 'UserBuy',
  data() {
    return {
      userBuyList: [],
      loading: false,
      currentStatus: 'all',
      searchKeyword: '',
      dateRange: null,
      currentPage: 1,
      pageSize: 10,
      statusCounts: {
        pending_payment: 0,
        pending_shipment: 0,
        pending_receipt: 0,
        pending_review: 0,
        refund: 0
      }
    };
  },
  computed: {
    filteredOrders() {
      let orders = this.userBuyList;
      
      // 状态筛选
      if (this.currentStatus !== 'all') {
        orders = orders.filter(order => {
          const status = order.status || order.type;
          return this.matchStatus(status, this.currentStatus);
        });
      }
      
      // 搜索筛选
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        orders = orders.filter(order => {
          const orderId = String(order.orderId || order.order_id || order.id || '').toLowerCase();
          const productName = (order.productName || order.title || '').toLowerCase();
          return orderId.includes(keyword) || productName.includes(keyword);
        });
      }
      
      // 日期筛选
      if (this.dateRange && this.dateRange.length === 2) {
        orders = orders.filter(order => {
          const orderDate = new Date(order.createTime || order.create_time);
          const startDate = new Date(this.dateRange[0]);
          const endDate = new Date(this.dateRange[1]);
          endDate.setHours(23, 59, 59, 999);
          return orderDate >= startDate && orderDate <= endDate;
        });
      }
      
      // 分页
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return orders.slice(start, end);
    },
    totalOrders() {
      let orders = this.userBuyList;
      
      if (this.currentStatus !== 'all') {
        orders = orders.filter(order => {
          const status = order.status || order.type;
          return this.matchStatus(status, this.currentStatus);
        });
      }
      
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        orders = orders.filter(order => {
          const orderId = String(order.orderId || order.order_id || order.id || '').toLowerCase();
          const productName = (order.productName || order.title || '').toLowerCase();
          return orderId.includes(keyword) || productName.includes(keyword);
        });
      }
      
      if (this.dateRange && this.dateRange.length === 2) {
        orders = orders.filter(order => {
          const orderDate = new Date(order.createTime || order.create_time);
          const startDate = new Date(this.dateRange[0]);
          const endDate = new Date(this.dateRange[1]);
          endDate.setHours(23, 59, 59, 999);
          return orderDate >= startDate && orderDate <= endDate;
        });
      }
      
      return orders.length;
    }
  },
  created() {
    this.$store.commit("updateUserActiveIndex", "3-1");
    this.loadOrders();
  },
  methods: {
    loadOrders() {
      this.loading = true;
      selectBuyByUserName({}).then((res) => {
        this.userBuyList = res.data || [];
        this.calculateStatusCounts();
        this.loading = false;
      }).catch(() => {
        this.loading = false;
        this.$message.error('加载订单失败');
      });
    },
    calculateStatusCounts() {
      this.statusCounts = {
        pending_payment: 0,
        pending_shipment: 0,
        pending_receipt: 0,
        pending_review: 0,
        refund: 0
      };
      
      this.userBuyList.forEach(order => {
        const status = order.status || order.type;
        if (status === 0 || status === 'pending_payment') {
          this.statusCounts.pending_payment++;
        } else if (status === 'pending_shipment') {
          this.statusCounts.pending_shipment++;
        } else if (status === 1 || status === 'pending_receipt') {
          this.statusCounts.pending_receipt++;
        } else if (status === 'pending_review') {
          this.statusCounts.pending_review++;
        } else if (status === 'refund') {
          this.statusCounts.refund++;
        }
      });
    },
    switchStatus(status) {
      this.currentStatus = status;
      this.currentPage = 1;
    },
    handleSearch() {
      this.currentPage = 1;
    },
    handleDateChange() {
      this.currentPage = 1;
    },
    handlePageChange(page) {
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    matchStatus(orderStatus, filterStatus) {
      if (filterStatus === 'pending_payment') {
        return orderStatus === 0 || orderStatus === 'pending_payment';
      } else if (filterStatus === 'pending_shipment') {
        return orderStatus === 'pending_shipment';
      } else if (filterStatus === 'pending_receipt') {
        return orderStatus === 1 || orderStatus === 'pending_receipt';
      } else if (filterStatus === 'pending_review') {
        return orderStatus === 'pending_review';
      } else if (filterStatus === 'completed') {
        return orderStatus === 'completed' || orderStatus === 2;
      } else if (filterStatus === 'refund') {
        return orderStatus === 'refund';
      }
      return false;
    },
    getStatusType(status) {
      if (status === 0 || status === 'pending_payment') return 'danger';
      if (status === 'pending_shipment') return 'warning';
      if (status === 1 || status === 'pending_receipt') return 'primary';
      if (status === 'pending_review') return 'info';
      if (status === 'completed' || status === 2) return 'success';
      if (status === 'refund') return 'danger';
      return 'info';
    },
    getStatusText(status) {
      if (status === 0 || status === 'pending_payment') return '待付款';
      if (status === 'pending_shipment') return '待发货';
      if (status === 1 || status === 'pending_receipt') return '待收货';
      if (status === 'pending_review') return '待评价';
      if (status === 'completed' || status === 2) return '已完成';
      if (status === 'refund') return '退款/售后';
      return '未知状态';
    },
    formatDate(date) {
      if (!date) return '-';
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      const hour = String(d.getHours()).padStart(2, '0');
      const minute = String(d.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    formatPrice(price) {
      if (!price) return '0.00';
      return Number(price).toFixed(2);
    },
    getProductImage(order) {
      if (order.picture) {
        return this.$store.state.imgShowRoad + '/file/' + order.picture;
      }
      if (order.image) {
        return order.image;
      }
      return require('../assets/img/wutu.gif');
    },
    handleImageError(e) {
      e.target.src = require('../assets/img/wutu.gif');
    },
    handlePayment(order) {
      this.$message.info('跳转到支付页面');
      // TODO: 实现支付逻辑
    },
    handleConfirmReceipt(order) {
      this.$confirm('确认已收到商品？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('确认收货成功');
        this.loadOrders();
      }).catch(() => {});
    },
    handleReview(order) {
      this.$message.info('跳转到评价页面');
      // TODO: 实现评价逻辑
    },
    handleViewDetail(order) {
      this.$router.push({
        path: '/home/details',
        query: { orderId: order.orderId || order.order_id || order.id }
      });
    },
    handleRefund(order) {
      this.$confirm('确定要申请退款吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.info('退款申请已提交');
        // TODO: 实现退款逻辑
      }).catch(() => {});
    }
  }
};
</script>

<style lang="less" scoped>
.user-buy-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 20px;
  
  .page-title {
    font-size: 24px;
    color: #303133;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    gap: 10px;
    
    i {
      color: #67C23A;
      font-size: 28px;
    }
  }
  
  .page-subtitle {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.status-tabs {
  background: white;
  padding: 0 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  gap: 0;
  border-bottom: 2px solid #EBEEF5;
  
  .tab-item {
    padding: 16px 24px;
    cursor: pointer;
    color: #606266;
    font-size: 14px;
    position: relative;
    transition: all 0.3s;
    border-bottom: 2px solid transparent;
    margin-bottom: -2px;
    display: flex;
    align-items: center;
    gap: 6px;
    
    &:hover {
      color: #67C23A;
    }
    
    &.active {
      color: #67C23A;
      border-bottom-color: #67C23A;
      font-weight: 600;
    }
    
    .count-badge {
      background: #F56C6C;
      color: white;
      border-radius: 10px;
      padding: 2px 6px;
      font-size: 12px;
      min-width: 18px;
      text-align: center;
    }
  }
}

.search-filter-bar {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  gap: 16px;
  align-items: center;
  
  .search-input {
    flex: 1;
    max-width: 400px;
  }
  
  .date-picker {
    width: 300px;
  }
}

.order-list {
  min-height: 400px;
  
  .empty-state {
    background: white;
    padding: 80px 20px;
    text-align: center;
    border-radius: 8px;
    
    i {
      font-size: 64px;
      color: #C0C4CC;
      margin-bottom: 16px;
      display: block;
    }
    
    p {
      font-size: 16px;
      color: #909399;
      margin: 8px 0;
    }
    
    .empty-tip {
      font-size: 14px;
      color: #C0C4CC;
    }
  }
  
  .order-card {
    background: white;
    border-radius: 8px;
    margin-bottom: 16px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 16px;
      border-bottom: 1px solid #EBEEF5;
      margin-bottom: 16px;
      
      .order-info {
        display: flex;
        gap: 20px;
        align-items: center;
        
        .order-number {
          font-size: 14px;
          color: #303133;
          font-weight: 500;
        }
        
        .order-date {
          font-size: 13px;
          color: #909399;
        }
      }
    }
    
    .order-content {
      display: flex;
      gap: 20px;
      
      .product-section {
        flex: 1;
        display: flex;
        gap: 16px;
        
        .product-image {
          width: 120px;
          height: 120px;
          border-radius: 8px;
          overflow: hidden;
          background: #F5F7FA;
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
            font-size: 16px;
            color: #303133;
            margin: 0 0 8px 0;
            font-weight: 600;
          }
          
          .product-desc {
            font-size: 13px;
            color: #909399;
            margin: 0 0 12px 0;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .product-meta {
            display: flex;
            gap: 20px;
            font-size: 13px;
            color: #606266;
            
            .seller-name {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
      
      .order-right {
        width: 200px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        align-items: flex-end;
        
        .price-section {
          text-align: right;
          margin-bottom: 12px;
          
          .price-item {
            margin-bottom: 8px;
            
            .label {
              font-size: 13px;
              color: #909399;
              margin-right: 8px;
            }
            
            .value {
              font-size: 14px;
              color: #606266;
            }
            
            &.total {
              .value {
                font-size: 18px;
                color: #F56C6C;
                font-weight: 600;
              }
            }
          }
        }
        
        .action-section {
          display: flex;
          flex-direction: column;
          gap: 8px;
          width: 100%;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
    
    .order-footer {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid #EBEEF5;
      font-size: 13px;
      color: #606266;
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
}

.pagination-wrapper {
  background: white;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .user-buy-page {
    padding: 10px;
  }
  
  .search-filter-bar {
    flex-direction: column;
    
    .search-input,
    .date-picker {
      width: 100%;
      max-width: 100%;
    }
  }
  
  .order-card {
    .order-content {
      flex-direction: column;
      
      .order-right {
        width: 100%;
        align-items: stretch;
        
        .action-section {
          flex-direction: row;
          flex-wrap: wrap;
          
          .el-button {
            flex: 1;
            min-width: 100px;
          }
        }
      }
    }
  }
}
</style>
