<template>
  <div class="order-info-page">
    <!-- 顶部主标签导航：我买的 / 我卖的 -->
    <div class="main-tabs-container">
      <div class="main-tabs-wrapper">
        <div 
          class="main-tab-item" 
          :class="{ active: currentModule === 'my_buy' }"
          @click="switchModule('my_buy')"
        >
          我买的
        </div>
        <div 
          class="main-tab-item" 
          :class="{ active: currentModule === 'my_sell' }"
          @click="switchModule('my_sell')"
        >
          我卖的
        </div>
      </div>
    </div>

    <!-- 子标签导航：所有订单、待付款等 -->
    <div class="tabs-container">
      <div class="tabs-wrapper">
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'all' }"
          @click="switchTab('all')"
        >
          所有订单
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'pending_payment' }"
          @click="switchTab('pending_payment')"
        >
          待付款
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'pending_shipment' }"
          @click="switchTab('pending_shipment')"
        >
          待发货
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'pending_receipt' }"
          @click="switchTab('pending_receipt')"
        >
          待收货
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'pending_review' }"
          @click="switchTab('pending_review')"
        >
          待评价 <span v-if="pendingReviewCount > 0" class="count-badge">{{ pendingReviewCount }}</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'refund_after_sale' }"
          @click="switchTab('refund_after_sale')"
        >
          退款/售后
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-filter-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="商品标题/订单号/店铺名"
          class="search-input"
          @keyup.enter.native="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
      <div class="filter-buttons">
        <el-button size="small" @click="showFilterDialog">筛选</el-button>
        <el-button size="small" @click="exportOrders">导出订单</el-button>
        <el-button size="small" @click="printOrders">打印</el-button>
      </div>
    </div>

    <!-- 订单列表表头 -->
    <div class="order-table-header">
      <div class="header-col order-info-col">订单信息</div>
      <div class="header-col amount-col">商品金额</div>
      <div class="header-col payment-col">实付款</div>
      <div class="header-col action-col">订单操作</div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <div 
        class="order-card" 
        v-for="(order, index) in paginatedOrders" 
        :key="order.orderId || index"
      >
        <!-- 订单头部：订单号和日期 -->
        <div class="order-header">
          <div class="order-number">
            <span class="order-id">订单号：{{ order.orderId || order.order_id }}</span>
            <span class="order-date">{{ formatDate(order.createTime || order.create_time) }}</span>
          </div>
        </div>

        <!-- 订单内容 -->
        <div class="order-content">
          <!-- 左侧：商品信息 -->
          <div class="order-info-section">
            <div class="product-info">
              <div class="product-image">
                <img 
                  :src="order.image || ($store.state.imgShowRoad + '/file/' + (order.picture || order.pic))" 
                  :alt="order.productName || order.title"
                  @error="handleImageError"
                />
              </div>
              <div class="product-details">
                <div class="store-name">
                  <span>{{ order.storeName || order.store_name || '店铺名称' }}</span>
                  <i class="el-icon-arrow-right"></i>
                </div>
                <div class="product-title">{{ order.productName || order.title || '商品名称' }}</div>
                <div class="product-desc" v-if="order.description || order.content">
                  {{ order.description || order.content }}
                </div>
                <div class="product-tags">
                  <span v-if="order.supportReturn !== false" class="tag return-tag">安心兑换 7天无理由退货</span>
                  <span v-else class="tag no-return-tag">不支持7天无理由</span>
                </div>
                <div class="product-actions">
                  <span class="action-link" @click="applyAfterSale(order)">申请售后</span>
                  <span class="action-link" v-if="order.contactService" @click="contactService(order)">联系客服代下单 全国通用</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 中间：商品金额 -->
          <div class="amount-section">
            <div class="price-info">
              <div class="original-price" v-if="order.originalPrice">
                ¥{{ order.originalPrice }}
              </div>
              <div class="actual-price">
                ¥{{ order.price || order.totalPrice || 0 }}
              </div>
              <div class="quantity">x{{ order.quantity || order.count || 1 }}</div>
            </div>
          </div>

          <!-- 右侧：实付款和操作 -->
          <div class="payment-action-section">
            <div class="payment-info">
              <div class="payment-amount">
                ¥{{ order.totalPrice || order.price || 0 }}
              </div>
              <div class="shipping-fee">(含运费 ¥{{ order.shippingFee || 0 }})</div>
              <div class="order-type" v-if="order.orderType">{{ order.orderType }}</div>
            </div>
            <div class="order-status">{{ getStatusText(order.status || order.type) }}</div>
            <div class="action-buttons">
              <!-- 我买的模块：买家操作 -->
              <el-button 
                v-if="currentModule === 'my_buy' && (order.status === 'pending_payment' || order.type === 0)" 
                size="small" 
                type="danger"
                @click="payOrder(order)"
              >
                立即付款
              </el-button>
              <el-button 
                v-if="currentModule === 'my_buy' && order.status === 'pending_receipt'" 
                size="small" 
                type="danger"
                @click="confirmReceive(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="currentModule === 'my_buy' && order.status === 'pending_review'" 
                size="small" 
                type="warning"
                @click="evaluate(order)"
              >
                评价
              </el-button>
              <!-- 我卖的模块：卖家操作 -->
              <el-button 
                v-if="currentModule === 'my_sell' && order.status === 'pending_shipment'" 
                size="small" 
                type="danger"
                @click="shipOrder(order)"
              >
                确认发货
              </el-button>
              <!-- 兼容旧逻辑（如果没有currentModule） -->
              <el-button 
                v-if="!currentModule && (order.status === 'pending_payment' || order.type === 0)" 
                size="small" 
                type="danger"
                @click="payOrder(order)"
              >
                立即付款
              </el-button>
              <el-button 
                v-if="!currentModule && order.status === 'pending_receipt'" 
                size="small" 
                type="danger"
                @click="confirmReceive(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="!currentModule && order.status === 'pending_review'" 
                size="small" 
                type="warning"
                @click="evaluate(order)"
              >
                评价
              </el-button>
              <el-button 
                v-if="order.status === 'completed' || order.type === 1" 
                size="small"
                @click="buyAgain(order)"
              >
                再买一单
              </el-button>
              <el-button 
                v-if="order.status === 'completed' || order.type === 1" 
                size="small"
                @click="viewLogistics(order)"
              >
                查看物流
              </el-button>
              <el-button 
                v-if="order.status === 'completed' || order.type === 1" 
                size="small"
                @click="addToCart(order)"
              >
                加入购物车
              </el-button>
              <el-button 
                v-if="order.status === 'completed' || order.type === 1" 
                size="small"
                type="text"
                @click="applyInvoice(order)"
              >
                申请开票
              </el-button>
              <el-button 
                v-if="order.status === 'pending_payment' || order.status === 'refund_after_sale'"
                size="small"
                type="text"
                @click="deleteOrder(order)"
              >
                删除订单
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="paginatedOrders.length === 0" class="empty-state">
        <i class="el-icon-box"></i>
        <p>暂无订单</p>
      </div>
    </div>

    <!-- 分页信息 -->
    <div class="pagination-info" v-if="paginatedOrders.length > 0">
      <span>共 {{ totalOrders }} 条订单</span>
    </div>
  </div>
