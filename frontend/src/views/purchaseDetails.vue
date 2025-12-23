<template>
  <div class="details-box">
    <div v-if="loading" class="loading-wrapper">
      <i class="el-icon-loading"></i>
      <span>加载中...</span>
    </div>
    <div v-else-if="!data || !data.orderId" class="empty-state">
      <i class="el-icon-warning"></i>
      <p>未找到相关信息</p>
    </div>
    <div v-else>
      <h4 class="title">{{ data.title || '无标题' }}</h4>
      <div class="time">
        <span>发布时间：{{ data.createTime | changeTime }}</span>
        <span style="margin-left: 50px;">最近修改时间：{{ data.updateTime | changeTime }}</span>
        <div class="item-style">
          <el-button type="danger" @click="addShopcartClick" v-if="data.type == 'goods'">加入购物车</el-button>
          <el-popover placement="right" width="320" trigger="hover">
            <div>
              <div class="item-sales">买家姓名：<span class="sales-text">{{data.ownName || '未知'}}</span></div>
              <div class="item-sales">买家地址：<span class="sales-text">{{data.address || '未提供'}}</span></div>
              <div class="item-sales">更新时间：<span class="sales-text">{{data.updateTime | formatTimer}}</span></div>
            </div>
            <div style="color:#fc7373;margin-left:50px;cursor:pointer;display: flex;" slot="reference" @click="changeInfo" v-show="data.type == 'needs'">
              <div style="margin-right:10px;color:#fff;font-weight:bold;background: #fc7373;width: 20px;height: 20px;border-radius: 10px;line-height: 20px;text-align: center;">!</div>
              买家信息
            </div>
          </el-popover>
        </div>
      </div>
      <div class="image-wrapper" v-if="data.picture">
        <img :src="$store.state.imgShowRoad + '/file/' + data.picture" alt="商品图片" />
      </div>
      <div class="info">
        <div class="content" :title="data.content">{{ data.content || '暂无描述' }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { addOrderToCart } from "../api/cart";
import { selectOrderById } from "../api/order";
import ChangeMessage from "../components/ChangeMessage.vue";
import { selectUserByUsername } from "../api/user";

export default {
  name: "PurchaseDetails",
  data() {
    return {
      data: {},
      ownerInfo: {},
      userGoods: [],
      updateGoodInfo: {},
      updateUserData: {},
      loading: true
    };
  },
  filters: {
    changeTime(time) {
      if (!time) return '未知';
      if (typeof time === 'string' && time.length >= 10) {
        return time.slice(0, 10);
      }
      try {
        const date = new Date(time);
        if (isNaN(date.getTime())) return '未知';
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      } catch (e) {
        return '未知';
      }
    },
    formatTimer: function(value) {
      if (!value) return '未知';
      try {
        let date = new Date(value);
        if (isNaN(date.getTime())) return '未知';
        let y = date.getFullYear();
        let MM = date.getMonth() + 1;
        MM = MM < 10 ? "0" + MM : MM;
        let d = date.getDate();
        d = d < 10 ? "0" + d : d;
        let h = date.getHours();
        h = h < 10 ? "0" + h : h;
        let m = date.getMinutes();
        m = m < 10 ? "0" + m : m;
        return y + "-" + MM + "-" + d + " " + h + ":" + m;
      } catch (e) {
        return '未知';
      }
    }
  },
  components: { ChangeMessage },
  props: {
    ctype: {
      type: String,
    },
    cdesciibe: {
      type: String,
    },
  },
  methods: {
    addShopcartClick() {
      if (!this.data.orderId) {
        this.$message.warning('订单信息不完整');
        return;
      }
      addOrderToCart({
        order_id: this.data.orderId,
      })
        .then((res) => {
          if (res.flag == true) {
            this.$message.success(res.message || '添加成功');
          } else {
            this.$message.error(res.message || '添加失败');
          }
        })
        .catch((err) => {
          console.error('加入购物车错误:', err);
          // 处理错误信息
          if (err && typeof err === 'object') {
            if (err.flag === false) {
              const errorMsg = err.message || '添加失败';
              if (errorMsg.includes('登录') || err.status === 401) {
                this.$message.warning('请先登录');
                this.$router.push('/login').catch(() => {});
              } else {
                this.$message.error(errorMsg);
              }
            } else if (err.isNetworkError) {
              this.$message.error(err.message || '网络连接失败，请检查网络设置');
            } else {
              this.$message.error(err.message || '添加失败，请检查网络连接');
            }
          } else {
            this.$message.error('添加失败，请先登录');
          }
        });
    },
    changeInfo() {
      if (!this.data.orderId) {
        this.$message.warning('订单信息不完整');
        return;
      }
      this.$store.commit("updateChangedOrderId", this.data.orderId);
      selectOrderById({
        order_id: this.data.orderId,
      })
        .then((res) => {
          if (res.flag == true) {
            this.updateGoodInfo = res.data;
          }
        })
        .catch((err) => {
          console.error(err);
          this.$message.error('获取信息失败');
        });
    },
    getSalesInfo() {
      if (!this.data.ownName) return;
      selectUserByUsername({
        user_name: this.data.ownName,
      }).then((res) => {
        if (res.flag == true) {
          this.updateUserData = res.data;
        }
      }).catch(err => {
        console.error(err);
      });
    },
    loadData() {
      const orderId = this.$route.query.orderId;
      if (!orderId) {
        this.loading = false;
        this.$message.error('缺少订单ID参数');
        return;
      }

      this.loading = true;
      selectOrderById({
        order_id: orderId,
      }).then((res) => {
        this.loading = false;
        if (res.flag == true && res.data) {
          this.data = res.data;
          this.getSalesInfo();
        } else {
          this.$message.error(res.message || '获取订单信息失败');
        }
      }).catch((err) => {
        this.loading = false;
        console.error(err);
        this.$message.error('加载失败，请稍后重试');
      });
    }
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
    this.loadData();
  },
  watch: {
    '$route'(to, from) {
      if (to.query.orderId !== from.query.orderId) {
        this.loadData();
      }
    }
  }
};
</script>

<style lang="less" scoped>
.details-box {
  width: 1100px;
  min-height: 100%;
  height: auto;
  margin: 20px auto;
  padding: 20px;
  background: #fff;
  display: flex;
  flex-direction: column;

  .loading-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: #999;

    i {
      font-size: 32px;
      margin-bottom: 10px;
      animation: rotating 2s linear infinite;
    }

    @keyframes rotating {
      0% {
        transform: rotate(0deg);
      }
      100% {
        transform: rotate(360deg);
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: #999;

    i {
      font-size: 64px;
      margin-bottom: 20px;
      display: block;
      color: #e6a23c;
    }

    p {
      font-size: 16px;
    }
  }

  .image-wrapper {
    display: flex;
    justify-content: center;
    margin: 30px 0;

    img {
      width: 360px;
      height: 300px;
      object-fit: cover;
      border-radius: 6px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }

  .title {
    font-size: 24px;
    font-weight: bold;
    line-height: 35px;
    max-height: 70px;
    text-align: center;
    margin-bottom: 20px;
    color: #303133;
    word-break: break-word;
  }

  .time {
    margin-top: 5px;
    color: #999;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
  }

  .info {
    width: 100%;
    min-height: 300px;
    border-radius: 6px;
    padding: 20px;
    margin: 20px 0;
    background: #fafafa;
    display: flex;
    align-content: center;
    justify-content: center;

    .content {
      min-height: 100px;
      line-height: 1.8;
      padding: 15px;
      color: #606266;
      font-size: 14px;
      white-space: pre-wrap;
      word-break: break-word;
      width: 100%;
    }
  }

  .item-style {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
    flex-wrap: wrap;
    gap: 10px;
  }

  .item-sales {
    color: #333 !important;
    line-height: 30px;
    max-height: 30px;
    font-size: 14px;

    .sales-text {
      color: #666;
      margin-left: 5px;
    }
  }
}
</style>
