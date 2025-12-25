<template>
  <div class="details-box">
    <div class="details-main">
    <div class="left">
        <img v-if="data.picture" :src="`/order/${data.picture}`" alt="商品图片" />
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
          <span>卖家：{{ updateUserData.userName || data.ownName || '未知' }}</span>
          <span>库存：{{ data.stock != null ? data.stock : '—' }}</span>
          <span>发布时间：{{ data.createTime | changeTime }}</span>
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
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { addOrderToCart,addOrderToCollect } from "../api/cart";
import { selectOrderById,selectComment,addComment } from "../api/order";
import ChangeMessage from "../components/ChangeMessage.vue";
import { selectUserByUsername } from "../api/user";

export default {
  data() {
    return {

      content:'',
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
    // 查询评论：映射后端返回字段到组件所需结构；若后端返回空数组则保留初始测试数据
    getCommentData(){
      selectComment({
        orderId: this.$route.query.orderId
      }).then(res => {
        try {
          const serverComments = (res && res.data && Array.isArray(res.data)) ? res.data : [];
          if (serverComments.length > 0) {
            // 映射后端评论字段到 { ownName, content, createTime }
            this.commentArray = serverComments.map(item => {
              return {
                ownName: item.ownName || item.userName || item.username || item.nick || ('匿名' + Math.floor(Math.random()*9000+1000)),
                content: item.content || item.text || item.comment || '',
                createTime: item.createTime || item.createdAt || item.time || new Date().toISOString()
              };
            });
          }
          // 若后端无数据，保持组件内置的测试评论（用于演示）
        } catch (e) {
          console.error('处理评论数据失败', e);
        }
      }).catch(err=>{
        // 网络或接口错误时保留本地测试评论，便于演示
        console.log(err)
      })
    },
    handleComment(){
      if(this.content===''){
        this.$message.error('评论内容不能为空！')
        return
      }
      if(localStorage.getItem('token')){
        addComment({
          orderId: this.$route.query.orderId,
          content: this.content
        }).then(res=>{
          this.content=''
          this.$message.success('评论成功！')
          this.getCommentData()
        }).catch(err=>{
          console.log(err)
        })
      }else{
        this.$message.error('请先登录')
      }
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
    onBuyNow() {
      if (this.data && this.data.type === 'goods') {
        this.handleBuyNow(this.data);
      } else {
        this.$message.warning('该商品暂不可购买');
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
      selectUserByUsername({
        user_name: this.data.ownName,
      }).then((res) => {
        this.updateUserData = res.data;
      }).catch(err=>{
        console.log(err);
      })
    }
    ,
    goBackGoodsSource() {
      // 返回到 商品货源 页面
      this.$router.push('/home/goods').catch(() => {});
    }
    ,
    mapResponseToData(resData) {
      // 统一后端返回字段到组件需要的 data 字段
      if (!resData || typeof resData !== 'object') return {};
      const mapped = {};
      mapped.orderId = resData.orderId || resData.id || resData.order_id || resData.productId;
      mapped.title = resData.title || resData.name || resData.productName || resData.goodsName;
      mapped.name = mapped.title;
      mapped.content = resData.content || resData.description || resData.intro || resData.desc || '';
      mapped.price = resData.price != null ? resData.price : (resData.unitPrice != null ? resData.unitPrice : null);
      mapped.stock = resData.stock != null ? resData.stock : (resData.inventory != null ? resData.inventory : null);
      // 图片可能是单条或数组或字段名不同
      let pic = resData.picture || resData.image || resData.images || resData.pictures || resData.img;
      if (Array.isArray(pic)) {
        mapped.picture = pic[0] || '';
      } else if (typeof pic === 'string') {
        mapped.picture = pic;
      } else if (resData.imageUrl) {
        mapped.picture = resData.imageUrl;
      } else {
        mapped.picture = '';
      }
      mapped.ownName = resData.ownName || resData.owner || resData.seller || resData.userName;
      mapped.type = resData.type || (resData.category === 'goods' ? 'goods' : resData.goodsType);
      return mapped;
    },
  },
  mounted() {
    this.getCommentData()

    selectOrderById({
      order_id: this.$route.query.orderId,
    }).then((res) => {
      if (res.flag == true) {
        this.data = this.mapResponseToData(res.data);
        this.getSalesInfo()
      }
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
    .comment-editor {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .comment-actions {
      display: flex;
      justify-content: flex-end;
    }
    .comment-list {
      margin-top: 16px;
      .comment-item {
        padding: 12px 0;
        border-bottom: 1px solid #f2f2f2;
        .comment-meta {
          display:flex;
          justify-content: space-between;
          color:#999;
          font-size:12px;
          margin-top:8px;
        }
      }
      .more-comments {
        text-align: center;
        margin-top: 12px;
      }
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