</template>

<script>
import { selectBuyByUserName, selectAllPage, deleteOrderById, confirmOrderReceive, payOrder, shipOrder, completeReview, getOrders } from "../api/trade";
import { selectSellByUserName } from "../api/order";
import { addOrderToCart } from "../api/cart";

export default {
  name: "OrderInfo",
  data() {
    return {
      currentModule: "my_buy", // 当前主模块：my_buy(我买的) 或 my_sell(我卖的)
      currentTab: "all", // 当前子标签：all, pending_payment, pending_shipment, pending_receipt, pending_review, refund_after_sale
      currentPage: 1,
      pageSize: 10,
      searchKeyword: "",
      orders: [],
      pendingReviewCount: 3,
      totalOrders: 0,
      totalPages: 1,
    };
  },
  computed: {
    filteredOrders() {
      let filtered = this.orders;
      
      // 根据子标签筛选订单状态（如果子标签不是"all"）
      // 严格按status字段筛选，避免订单在多个标签下重复显示
      if (this.currentTab === "pending_payment") {
        filtered = filtered.filter(order => {
          const status = order.status;
          // 只显示待付款状态的订单，兼容旧数据（type === 0 且没有status）
          return status === "pending_payment" || (!status && order.type === 0);
        });
      } else if (this.currentTab === "pending_shipment") {
        filtered = filtered.filter(order => {
          const status = order.status;
          // 只显示待发货状态的订单
          return status === "pending_shipment";
        });
      } else if (this.currentTab === "pending_receipt") {
        filtered = filtered.filter(order => {
          const status = order.status;
          // 只显示待收货状态的订单
          return status === "pending_receipt";
        });
      } else if (this.currentTab === "pending_review") {
        filtered = filtered.filter(order => {
          const status = order.status;
          // 只显示待评价状态的订单
          return status === "pending_review";
        });
      } else if (this.currentTab === "refund_after_sale") {
        filtered = filtered.filter(order => {
          const status = order.status;
          // 只显示退款/售后状态的订单
          return status === "refund_after_sale" ||
                 status === "refunding" || 
                 status === "refunded" ||
                 status === "after_sale";
        });
      }
      // "all" 标签不进行状态筛选，显示所有数据

      // 根据搜索关键词筛选
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(order => {
          const title = (order.title || order.productName || "").toLowerCase();
          const orderId = (order.orderId || order.order_id || "").toString();
          const storeName = (order.storeName || order.store_name || "").toLowerCase();
          return title.includes(keyword) || 
                 orderId.includes(keyword) || 
                 storeName.includes(keyword);
        });
      }

      this.totalOrders = filtered.length;
      this.totalPages = Math.ceil(filtered.length / this.pageSize);
      
      return filtered;
    },
    paginatedOrders() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredOrders.slice(start, end);
    }
  },
  methods: {
    switchModule(module) {
      this.currentModule = module;
      this.currentTab = "all"; // 切换主模块时，重置为"所有订单"
      this.currentPage = 1;
      this.loadOrders();
    },
    switchTab(tab) {
      this.currentTab = tab;
      this.currentPage = 1;
      this.loadOrders();
    },
    handleSearch() {
      this.currentPage = 1;
      // 搜索逻辑已在 computed 中处理
    },
    showFilterDialog() {
      this.$message.info("筛选功能开发中");
    },
    exportOrders() {
      this.$message.info("导出订单功能开发中");
    },
    printOrders() {
      window.print();
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    getStatusText(status) {
      const statusMap = {
        "pending_payment": "待付款",
        "pending_shipment": "待发货",
        "pending_receipt": "待收货",
        "pending_review": "待评价",
        "completed": "交易成功",
        0: "待付款",
        1: "交易成功"
      };
      return statusMap[status] || "交易成功";
    },
    handleImageError(e) {
      // 使用 require 引用本地资源，避免走代理
      e.target.src = require("../assets/img/wutu.gif");
    },
    async payOrder(order) {
      const orderId = order.id || order.orderId || order.order_id;
      if (!orderId) {
        this.$message.error("订单ID缺失，无法付款");
        return;
      }
      
      this.$confirm("确认付款？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(async () => {
        try {
          const res = await payOrder(orderId);
          if (res && res.flag) {
            this.$message.success(res.message || "付款成功，订单已进入待发货状态");
            // 重新加载订单列表，确保状态正确（不直接修改order对象，避免状态不一致）
            await this.loadOrders();
            // 如果当前在待付款标签，切换到待发货标签
            if (this.currentTab === "pending_payment") {
              this.currentTab = "pending_shipment";
            }
          } else {
            this.$message.error((res && res.message) || "付款失败");
          }
        } catch (error) {
          console.error("付款失败:", error);
          if (error && typeof error === 'object') {
            if (error.flag === false) {
              const errorMsg = error.message || '付款失败';
              if (errorMsg.includes('登录') || error.status === 401) {
                this.$message.warning('请先登录');
                this.$router.push('/login').catch(() => {});
              } else {
                this.$message.error(errorMsg);
              }
            } else {
              this.$message.error(error.message || '付款失败，请检查网络连接');
            }
          } else {
            this.$message.error("付款失败");
          }
        }
      }).catch(() => {});
    },
    async shipOrder(order) {
      const orderId = order.id || order.orderId || order.order_id;
      if (!orderId) {
        this.$message.error("订单ID缺失，无法发货");
        return;
      }
      
      this.$confirm("确认发货？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(async () => {
        try {
          const res = await shipOrder(orderId);
          if (res && res.flag) {
            this.$message.success(res.message || "发货成功，订单已进入待收货状态");
            // 重新加载订单列表，确保状态正确（不直接修改order对象，避免状态不一致）
            await this.loadOrders();
            // 如果当前在待发货标签，切换到待收货标签
            if (this.currentTab === "pending_shipment") {
              this.currentTab = "pending_receipt";
            }
          } else {
            this.$message.error((res && res.message) || "发货失败");
          }
        } catch (error) {
          console.error("发货失败:", error);
          if (error && typeof error === 'object') {
            if (error.flag === false) {
              const errorMsg = error.message || '发货失败';
              if (errorMsg.includes('登录') || error.status === 401) {
                this.$message.warning('请先登录');
                this.$router.push('/login').catch(() => {});
              } else {
                this.$message.error(errorMsg);
              }
            } else {
              this.$message.error(error.message || '发货失败，请检查网络连接');
            }
          } else {
            this.$message.error("发货失败");
          }
        }
      }).catch(() => {});
    },
    confirmReceive(order) {
      this.$confirm("确认收货？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        const orderId = order.id || order.orderId || order.order_id;
        if (!orderId) {
          this.$message.error("订单ID缺失，无法确认收货");
          return;
        }
        confirmOrderReceive(orderId)
          .then(async (res) => {
            if (res && res.flag) {
              this.$message.success(res.message || "确认收货成功，订单已进入待评价");
              // 重新加载订单列表，确保状态正确（不直接修改order对象，避免状态不一致）
              await this.loadOrders();
              // 如果当前在待收货标签，切换到待评价标签
              if (this.currentTab === "pending_receipt") {
                this.currentTab = "pending_review";
              }
            } else {
              this.$message.error((res && res.message) || "确认收货失败");
            }
          })
          .catch((err) => {
            console.error(err);
            this.$message.error("确认收货失败");
          });
      });
    },
    evaluate(order) {
      // 跳转到评价页面
      const orderId = order.id || order.orderId || order.order_id;
      this.$router.push({
        path: '/home/order/evaluate',
        query: { orderId: orderId }
      }).catch(() => {});
    },
    buyAgain(order) {
      addOrderToCart({
        order_id: order.orderId || order.order_id
      }).then((res) => {
        if (res.flag) {
          this.$message.success("已加入购物车");
        } else {
          this.$message.error(res.message || "加入购物车失败");
        }
      }).catch((err) => {
        console.error('加入购物车错误:', err);
        // 处理错误信息
        if (err && typeof err === 'object') {
          if (err.flag === false) {
            const errorMsg = err.message || '加入购物车失败';
            if (errorMsg.includes('登录') || err.status === 401) {
              this.$message.warning('请先登录');
              this.$router.push('/login').catch(() => {});
            } else {
              this.$message.error(errorMsg);
            }
          } else if (err.isNetworkError) {
            this.$message.error(err.message || '网络连接失败，请检查网络设置');
          } else {
            this.$message.error(err.message || '加入购物车失败，请检查网络连接');
          }
        } else {
          this.$message.error('加入购物车失败');
        }
      });
    },
    viewLogistics(order) {
      this.$message.info("查看物流功能开发中");
    },
    addToCart(order) {
      addOrderToCart({
        order_id: order.orderId || order.order_id
      }).then((res) => {
        if (res.flag) {
          this.$message.success("已加入购物车");
        } else {
          this.$message.error(res.message || "加入购物车失败");
        }
      }).catch((err) => {
        console.error('加入购物车错误:', err);
        // 处理错误信息
        if (err && typeof err === 'object') {
          if (err.flag === false) {
            const errorMsg = err.message || '加入购物车失败';
            if (errorMsg.includes('登录') || err.status === 401) {
              this.$message.warning('请先登录');
              this.$router.push('/login').catch(() => {});
            } else {
              this.$message.error(errorMsg);
            }
          } else if (err.isNetworkError) {
            this.$message.error(err.message || '网络连接失败，请检查网络设置');
          } else {
            this.$message.error(err.message || '加入购物车失败，请检查网络连接');
          }
        } else {
          this.$message.error('加入购物车失败');
        }
      });
    },
    applyAfterSale(order) {
      this.$message.info("申请售后功能开发中");
    },
    contactService(order) {
      this.$message.info("联系客服功能开发中");
    },
    applyInvoice(order) {
      this.$message.info("申请开票功能开发中");
    },
    deleteOrder(order) {
      const orderId = order.id || order.orderId || order.order_id;
      if (!orderId) {
        this.$message.error("订单ID缺失，无法删除");
        return;
      }

      // 检查订单状态，给出提示
      const status = order.status;
      if (status !== "pending_payment" && status !== "refund_after_sale") {
        this.$message.warning("只能删除待付款或退款/售后状态的订单");
        return;
      }

      this.$confirm("确认删除该订单？删除后无法恢复。", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteOrderById({
          order_id: orderId
        }).then((res) => {
          if (res && res.flag) {
            this.$message.success(res.message || "删除成功");
            // 从列表中移除已删除的订单
            this.orders = this.orders.filter(o => 
              (o.id || o.orderId || o.order_id) !== orderId
            );
            this.$forceUpdate();
            // 重新加载订单列表
            this.loadOrders();
          } else {
            this.$message.error((res && res.message) || "删除失败");
          }
        }).catch((err) => {
          console.error("删除订单失败:", err);
          if (err && typeof err === 'object') {
            if (err.flag === false) {
              const errorMsg = err.message || '删除失败';
              if (errorMsg.includes('登录') || err.status === 401) {
                this.$message.warning('请先登录');
                this.$router.push('/login').catch(() => {});
              } else {
                this.$message.error(errorMsg);
              }
            } else {
              this.$message.error(err.message || '删除失败，请检查网络连接');
            }
          } else {
            this.$message.error("删除失败");
          }
        });
      }).catch(() => {});
    },
    getMockOrders() {
      // 模拟订单数据 - 仅包含农产品相关订单
      const now = new Date();
      const mockOrders = [
        {
          orderId: "4851234567890123456",
          order_id: "4851234567890123456",
          storeName: "绿色农业专营店",
          store_name: "绿色农业专营店",
          productName: "有机苹果 5kg装 新鲜水果 产地直发",
          title: "有机苹果 5kg装 新鲜水果 产地直发",
          description: "【有机认证】新疆阿克苏有机苹果 脆甜多汁 5kg装 包邮",
          content: "【有机认证】新疆阿克苏有机苹果 脆甜多汁 5kg装 包邮",
          image: "https://via.placeholder.com/120x120?text=Apple",
          picture: "apple.jpg",
          originalPrice: 88.00,
          price: 68.00,
          totalPrice: 68.00,
          quantity: 1,
          count: 1,
          shippingFee: 0.00,
          status: "completed",
          type: 1,
          createTime: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000).toISOString(), // 5天前
          create_time: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000).toISOString(),
          supportReturn: true,
          reviewed: true,
          orderType: "手机订单"
        },
        {
          orderId: "4852345678901234567",
          order_id: "4852345678901234567",
          storeName: "优质种子供应商",
          store_name: "优质种子供应商",
          productName: "玉米种子 高产抗病 适合北方种植 1kg装",
          title: "玉米种子 高产抗病 适合北方种植 1kg装",
          description: "【高产品种】郑单958玉米种子 抗倒伏 抗病性强 亩产可达800kg",
          content: "【高产品种】郑单958玉米种子 抗倒伏 抗病性强 亩产可达800kg",
          image: "https://via.placeholder.com/120x120?text=Corn",
          picture: "corn-seed.jpg",
          originalPrice: 0,
          price: 45.00,
          totalPrice: 45.00,
          quantity: 2,
          count: 2,
          shippingFee: 10.00,
          status: "pending_receipt",
          type: 1,
          createTime: new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000).toISOString(), // 2天前
          create_time: new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000).toISOString(),
          supportReturn: true,
          reviewed: false,
          orderType: "手机订单"
        },
        {
          orderId: "4853456789012345678",
          order_id: "4853456789012345678",
          storeName: "农机设备商城",
          store_name: "农机设备商城",
          productName: "小型手扶拖拉机 农用微耕机 田园管理机",
          title: "小型手扶拖拉机 农用微耕机 田园管理机",
          description: "【包邮】15马力柴油手扶拖拉机 适合大棚蔬菜种植 果园管理",
          content: "【包邮】15马力柴油手扶拖拉机 适合大棚蔬菜种植 果园管理",
          image: "https://via.placeholder.com/120x120?text=Tractor",
          picture: "tractor.jpg",
          originalPrice: 0,
          price: 3280.00,
          totalPrice: 3280.00,
          quantity: 1,
          count: 1,
          shippingFee: 0.00,
          status: "pending_payment",
          type: 0,
          createTime: new Date(now.getTime() - 1 * 24 * 60 * 60 * 1000).toISOString(), // 1天前
          create_time: new Date(now.getTime() - 1 * 24 * 60 * 60 * 1000).toISOString(),
          supportReturn: true,
          reviewed: false,
          orderType: "手机订单"
        }
      ];
      return mockOrders;
    },
    async loadOrders() {
      try {
        let res;
        
        // 根据当前主模块和子标签加载订单
        // 根据主模块确定是"我买的"还是"我卖的"
        // 先获取所有订单，然后在前端根据子标签筛选
        if (this.currentModule === "my_buy") {
          // "我买的"模块：获取当前用户作为买家的所有订单
          res = await selectBuyByUserName({});
        } else if (this.currentModule === "my_sell") {
          // "我卖的"模块：获取当前用户作为卖家的所有订单
          res = await selectSellByUserName({});
        } else {
          // 兼容旧逻辑（如果currentModule不是my_buy或my_sell）
          let status = null;
          if (this.currentTab === "pending_payment") {
            status = "pending_payment";
          } else if (this.currentTab === "pending_receipt") {
            status = "pending_receipt";
          } else if (this.currentTab === "pending_review") {
            status = "pending_review";
          } else if (this.currentTab === "refund_after_sale") {
            status = "refund_after_sale";
          }
          res = await getOrders({ status: status });
        }
        
        if (res && res.flag && res.data) {
          // 处理不同API返回的数据格式
          let apiOrders = [];
          if (Array.isArray(res.data)) {
            apiOrders = res.data;
          } else if (res.data.list) {
            apiOrders = res.data.list;
          } else if (res.data.orders) {
            apiOrders = res.data.orders;
          }
          
          if (apiOrders.length > 0) {
            // 转换订单数据格式，兼容前端显示
            this.orders = apiOrders.map(order => {
              // 兼容不同的数据格式
              const orderId = order.id || order.orderId || order.order_id;
              const orderItems = order.orderItems || order.items || [];
              const productName = orderItems.length > 0 
                ? orderItems[0].productName || orderItems[0].name || orderItems[0].title
                : (order.title || order.productName || order.name || '商品名称');
              
              return {
                id: orderId,
                orderId: orderId,
                order_id: orderId,
                orderNumber: order.orderNumber || order.order_number || orderId,
                title: productName,
                productName: productName,
                price: order.totalAmount || order.total_amount || order.price || 0,
                totalPrice: order.totalAmount || order.total_amount || order.price || 0,
                quantity: orderItems.length > 0 
                  ? orderItems.reduce((sum, item) => sum + (item.quantity || item.count || 1), 0)
                  : (order.quantity || order.count || 1),
                count: orderItems.length > 0 
                  ? orderItems.reduce((sum, item) => sum + (item.quantity || item.count || 1), 0)
                  : (order.quantity || order.count || 1),
                // 优先使用status字段，如果没有status则根据type推断（兼容旧数据）
                status: order.status || (order.type === 0 ? "pending_payment" : (order.type === 1 ? "completed" : "pending_payment")),
                // type字段仅用于兼容旧数据，新数据统一使用status字段
                type: undefined, // 清除type字段，统一使用status避免混乱
                createTime: order.createTime || order.create_time || order.createDate,
                create_time: order.createTime || order.create_time || order.createDate,
                shippingAddress: order.shippingAddress || order.shipping_address,
                receiverName: order.receiverName || order.receiver_name,
                receiverPhone: order.receiverPhone || order.receiver_phone,
                orderItems: orderItems,
                image: order.image || order.imageUrl || order.picture,
                picture: order.picture || order.imageUrl || order.image,
                storeName: order.storeName || order.store_name || '店铺名称'
              };
            });
            
            // 更新待评价数量
            const pendingReviewOrders = this.orders.filter(o => o.status === "pending_review");
            this.pendingReviewCount = pendingReviewOrders.length;
          } else {
            this.orders = [];
          }
        } else {
          // 如果API没有返回数据，使用模拟数据
          this.orders = this.getMockOrders();
        }
      } catch (error) {
        console.error("加载订单失败:", error);
        // 出错时使用模拟数据
        this.orders = this.getMockOrders();
      }
    }
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "5");
    this.loadOrders();
  }
};
</script>

