<!--商品货源-->
<template>
  <div class="home-goods">
    <goods-source :cgoods="goods" @handleSearch="handleSearch" @addToCart="handleAddToCart"></goods-source>
    <pagination @item-click="pageClick" :cUrl="url" :cTotal="total" :cPageSize="pageSize"></pagination>
  </div>
</template>

<script>
import { selectGoodsPage } from "../api/trade";
import { addOrderToCart } from "../api/cart";
import GoodsSource from "../components/GoodsSource.vue";
import Pagination from "../components/Pagination.vue";
export default {
  data() {
    return {
      goods: [],
      total: 0,
      pageSize: 30,
      searchValue:'',
      url: "/order/goods/",
      goodsCount: sessionStorage.getItem("/order/goods/pageCode")
        ? sessionStorage.getItem("/order/goods/pageCode")
        : 1,
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "2");
    this.getData()
  },
  methods: {
    getData(){
      selectGoodsPage({
        pageNum: this.goodsCount,
        keys:this.searchValue
      }).then((res) => {
        if (res.flag == true) {
          this.goods = res.data.list;
          this.total = res.data.total;
        } else {
          // alert(res.data.data);
        }
      });
    },
    pageClick(item) {
      this.goods = item;
    },
    handleSearch(val){
      this.searchValue = val
      this.getData()
    },
    handleAddToCart(item) {
      // 处理加入购物车
      if (!item || !item.orderId) {
        this.$message.error('商品信息不完整，无法加入购物车');
        return;
      }
      
      addOrderToCart({
        order_id: item.orderId,
        quantity: 1
      })
        .then((res) => {
          if (res.flag == true) {
            this.$message.success(res.message || '已加入购物车');
          } else {
            this.$message.error(res.message || '加入购物车失败');
          }
        })
        .catch((err) => {
          console.error('加入购物车错误:', err);
          if (err && typeof err === 'object') {
            if (err.flag === false) {
              const errorMsg = err.message || '添加失败';
              if (errorMsg.includes('登录') || err.status === 401) {
                this.$message.warning('请先登录');
              } else {
                this.$message.error(errorMsg);
              }
            } else {
              this.$message.error(err.message || '加入购物车失败，请检查网络连接');
            }
          } else {
            this.$message.error('加入购物车失败');
          }
        });
    }
  },
  components: {
    Pagination,
    GoodsSource,
  },
};
</script>

<style lang="less" scoped>
.home-goods {
  width: 1100px;
  margin: 0 auto;
}
</style>
