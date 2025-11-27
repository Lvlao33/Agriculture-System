<!--农产品交易-->
<template>
  <div class="trade-container">
    <!-- 顶部横幅 -->
    <div class="trade-banner">
      <div class="banner-content">
        <h1 class="banner-title">农产品交易平台</h1>
        <p class="banner-desc">为农户、经销商和消费者提供完整的线上交易服务</p>
      </div>
    </div>

    <!-- 快速入口 -->
    <div class="quick-actions">
      <div class="action-card" @click="handleQuickAction('goods')">
        <div class="action-icon goods-icon">
          <i class="el-icon-s-shop"></i>
        </div>
        <div class="action-title">商品货源</div>
        <div class="action-desc">浏览和购买优质农产品</div>
      </div>
      <div class="action-card" @click="handleQuickAction('purchase')">
        <div class="action-icon purchase-icon">
          <i class="el-icon-s-comment"></i>
        </div>
        <div class="action-title">求购需求</div>
        <div class="action-desc">发布和查看求购信息</div>
      </div>
      <div class="action-card" @click="handleQuickAction('order')">
        <div class="action-icon order-icon">
          <i class="el-icon-s-order"></i>
        </div>
        <div class="action-title">订单信息</div>
        <div class="action-desc">管理我的订单信息</div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧：商品货源预览 -->
      <div class="left-content">
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="el-icon-s-shop"></i> 热门商品
            </h2>
            <a class="more-link" @click="handleQuickAction('goods')">查看更多 ></a>
          </div>
          <div class="goods-preview">
            <div 
              class="goods-item" 
              v-for="(item, index) in hotGoods" 
              :key="index"
              @click="goToGoodsDetail(item)"
            >
              <div class="goods-image">
                <img 
                  v-if="item.picture && item.picture !== ''"
                  :src="`/order/${item.picture}`" 
                  alt="商品图片"
                />
                <img 
                  v-else
                  :src="`/order/wutu.gif`" 
                  alt="暂无图片"
                />
              </div>
              <div class="goods-info">
                <div class="goods-name">{{ item.content || item.name || '商品名称' }}</div>
                <div class="goods-price">¥{{ item.price || '面议' }}</div>
              </div>
            </div>
            <div v-if="hotGoods.length === 0" class="empty-state">
              <p>暂无商品</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：求购需求和订单统计 -->
      <div class="right-content">
        <!-- 求购需求 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="el-icon-s-comment"></i> 最新求购
            </h2>
            <a class="more-link" @click="handleQuickAction('purchase')">查看更多 ></a>
          </div>
          <div class="purchase-preview">
            <div 
              class="purchase-item" 
              v-for="(item, index) in latestNeeds" 
              :key="index"
              @click="goToPurchaseDetail(item)"
            >
              <div class="purchase-title">{{ item.content || item.title || '求购信息' }}</div>
              <div class="purchase-meta">
                <span>{{ item.ownName || '个人' }}</span>
                <span>{{ formatDate(item.createTime || item.updateTime) }}</span>
              </div>
            </div>
            <div v-if="latestNeeds.length === 0" class="empty-state">
              <p>暂无求购信息</p>
            </div>
          </div>
        </div>

        <!-- 订单统计 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="el-icon-s-order"></i> 订单统计
            </h2>
            <a class="more-link" @click="handleQuickAction('order')">查看详情 ></a>
          </div>
          <div class="order-stats">
            <div class="stat-item">
              <div class="stat-number">{{ orderStats.pendingPayment || 0 }}</div>
              <div class="stat-label">待付款</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ orderStats.pendingShipment || 0 }}</div>
              <div class="stat-label">待发货</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ orderStats.pendingReceipt || 0 }}</div>
              <div class="stat-label">待收货</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { selectGoodsPage, selectNeedsPage } from "../api/order";

