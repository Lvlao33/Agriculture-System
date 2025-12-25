<template>
  <div class="details-box">
    <div v-if="loading" class="loading-container">
      <el-loading text="加载中..."></el-loading>
    </div>
    <div v-else-if="error" class="error-container">
      <el-alert :title="error" type="error" :closable="false"></el-alert>
    </div>
    <div v-else class="details-main">
      <div class="left">
        <img v-if="data.picture && data.picture !== ''" :src="getImageUrl(data.picture)" alt="商品图片" @error="handleImageError" />
        <img v-else src="/order/wutu.gif" alt="暂无图片" />
    </div>

    <div class="info">
        <div class="meta">
          <div class="price-block">
            <div class="product-name-large">{{ data.title || data.name || (data.content ? (data.content.length>30 ? data.content.substr(0,30)+'...' : data.content) : '商品名称') }}</div>
            <span class="price">¥{{ data.price != null ? data.price : '面议' }}</span>
      </div>
          <span class="stock" v-if="data.stock != null">库存：{{ data.stock }}</span>
      </div>

        <div class="time">
          <span>卖家：{{ updateUserData.nickname || updateUserData.username || data.ownName || '未知' }}</span>
          <span>库存：{{ data.stock != null ? data.stock : '—' }}</span>
          <span v-if="data.createTime">发布时间：{{ data.createTime | changeTime }}</span>
        </div>

        <div class="actions">
          <el-button type="primary" @click="onBuyNow">立即购买</el-button>
          <el-button type="success" @click="onAddToCart">加入购物车</el-button>
          <el-button type="default" class="details-back-btn" icon="el-icon-arrow-left" @click="goBackGoodsSource">返回</el-button>
      </div>
    </div>
  </div>

    <!-- 评论总览（始终显示在顶部） -->
    <div class="comments-overview">
      <span class="rating-count">共 {{ commentArray.length || 0 }} 条评论</span>
    </div>

    <el-tabs v-model="activeTab" class="details-tabs" type="card">
      <el-tab-pane label="商品简介" name="desc">
        <div class="desc" :title="data.content">{{ data.content }}</div>
      </el-tab-pane>
      <el-tab-pane label="商品评价" name="comments">
        <div class="comments-section">
          <div class="rating-summary">
            <div class="rating-count">共 {{ commentArray.length || 0 }} 条评论</div>
          </div>
          
          <!-- 评论列表（仅展示，不允许在商品详情页评论） -->
          <div class="comment-list" v-if="commentArray.length > 0">
            <div class="comment-item" v-for="(comment, index) in commentArray" :key="index">
              <div class="comment-header">
                <img v-if="comment.userAvatar" :src="comment.userAvatar" class="comment-avatar" alt="头像" />
                <img v-else src="/person.png" class="comment-avatar" alt="默认头像" />
                <span class="comment-author">{{ comment.ownName }}</span>
                <span class="comment-rating" v-if="comment.rating">
                  <span v-for="i in 5" :key="i" class="star" :class="{ 'star-filled': i <= comment.rating }">★</span>
                </span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-meta">
                <span>{{ comment.createTime | formatTimer2 }}</span>
              </div>
            </div>
          </div>
          <div v-else class="no-comments">
            <p>暂无评论</p>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { addOrderToCart,addOrderToCollect } from "../api/cart";
import { selectOrderById,selectComment } from "../api/order";
import { getProductReviews, getProductDetail, buyNow } from "../api/trade";
import ChangeMessage from "../components/ChangeMessage.vue";
import { selectUserByUsername, searchUserById } from "../api/user";
import { request } from "../utils/request";
import { selectDefaultByOwnName } from "../api/address";

