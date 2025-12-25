<template>
  <div class="published-message">
    <el-button type="success" @click="publishClick" style="margin: 10px 20px"
      >发布商品{{ cdescribe }}</el-button
    >
    <div class="search" style="text-align: center">
      <el-input
        v-model="searchValue"
        maxlength="100"
        placeholder="请输入商品关键词进行搜索"
        clearable
        style="width: 290px"
      />
      <img
        src="../assets/img/search.png"
        @click="handleSearch"
        class="search-icon"
      />
    </div>
    <div
      style="
        display: flex;
        flex-wrap: wrap;
        background: #fff;
        width: 900px;
        padding: 10px 20px;
      "
    >
      <div class="message" v-for="(item, index) in pagedGoods()" :key="item.orderId || item.id || index">
        <img
          v-if="item.picture"
          :src="$store.state.imgShowRoad + '/file/' + item.picture"
        />
        <img v-else src="../assets/img/wutu.gif" style="height: 177px" />
        <div class="info">
          <div class="title">{{ item.title }}</div>
          <div class="content initiator">{{ truncatedContent(item.content) }}</div>
          <div class="price">￥ {{ item.price }}</div>
        </div>
        <div class="action-area">
          <span class="action-btn edit-btn" @click.stop="goEdit(item.orderId)">修改</span>
          <div class="delete-wrapper" @click.stop="prepareDelete(item.orderId || item.id)">
            <delete-message :ctype="ctype" @deleted="handleDeleted"></delete-message>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-center" style="width:900px; margin: 0 auto;">
      <button class="page-btn" :disabled="currentPage===1" @click="prevPage">‹</button>
      <button
        v-for="p in totalPages()"
        :key="p"
        class="page-number"
        :class="{ active: p===currentPage }"
        @click="goToPage(p)"
      >{{ p }}</button>
      <button class="page-btn" :disabled="currentPage===totalPages()" @click="nextPage">›</button>
    </div>
  </div>
</template>

<script>
import { selectGoodsPage, selectNeedsPage, deleteProduct, deleteDemand } from "../api/trade";
import ChangeMessage from "./ChangeMessage.vue";
import DeleteMessage from "./DeleteMessage.vue";
import Pagination from "./Pagination.vue";
// 补充缺失的导入（原代码中使用了selectOrderById但未导入）
import { selectOrderById } from "../api/trade"; // 根据实际路径调整