export default {
  name: "HomeTrade",
  data() {
    return {
      hotGoods: [],
      latestNeeds: [],
      orderStats: {
        pendingPayment: 0,
        pendingShipment: 0,
        pendingReceipt: 0
      }
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "2");
    this.loadHotGoods();
    this.loadLatestNeeds();
    this.loadOrderStats();
  },
  methods: {
    handleQuickAction(type) {
      switch(type) {
        case 'goods':
          this.$router.push("/home/goods").catch((err) => err);
          break;
        case 'purchase':
          this.$router.push("/home/purchase").catch((err) => err);
          break;
        case 'order':
          this.$router.push("/home/orderInfo").catch((err) => err);
          break;
      }
    },
    loadHotGoods() {
      selectGoodsPage({
        pageNum: 1,
        keys: ""
      }).then((res) => {
        if (res.flag == true) {
          this.hotGoods = (res.data.list || []).slice(0, 6);
        }
      }).catch(err => {
        console.log(err);
      });
    },
    loadLatestNeeds() {
      selectNeedsPage({
        pageNum: 1,
        keys: ""
      }).then((res) => {
        if (res.flag == true) {
          this.latestNeeds = (res.data.list || []).slice(0, 5);
        }
      }).catch(err => {
        console.log(err);
      });
    },
    loadOrderStats() {
      // TODO: 加载订单统计信息
      // 这里可以调用订单统计接口
    },
    goToGoodsDetail(item) {
      if (item.orderId) {
        this.$store.commit("updateOrderId", item.orderId);
        this.$router.push(`/home/details?orderId=${item.orderId}`).catch((err) => err);
      }
    },
    goToPurchaseDetail(item) {
      if (item.orderId) {
        this.$store.commit("updateOrderId", item.orderId);
        this.$router.push(`/home/purchaseDetails?orderId=${item.orderId}`).catch((err) => err);
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  }
};
</script>

<style lang="less" scoped>
.trade-container {
  width: 1100px;
  margin: 0 auto;
  padding: 20px 0;

  .trade-banner {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    border-radius: 8px;
    padding: 40px 30px;
    margin-bottom: 30px;
    color: #fff;

    .banner-content {
      text-align: center;

      .banner-title {
        font-size: 32px;
        font-weight: bold;
        margin: 0 0 15px 0;
      }

      .banner-desc {
        font-size: 16px;
        margin: 0;
        opacity: 0.9;
      }
    }
  }

  .quick-actions {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;

    .action-card {
      flex: 1;
      background: #fff;
      border-radius: 8px;
      padding: 30px 20px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .action-icon {
        width: 60px;
        height: 60px;
        margin: 0 auto 15px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 30px;
        color: #fff;

        &.goods-icon {
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
        }

        &.purchase-icon {
          background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
        }

        &.order-icon {
          background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
        }
      }

      .action-title {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        margin-bottom: 8px;
      }

      .action-desc {
        font-size: 14px;
        color: #666;
      }
    }
  }

  .main-content {
    display: flex;
    gap: 20px;

    .left-content {
      flex: 2;

      .content-section {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        .section-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          padding-bottom: 15px;
          border-bottom: 2px solid #f0f0f0;

          .section-title {
            font-size: 20px;
            font-weight: bold;
            color: #333;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 8px;

            i {
              color: #67c23a;
            }
          }

          .more-link {
            font-size: 14px;
            color: #409eff;
            cursor: pointer;
            text-decoration: none;

            &:hover {
              text-decoration: underline;
            }
          }
        }

        .goods-preview {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 15px;

          .goods-item {
            cursor: pointer;
            transition: transform 0.3s;

            &:hover {
              transform: translateY(-3px);
            }

            .goods-image {
              width: 100%;
              height: 150px;
              border-radius: 6px;
              overflow: hidden;
              margin-bottom: 10px;
              background: #f5f5f5;

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
              }
            }

            .goods-info {
              .goods-name {
                font-size: 14px;
                color: #333;
                margin-bottom: 5px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }

              .goods-price {
                font-size: 16px;
                font-weight: bold;
                color: #f56c6c;
              }
            }
          }

          .empty-state {
            grid-column: 1 / -1;
            text-align: center;
            padding: 40px 0;
            color: #999;
          }
        }
      }
    }

    .right-content {
      flex: 1;

      .content-section {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        .section-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          padding-bottom: 15px;
          border-bottom: 2px solid #f0f0f0;

          .section-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 8px;

            i {
              color: #67c23a;
            }
          }

          .more-link {
            font-size: 14px;
            color: #409eff;
            cursor: pointer;
            text-decoration: none;

            &:hover {
              text-decoration: underline;
            }
          }
        }

        .purchase-preview {
          .purchase-item {
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
            cursor: pointer;
            transition: color 0.3s;

            &:last-child {
              border-bottom: none;
            }

            &:hover {
              color: #409eff;
            }

            .purchase-title {
              font-size: 14px;
              color: #333;
              margin-bottom: 8px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .purchase-meta {
              display: flex;
              justify-content: space-between;
              font-size: 12px;
              color: #999;
            }
          }

          .empty-state {
            text-align: center;
            padding: 40px 0;
            color: #999;
          }
        }

        .order-stats {
          display: flex;
          justify-content: space-around;
          padding: 20px 0;

          .stat-item {
            text-align: center;

            .stat-number {
              font-size: 32px;
              font-weight: bold;
              color: #67c23a;
              margin-bottom: 8px;
            }

            .stat-label {
              font-size: 14px;
              color: #666;
            }
          }
        }
      }
    }
  }
}
</style>