<style lang="less" scoped>
.order-info-page {
  width: 1100px;
  margin: 15px auto;
  background: #fff;
  padding: 15px;
  min-height: 600px;

  .main-tabs-container {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e8e8e8;

    .main-tabs-wrapper {
      display: flex;
      gap: 30px;

      .main-tab-item {
        cursor: pointer;
        padding: 10px 20px;
        font-size: 16px;
        font-weight: 600;
        color: #666;
        position: relative;
        border-bottom: 3px solid transparent;
        transition: all 0.3s;

        &:hover {
          color: #ff5000;
        }

        &.active {
          color: #ff5000;
          border-bottom-color: #ff5000;
        }
      }
    }
  }

  .tabs-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #e8e8e8;

    .tabs-wrapper {
      display: flex;
      gap: 25px;

      .tab-item {
        cursor: pointer;
        padding: 6px 0;
        font-size: 13px;
        color: #666;
        position: relative;

        &.active {
          color: #ff5000;
          font-weight: bold;

          &::after {
            content: "";
            position: absolute;
            bottom: -9px;
            left: 0;
            right: 0;
            height: 2px;
            background: #ff5000;
          }
        }

        .count-badge {
          display: inline-block;
          background: #ff5000;
          color: #fff;
          font-size: 11px;
          padding: 0 4px;
          border-radius: 10px;
          margin-left: 4px;
        }
      }
    }
  }

  .search-filter-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding: 10px 12px;
    background: #f5f5f5;
    border-radius: 4px;

    .search-box {
      flex: 1;
      max-width: 400px;

      .search-input {
        width: 100%;
      }
    }

    .filter-buttons {
      display: flex;
      gap: 10px;
    }
  }

  .order-table-header {
    display: flex;
    padding: 8px 0;
    background: #fafafa;
    border: 1px solid #e8e8e8;
    border-bottom: none;
    font-size: 13px;
    font-weight: bold;
    color: #333;

    .header-col {
      padding: 0 12px;

      &.order-info-col {
        flex: 4;
      }

      &.amount-col {
        flex: 1;
        text-align: center;
      }

      &.payment-col {
        flex: 1;
        text-align: center;
      }

      &.action-col {
        flex: 1;
        text-align: center;
      }
    }
  }

  .order-list {
      .order-card {
      border: 1px solid #e8e8e8;
      border-top: none;
      padding: 6px 8px;
      margin-bottom: 6px;
      background: #fff;

      &:hover {
        background: #fafafa;
      }

      .order-header {
        margin-bottom: 4px;
        padding-bottom: 3px;
        border-bottom: 1px solid #f0f0f0;

        .order-number {
          display: flex;
          gap: 12px;
          font-size: 11px;
          color: #666;
          line-height: 1.3;

          .order-id {
            font-weight: 500;
          }
        }
      }

      .order-content {
        display: flex;
        gap: 6px;
        align-items: flex-start;

        .order-info-section {
          flex: 4;

          .product-info {
            display: flex;
            gap: 6px;

            .product-image {
              width: 60px;
              height: 60px;
              flex-shrink: 0;

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                border-radius: 2px;
              }
            }

            .product-details {
              flex: 1;

              .store-name {
                font-size: 12px;
                color: #666;
                margin-bottom: 2px;
                line-height: 1.3;

                i {
                  margin-left: 3px;
                  font-size: 10px;
                }
              }

              .product-title {
                font-size: 13px;
                color: #333;
                margin-bottom: 2px;
                font-weight: 500;
                line-height: 1.4;
              }

              .product-desc {
                font-size: 11px;
                color: #999;
                margin-bottom: 2px;
                line-height: 1.3;
              }

              .product-tags {
                margin-bottom: 2px;

                .tag {
                  display: inline-block;
                  padding: 1px 4px;
                  font-size: 10px;
                  border-radius: 2px;
                  margin-right: 4px;
                  line-height: 1.2;

                  &.return-tag {
                    background: #e8f5e9;
                    color: #4caf50;
                    border: 1px solid #c8e6c9;
                  }

                  &.no-return-tag {
                    background: #fff3e0;
                    color: #ff9800;
                    border: 1px solid #ffe0b2;
                  }
                }
              }

              .product-actions {
                font-size: 11px;
                color: #666;
                line-height: 1.3;

                .action-link {
                  color: #ff5000;
                  cursor: pointer;
                  margin-right: 8px;

                  &:hover {
                    text-decoration: underline;
                  }
                }
              }
            }
          }
        }

        .amount-section {
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0 4px;

          .price-info {
            text-align: center;

            .original-price {
              font-size: 11px;
              color: #999;
              text-decoration: line-through;
              line-height: 1.2;
            }

            .actual-price {
              font-size: 14px;
              color: #ff5000;
              font-weight: bold;
              margin: 2px 0;
              line-height: 1.2;
            }

            .quantity {
              font-size: 11px;
              color: #666;
              line-height: 1.2;
            }
          }
        }

        .payment-action-section {
          flex: 1;
          text-align: center;
          padding: 0 4px;

          .payment-info {
            margin-bottom: 3px;

            .payment-amount {
              font-size: 14px;
              color: #ff5000;
              font-weight: bold;
              margin-bottom: 1px;
              line-height: 1.2;
            }

            .shipping-fee {
              font-size: 10px;
              color: #999;
              margin-bottom: 1px;
              line-height: 1.2;
            }

            .order-type {
              font-size: 10px;
              color: #666;
              line-height: 1.2;
            }
          }

          .order-status {
            font-size: 11px;
            color: #ff5000;
            font-weight: bold;
            margin-bottom: 4px;
            line-height: 1.2;
          }

          .action-buttons {
            display: flex;
            flex-direction: column;
            gap: 3px;
            align-items: center;

            .el-button {
              width: 75px;
              padding: 4px 8px;
              font-size: 11px;
              height: auto;
              line-height: 1.2;
              
              &.el-button--text {
                color: #666;
                padding: 0;
                width: auto;
                font-size: 10px;
                
                &:hover {
                  color: #ff5000;
                }
              }
            }
          }
        }
      }
    }

    .empty-state {
      text-align: center;
      padding: 60px 0;
      color: #999;

      i {
        font-size: 48px;
        margin-bottom: 15px;
        display: block;
      }

      p {
        font-size: 14px;
      }
    }
  }

  .pagination-info {
    text-align: center;
    padding: 20px 0;
    color: #666;
    font-size: 12px;
  }
}
</style>