export default {
  data() {
    return {
      userGoods: [],
      updateGoodInfo: {},
      pageNum: 1,
      searchValue: "",
      total: 0,
      pageSize: 30,
      // client-side pagination
      currentPage: 1,
      pageSizeDisplay: 2,
      url: "/order/search/",
    };
  },
  components: { ChangeMessage, DeleteMessage, Pagination },
  props: {
    ctype: {
      type: String,
      required: true, // 增加必填校验，提高健壮性
    },
    cdescribe: { // 修正拼写错误：cdesciibe → cdescribe
      type: String,
      required: true,
    },
  },
  methods: {
    pageClick(item) {
      console.log("item", item);
      this.userGoods = item;
    },
    publishClick() {
      this.$router
        .push("/home/addmessage/publish" + this.ctype)
        .catch((err) => err);
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
    goEdit(id) {
      // 跳转到编辑页面
      if (!id) {
        this.$message.error("缺少商品ID");
        return;
      }
      this.$router.push({ path: `/home/edit-product/${id}` }).catch(() => {});
    },
    prepareDelete(itemId) {
      // 将要操作的 id 写入 store，DeleteMessage 组件会读取并执行下架/删除
      this.$store.commit("updateChangedOrderId", itemId);
    },
    handleDeleted() {
      // 删除成功后刷新列表
      this.getData();
    },
    deleteInfo(item) {
      this.$store.commit("updateChangedOrderId", item);
      // 直接调用删除，删除后刷新
      const deletePromise = this.ctype === "needs" 
        ? deleteDemand(item) 
        : deleteProduct(item);
      
      deletePromise
        .then(() => {
          this.$message.success("删除成功");
          this.getData();
        })
        .catch(() => {
          this.$message.error("删除失败");
        });
    },
    handleSearch() {
      this.getData();
    },
    getData() {
      const userId = this.$store.state.loginUserId;
      // 校验用户ID
      if (!userId) {
        this.$message.warning("未获取到用户信息，请重新登录");
        this.userGoods = [];
        this.total = 0;
        return;
      }

      const requestParams = {
        pageNum: this.pageNum,
        keys: this.searchValue,
      };

      // 根据类型设置不同的请求参数和接口
      let requestPromise;
      if (this.ctype === "needs") {
        requestPromise = selectNeedsPage({ ...requestParams, userId });
      } else {
        requestPromise = selectGoodsPage({ ...requestParams, sellerId: userId });
      }

      requestPromise
        .then((res) => {
          this.userGoods = res.data.list || [];
          this.total = res.data.total || 0; // 优化默认值
          // reset client paging
          this.currentPage = 1;
        })
        .catch((err) => {
          console.error("获取数据失败：", err);
          this.userGoods = [];
          this.total = 0;
          this.$message.error("获取数据失败，请稍后重试");
        });
    },
    truncatedContent(text) {
      if (!text) return "";
      const str = String(text);
      const max = 15;
      return str.length > max ? str.slice(0, max) + "..." : str;
    },
    // client-side paging helpers
    pagedGoods() {
      const start = (this.currentPage - 1) * this.pageSizeDisplay;
      return this.userGoods.slice(start, start + this.pageSizeDisplay);
    },
    totalPages() {
      return Math.max(1, Math.ceil((this.userGoods.length || this.total) / this.pageSizeDisplay));
    },
    goToPage(p) {
      if (p < 1) p = 1;
      if (p > this.totalPages()) p = this.totalPages();
      this.currentPage = p;
      // scroll into view
      this.$nextTick(() => {
        const el = this.$el;
        if (el) el.scrollIntoView({ behavior: "smooth", block: "start" });
      });
    },
    prevPage() {
      this.goToPage(this.currentPage - 1);
    },
    nextPage() {
      this.goToPage(this.currentPage + 1);
    },
  },
  created() {
    this.getData();
  },
};
</script>

<style lang="less" scoped>
.search {
  height: 60px;
  background-color: white;
  padding: 10px 20px;
  background-color: white;
  margin-top: 10px;
  
  .search-icon {
    position: relative;
    bottom: 2px;
    left: -2px;
    cursor: pointer;
  }
}

.published-message {
  width: 900px;
  margin: 0 auto;
  height: 100%;
  background: #fff;
  padding-bottom: 100px;

  .message {
    box-sizing: border-box;
    width: 760px;
    height: auto;
    border: 1px solid #f2f2f2;
    background-color: white;
    border-radius: 8px;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    margin: 12px auto;
    padding: 12px;
    background: #fff;

    img {
      width: 260px;
      height: 160px;
      margin-right: 12px;
      border-radius: 6px;
      object-fit: cover;
    }

    .info {
      /* ensure info column centers its content so action-area aligns consistently */
      display: flex;
      flex-direction: column;
      justify-content: center;
      flex: 1;
      margin-top: 0;

      .item-style {
        height: 20px;
        line-height: 20px;
      }

      .initiator {
        color: #666;
      }

      .title {
        font-size: 20px;
        height: 30px;
        font-weight: 700;
        color: #1f2c3b;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .content {
        line-height: 20px;
        margin-top: 8px;
        word-break: break-word;
        color: #9aa0a6; /* gray */
        font-size: 14px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .price {
        font-size: 20px;
        font-weight: 800;
        display: block;
        color: #e64c3c;
        margin-top: 8px;
      }
    }
  }

  .action-area {
    flex: 0 0 140px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding-left: 12px;
  }

  .action-area .action-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    min-width: 96px;
    height: 40px;
    padding: 0 18px;
    border-radius: 8px;
    background: #2e8b57;
    color: #fff;
    font-weight: 700;
    font-size: 16px;
    cursor: pointer;
    transition: all .12s ease;
    border: 1px solid rgba(46,139,87,0.18);
    box-shadow: 0 6px 12px rgba(46,139,87,0.06);
  }

  .action-area .action-btn:hover {
    background: #237a48;
    transform: translateY(-1px);
  }

  .delete-wrapper {
    display: inline-block;
  }

  .action-area ::v-deep .delete-text {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    min-width: 96px;
    height: 40px;
    padding: 0 18px;
    font-size: 16px;
    color: #2e8b57;
    background: #fff;
    border: 1px solid rgba(46,139,87,0.18);
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 4px 10px rgba(46,139,87,0.03);
    cursor: pointer;
  }

  .pagination-center {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin: 18px 0 28px;
  }
  .pagination-center .page-number,
  .pagination-center .page-btn {
    height: 36px;
    min-width: 36px;
    padding: 0 10px;
    border-radius: 6px;
    border: 1px solid #e6e6e6;
    background: #fafafa;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
  .pagination-center .page-number.active {
    background: #2d8cf0;
    color: #fff;
    border-color: #2d8cf0;
  }
  .pagination-center .page-btn[disabled] {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.search /deep/ .el-input--suffix .el-input__inner {
  height: 35px;
  line-height: 35px;
}
</style>