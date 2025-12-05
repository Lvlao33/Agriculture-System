<template>
  <div class="all-expert-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">专家信息</h1>
      <p class="page-subtitle">汇聚农业领域专业人才，为您提供全方位的技术指导与咨询服务</p>
      <div class="expert-stats">
        <span class="stat-item">当前已入驻 <strong>{{ total }}</strong> 位专家</span>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="page-content">
      <expert-source :cgoods="goods" @handleSearch="handleSearch"></expert-source>
    </div>
  </div>
</template>
<script>
import { selectExperts } from "../api/expert";
import ExpertSource from "../components/ExpertSource.vue";
export default {
  data() {
    return {
      goods: [],
      total: 0,
      pageSize: 50,
      searchValue:'',
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "5");
    this.getData()
  },
  methods: {
    handleSearch(val){
      this.searchValue = val
      this.getData()
    },
    getData(){
      selectExperts({
        pageNum: 1,
        pageSize: this.pageSize,
        keys: this.searchValue
      }).then((res) => {
        if (res.flag === true && res.data) {
          this.goods = res.data.list || [];
          this.total = res.data.total || 0;
        } else {
          this.goods = [];
          this.total = 0;
        }
      }).catch(err => {
        console.error(err);
        this.goods = [];
        this.total = 0;
      });
    }
  },
  components: {
    ExpertSource,
  },
};
</script>

<style lang="less" scoped>
.all-expert-page {
  width: 100%;
  min-height: calc(100vh - 200px);
  background: linear-gradient(to bottom, #f0f9f4 0%, #f5f5f5 300px);
  padding-bottom: 40px;

  .page-header {
    width: 100%;
    background: linear-gradient(135deg, #67C23A 0%, #035D1C 100%);
    padding: 50px 0;
    text-align: center;
    color: white;
    margin-bottom: 30px;
    box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);

    .page-title {
      font-size: 36px;
      font-weight: bold;
      margin: 0 0 15px 0;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .page-subtitle {
      font-size: 16px;
      margin: 0 0 25px 0;
      opacity: 0.95;
      line-height: 1.6;
    }

    .expert-stats {
      .stat-item {
        display: inline-block;
        font-size: 16px;
        padding: 8px 20px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        backdrop-filter: blur(10px);

        strong {
          font-size: 20px;
          font-weight: bold;
          margin: 0 5px;
        }
      }
    }
  }

  .page-content {
    width: 1100px;
    margin: 0 auto;
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .all-expert-page .page-content {
    width: 95%;
    padding: 0 20px;
  }
}

@media (max-width: 768px) {
  .all-expert-page {
    .page-header {
      padding: 30px 20px;

      .page-title {
        font-size: 28px;
      }

      .page-subtitle {
        font-size: 14px;
      }
    }
  }
}
</style>