export default {
  data() {
    return {
      // 测试评论（若后端返回空则保留这些测试数据）
      commentArray:[
        { ownName: '匿名' + Math.floor(Math.random()*9000+1000), content: '不错，质量很好，值得购买！', createTime: '2025-12-01T10:00:00Z' },
        { ownName: '匿名' + Math.floor(Math.random()*9000+1000), content: '包装很用心，发货速度快。', createTime: '2025-12-05T15:30:00Z' },
        { ownName: '匿名' + Math.floor(Math.random()*9000+1000), content: '性价比高，下次还会回购。', createTime: '2025-12-10T09:20:00Z' }
      ],
      imgUrl: '../img/good',
      data: {},
      ownerInfo: {},
      userGoods: [],
      updateGoodInfo: {},
      updateUserData:{},
      activeTab: 'desc',
      loading: false,
      error: null
    };
  },
  filters: {
    changeTime(time) {
      if (!time) return '';
      try {
        return String(time).slice(0, 10);
      } catch (e) {
        return '';
      }
    },
    formatTimer: function(value) {
      if (!value) return '';
      let date = new Date(value);
      let y = date.getFullYear();
      let MM = date.getMonth() + 1;
      MM = MM < 10 ? "0" + MM : MM;
      let d = date.getDate();
      d = d < 10 ? "0" + d : d;
      let h = date.getHours();
      h = h < 10 ? "0" + h : h;
      let m = date.getMinutes();
      m = m < 10 ? "0" + m : m;
      let s = date.getSeconds();
      s = s < 10 ? "0" + s : s;
      return y + "-" + MM + "-" + d + " ";
    },
    formatTimer2: function(value) {
      if (!value) return '';
      let date = new Date(value);
      let y = date.getFullYear();
      let MM = date.getMonth() + 1;
      MM = MM < 10 ? "0" + MM : MM;
      let d = date.getDate();
      d = d < 10 ? "0" + d : d;
      let h = date.getHours();
      h = h < 10 ? "0" + h : h;
      let m = date.getMinutes();
      m = m < 10 ? "0" + m : m;
      let s = date.getSeconds();
      s = s < 10 ? "0" + s : s;
      return y + "-" + MM + "-" + d + " "+h+":"+m;
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
    // 查询评论：使用商品ID获取评论列表
    getCommentData(){
      // 获取商品ID（优先使用data.id，否则使用orderId作为productId）
      const productId = this.data.id || this.data.orderId || this.$route.query.orderId;
      if (!productId) {
        console.warn('无法获取商品ID，跳过评论加载');
        return;
      }

      getProductReviews(productId).then(res => {
        try {
          if (res.flag && res.data && res.data.list) {
            const serverComments = res.data.list;
            // 映射后端评论字段到 { ownName, content, createTime, rating }
            this.commentArray = serverComments.map(item => {
              return {
                ownName: item.userNickname || item.userName || item.username || ('匿名' + Math.floor(Math.random()*9000+1000)),
                content: item.content || '',
                createTime: item.createTime || item.createdAt || new Date().toISOString(),
                rating: item.rating || 5,
                userAvatar: item.userAvatar || ''
              };
            });
          } else {
            // 若后端无数据，清空评论数组
            this.commentArray = [];
          }
        } catch (e) {
          console.error('处理评论数据失败', e);
          this.commentArray = [];
        }
      }).catch(err=>{
        // 网络或接口错误时保留空数组
        console.log('获取评论失败', err);
        this.commentArray = [];
      })
    },



    addShopcartClick() {
      if (!this.data.orderId) {
        this.$message.warning('商品信息不完整');
        return;
      }
      addOrderToCart({
        order_id: this.data.orderId,
      })
          .then((res) => {
            console.log(res);
            if (res.flag == true) {
              this.$message.success(res.message || '已加入购物车');
            } else {
              this.$message.error(res.message || '加入购物车失败');
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
    async onBuyNow() {
      // 检查是否登录
      const token = localStorage.getItem('token');
      if (!token) {
        this.$message.warning('请先登录');
        this.$router.push('/login').catch(err => err);
        return;
      }

      // 检查商品信息
      if (!this.data || !this.data.id) {
        this.$message.error('商品信息不完整，无法购买');
        return;
      }

      // 检查商品是否可购买
      if (this.data.type && this.data.type !== 'goods') {
        this.$message.warning('该商品暂不可购买');
        return;
      }

      // 检查库存
      if (this.data.stock != null && this.data.stock <= 0) {
        this.$message.warning('商品库存不足');
        return;
      }

      try {
        // 获取商品ID
        const productId = this.data.id || this.data.productId;
        if (!productId) {
          this.$message.error('无法获取商品ID');
          return;
        }

        // 创建订单（数量默认为1，可以从商品详情页添加数量选择功能）
        const quantity = 1; // 可以后续添加数量选择功能
        
        const res = await buyNow({
          productId: productId,
          quantity: quantity,
          shippingAddress: '请填写收货地址', // 可以从地址管理获取
          receiverName: '请填写收货人姓名',
          receiverPhone: '请填写收货人电话'
        });

        if (res && res.flag) {
          this.$message.success('订单创建成功，请前往订单管理付款');
          // 跳转到订单管理页面
          this.$router.push('/home/orderInfo').catch(err => err);
        } else {
          this.$message.error(res?.message || '创建订单失败');
        }
      } catch (error) {
        console.error('立即购买失败:', error);
        if (error && typeof error === 'object') {
          if (error.flag === false) {
            const errorMsg = error.message || '创建订单失败';
            if (errorMsg.includes('登录') || error.status === 401) {
              this.$message.warning('请先登录');
              this.$router.push('/login').catch(() => {});
            } else {
              this.$message.error(errorMsg);
            }
          } else {
            this.$message.error(error.message || '创建订单失败，请检查网络连接');
          }
        } else {
          this.$message.error('创建订单失败');
        }
      }
    },
    onAddToCart() {
      // 直接调用已有加入购物车逻辑（后端接口已实现）
      this.addShopcartClick();
    },


    like() {
      //   if (this.imageUrl === '../assets/img/good.png') {
      //   this.imageUrl = '../assets/img/like.png';
      // }

      let img = document.getElementById("aa");
      console.log(img.src)
      if(img.src == "http://localhost:8080/img/good.png"){

        img.src = "/img/like.png";

      }
      else if(img.src=="http://localhost:8080/img/like.png"){
        img.src = "/img/good.png";
      }


    },
    like2(){
      let imgb = document.getElementById("bb");
      if(imgb.src == "http://localhost:8080/img/no-star.png"){

        addOrderToCollect({
          order_id: this.data.orderId,
        })
            .then((res) => {
              console.log(res);
              console.log(11111);
              console.log(this.data.orderId)
              if (res.flag == true) {
                alert(res.message);
              } else {
                alert(res.message);
              }
            })
            .catch((err) => {
              alert("添加失败,请先登录");
            });
        imgb.src = "/img/full-star.png";

      }
      else if(imgb.src=="http://localhost:8080/img/full-star.png"){
        imgb.src = "/img/no-star.png";
      }

    },

    changeInfo(item) {
      this.$store.commit("updateChangedOrderId", item);
      selectOrderById({
        order_id: this.$store.state.changedOrderId,
      })
          .then((res) => {
            this.updateGoodInfo = res.data;
          })
          .catch((err) => {
            console.log(err);
          });
    },
    getSalesInfo(){
      // 优先使用sellerId查询卖家信息（新API）
      if (this.data.sellerId) {
        // 使用后端getUserById接口
        searchUserById(this.data.sellerId).then((res) => {
          if (res.flag && res.data) {
            this.updateUserData = res.data;
            // 如果ownName为空，使用查询到的用户名或昵称
            if (!this.data.ownName && res.data) {
              this.data.ownName = res.data.nickname || res.data.username;
            }
          }
        }).catch(err=>{
          console.log('通过sellerId查询卖家信息失败', err);
          // 如果通过ID查询失败，尝试使用用户名查询（向后兼容）
          if (this.data.ownName) {
            this.getSalesInfoByUsername();
          }
        });
      } else if (this.data.ownName) {
        // 如果没有sellerId，使用用户名查询（向后兼容）
        this.getSalesInfoByUsername();
      }
    },
    getSalesInfoByUsername(){
      selectUserByUsername({
        user_name: this.data.ownName,
      }).then((res) => {
        this.updateUserData = res.data;
      }).catch(err=>{
        console.log('通过用户名查询卖家信息失败', err);
      })
    },
    // 处理图片URL
    getImageUrl(picture) {
      if (!picture) return '/order/wutu.gif';
      // 如果已经是完整URL，直接返回
      if (picture.startsWith('http://') || picture.startsWith('https://')) {
        return picture;
      }
      // 如果已经包含/order/前缀，直接返回
      if (picture.startsWith('/order/')) {
        return picture;
      }
      // 如果以/开头，直接返回
      if (picture.startsWith('/')) {
        return picture;
      }
      // 否则添加/order/前缀
      return `/order/${picture}`;
    },
    // 图片加载错误处理
    handleImageError(event) {
      console.log('图片加载失败，使用默认图片', event.target.src);
      event.target.src = '/order/wutu.gif';
    }
    ,
    goBackGoodsSource() {
      // 返回到 商品货源 页面
      this.$router.push('/home/goods').catch(() => {});
    }
    ,
    mapResponseToData(resData) {
      // 统一后端返回字段到组件需要的 data 字段
      if (!resData || typeof resData !== 'object') {
        console.warn('mapResponseToData: resData为空或不是对象', resData);
        return {};
      }
      console.log('mapResponseToData: 原始数据', resData);
      const mapped = {};
      // 优先使用id字段（新API返回的格式）
      mapped.id = resData.id || resData.productId;
      mapped.orderId = resData.orderId || resData.id || resData.order_id || resData.productId;
      // 商品名称：新API返回name字段
      mapped.title = resData.title || resData.name || resData.productName || resData.goodsName || '商品名称';
      mapped.name = mapped.title;
      // 商品描述：新API返回description字段
      mapped.content = resData.content || resData.description || resData.intro || resData.desc || '';
      // 价格
      mapped.price = resData.price != null ? resData.price : (resData.unitPrice != null ? resData.unitPrice : null);
      // 库存
      mapped.stock = resData.stock != null ? resData.stock : (resData.inventory != null ? resData.inventory : null);
      // 图片：新API返回imageUrl字段
      let pic = resData.picture || resData.image || resData.images || resData.pictures || resData.img || resData.imageUrl;
      if (Array.isArray(pic)) {
        mapped.picture = pic[0] || '';
      } else if (typeof pic === 'string' && pic.trim() !== '') {
        // 如果图片路径不包含/order/前缀，且不是完整URL，则添加前缀
        if (!pic.startsWith('http') && !pic.startsWith('/order/') && !pic.startsWith('/')) {
          mapped.picture = `/order/${pic}`;
        } else if (pic.startsWith('/')) {
          mapped.picture = pic;
        } else {
          mapped.picture = pic;
        }
      } else {
        mapped.picture = '';
      }
      // 卖家信息：新API返回sellerId，需要通过sellerId查询卖家信息
      mapped.sellerId = resData.sellerId;
      mapped.ownName = resData.ownName || resData.owner || resData.seller || resData.userName || '';
      mapped.type = resData.type || (resData.category === 'goods' ? 'goods' : resData.goodsType) || 'goods';
      // 创建时间：Product实体没有createTime字段，使用当前时间或留空
      mapped.createTime = resData.createTime || resData.createdAt || null;
      // 分类和单位
      mapped.category = resData.category || '';
      mapped.unit = resData.unit || '';
      console.log('mapResponseToData: 映射后的数据', mapped);
      return mapped;
    },
    // 使用旧API加载数据（向后兼容）
    loadWithOldAPI(orderId) {
      console.log('尝试使用旧API加载商品信息:', orderId);
      this.loading = true;
      this.error = null;
      selectOrderById({
        order_id: orderId,
      }).then((res) => {
        console.log('旧API响应:', res);
        this.loading = false;
        if (res && res.flag == true && res.data) {
          this.data = this.mapResponseToData(res.data);
          console.log('旧API映射后的商品数据:', this.data);
          // 确保data.id存在
          if (!this.data.id) {
            this.data.id = orderId;
          }
          // 确保orderId存在
          if (!this.data.orderId) {
            this.data.orderId = this.data.id || orderId;
          }
          // 确保data对象不为空
          if (!this.data || Object.keys(this.data).length === 0) {
            console.error('旧API映射后的数据为空');
            this.error = '商品数据格式错误，无法显示';
            this.$message.error(this.error);
            return;
          }
          console.log('旧API最终商品数据:', this.data);
          this.getSalesInfo();
          this.getCommentData();
        } else {
          console.error('旧API返回失败:', res);
          this.error = res?.message || '获取商品信息失败，商品可能不存在';
          this.$message.error(this.error);
        }
      }).catch((err) => {
        console.error('旧API调用失败:', err);
        this.loading = false;
        this.error = err?.message || '获取商品信息失败，请稍后重试';
        this.$message.error(this.error);
      });
    },
  },
  mounted() {
    // 获取商品ID（优先使用productId，否则使用orderId）
    const productId = this.$route.query.productId || this.$route.query.orderId;
    
    console.log('商品详情页加载，商品ID:', productId, '路由参数:', this.$route.query);
    
    if (!productId) {
      this.error = '缺少商品ID参数，无法加载商品信息';
      this.$message.error('缺少商品ID参数');
      console.error('缺少商品ID参数，路由参数:', this.$route.query);
      return;
    }

    this.loading = true;
    this.error = null;

    // 优先使用新的商品API
    console.log('开始调用新商品API，商品ID:', productId);
    getProductDetail(productId).then((res) => {
      console.log('商品详情API响应:', res);
      this.loading = false;
      if (res && res.flag && res.data) {
        console.log('新API成功，商品数据:', res.data);
        this.data = this.mapResponseToData(res.data);
        console.log('映射后的商品数据:', this.data);
        // 确保data.id存在，用于后续评论功能
        if (!this.data.id) {
          this.data.id = productId;
        }
        // 确保orderId存在（用于兼容旧代码）
        if (!this.data.orderId) {
          this.data.orderId = this.data.id || productId;
        }
        console.log('最终商品数据:', this.data);
        // 确保data对象不为空
        if (!this.data || Object.keys(this.data).length === 0) {
          console.error('映射后的数据为空，尝试使用旧API');
          this.loadWithOldAPI(productId);
          return;
        }
        this.getSalesInfo();
        this.getCommentData();
      } else {
        // 如果新API返回flag=false，尝试使用旧API（向后兼容）
        console.warn('新API返回flag=false，尝试旧API', res);
        if (res && res.message) {
          console.warn('新API错误信息:', res.message);
        }
        this.loadWithOldAPI(productId);
      }
    }).catch((err) => {
      console.error('新API调用异常，尝试旧API', err);
      this.loading = false;
      // 如果新API失败，尝试使用旧API（向后兼容）
      this.loadWithOldAPI(productId);
    });
  },
};
</script>


<style lang="less" scoped>
.details-box {
  max-width: 1100px;
  margin: 20px auto;
  padding: 20px;
  min-height: 100vh;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);

  .loading-container {
    min-height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .error-container {
    margin: 40px 0;
  }

  .details-main {
    display: flex;
    gap: 24px;
    align-items: flex-start;
  }

  .left {
    flex: 0 0 300px;
  img {
      width: 100%;
      height: 220px;
      object-fit: cover;
    border-radius: 6px;
      border: 1px solid #f2f2f2;
      background: #fafafa;
    }
  }

  .info {
    flex: 1;
    .title {
      font-size: 26px;
      font-weight: 700;
      margin-bottom: 22px; /* 增加标题与下方内容的间距 */
      letter-spacing: 0.4px;
    }
    .meta {
      display: flex;
      gap: 24px;
      align-items: center;
      margin-bottom: 18px; /* 放宽价格区域与卖家行之间的空隙 */
      .price-block {
        display: flex;
        flex-direction: column;
        gap: 6px;
        align-items: flex-start;
      }
      .product-name-large {
        font-size: 30px;
        font-weight: 700;
        color: #333;
        line-height: 1.1;
        margin-bottom: 6px;
    }
    .price {
        color: #e55353;
        font-size: 20px;
        font-weight: 700;
      }
      .stock {
        color: #666;
        font-size: 14px;
      }
    }
    .time {
      color: #999;
      font-size: 13px;
      margin-bottom: 18px; /* 略微放大时间行与下方描述的间距 */
      display:flex;
      gap:18px;
    }
    .desc {
      color: #666;
      line-height: 1.6;
      border-top: 1px dashed #f2f2f2;
      padding-top: 12px;
      margin-bottom: 16px;
    }
    .actions {
      display: flex;
      gap: 12px;
      align-items: center;
      /* 确保按钮在小屏下不换行并保持一致高度 */
      flex-wrap: nowrap;
  }
  .item-sales{
    color: #333 !important;
    line-height: 30px;
    max-height: 30px;
    .sales-text{
      color: #666;
    }
  }
}

  .comments-section {
    margin-top: 24px;
    border-top: 1px solid #f5f5f5;
    padding-top: 20px;
    .rating-summary {
      display:flex;
      align-items:center;
      gap:12px;
      margin-bottom:12px;
      .rating-label{
        font-weight:700;
        color:#333;
  }
      .rating-count{
        color:#999;
        font-size:13px;
      }
    }
    .comment-list {
      margin-top: 16px;
      .comment-item {
        padding: 16px 0;
        border-bottom: 1px solid #f2f2f2;
        &:last-child {
          border-bottom: none;
        }
        .comment-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          .comment-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            object-fit: cover;
          }
          .comment-author {
            font-weight: 500;
            color: #333;
            font-size: 14px;
          }
          .comment-rating {
            margin-left: auto;
            .star {
              color: #ddd;
              font-size: 14px;
              &.star-filled {
                color: #ffa500;
              }
            }
          }
        }
        .comment-content {
          color: #666;
          line-height: 1.6;
          margin-bottom: 8px;
          font-size: 14px;
        }
        .comment-meta {
          display:flex;
          justify-content: space-between;
          color:#999;
          font-size:12px;
        }
      }
    }
    .no-comments {
      text-align: center;
      padding: 40px 0;
      color: #999;
      font-size: 14px;
    }
  }
  .comments-overview {
    margin-bottom: 12px;
    .rating-count {
        color: #666;
      font-size: 14px;
      font-weight: 500;
    }
  }
}
</style>
