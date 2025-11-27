<template>
  <div class="purchase-page">
    <el-backtop target=".home-content"></el-backtop>

    <!-- 顶部横幅 -->
    <div class="purchase-hero">
      <div class="hero-content">
        <div>
          <p class="hero-label">发布需求  快速匹配</p>
          <h1 class="hero-title">农产品采购</h1>
          <p class="hero-desc">
            农产品供需发布平台、智能匹配优质货源、一站式采购解决方案
          </p>
          <div class="hero-actions">
            <el-button type="primary" icon="el-icon-plus" @click="handlePublish">发布需求</el-button>
            <el-button type="success" icon="el-icon-s-shop" @click="goToGoods">查看货源</el-button>
          </div>
        </div>
        <div class="hero-steps">
          <div class="step-card" v-for="step in processSteps" :key="step.title">
            <div class="step-icon">{{ step.icon }}</div>
            <div class="step-info">
              <div class="step-title">{{ step.title }}</div>
              <div class="step-desc">{{ step.desc }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="purchase-layout">
      <!-- 主内容区 -->
      <div class="main-column">
        <!-- 筛选卡片 -->
        <div class="filter-card">
          <div class="filter-row">
            <el-input
              v-model="searchValue"
              placeholder="搜索产品、品类..."
              clearable
              style="width: 260px;"
              @keyup.enter.native="applyFilters"
            >
              <el-button slot="append" icon="el-icon-search" @click="applyFilters"></el-button>
            </el-input>
            <el-select v-model="filters.category" placeholder="产品品类" clearable style="width: 150px;" @change="applyFilters">
              <el-option label="全品类" value="all" />
              <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
            </el-select>
            <el-select v-model="filters.urgency" placeholder="紧急程度" clearable style="width: 150px;" @change="applyFilters">
              <el-option label="全部" value="all" />
              <el-option label="24小时内" value="urgent" />
              <el-option label="近3天内" value="normal" />
              <el-option label="长期有效" value="long_term" />
            </el-select>
            <el-select v-model="filters.quantity" placeholder="采购规模" clearable style="width: 150px;" @change="applyFilters">
              <el-option label="全部" value="all" />
              <el-option label="零售采购" value="retail" />
              <el-option label="批发采购" value="wholesale" />
              <el-option label="大宗采购" value="bulk" />
            </el-select>
            <el-select v-model="filters.status" placeholder="状态" clearable style="width: 150px;" @change="applyFilters">
              <el-option label="全部" value="all" />
              <el-option label="开放中" value="open" />
              <el-option label="对接中" value="in_progress" />
              <el-option label="已完成" value="completed" />
            </el-select>
          </div>
          <div class="filter-tags">
            <span>快速筛选</span>
            <el-tag
              v-for="tag in quickTags"
              :key="tag.value"
              @click="applyQuickTag(tag)"
              :type="filters.urgency === tag.value ? 'success' : 'info'"
              class="quick-tag"
            >
              {{ tag.label }}
            </el-tag>
          </div>
        </div>

        <!-- 需求列表 -->
        <div class="demand-list">
          <div
            class="demand-card"
            v-for="(item, index) in filteredDemands"
            :key="index"
            @click="openDemandDetail(item)"
          >
            <div class="demand-card-header">
              <div>
                <div class="demand-title">{{ item.title }}</div>
                <div class="demand-meta">
                  <el-tag size="mini" :type="getUrgencyTagType(item.urgency)">{{ getUrgencyLabel(item.urgency) }}</el-tag>
                  <el-tag size="mini" type="warning">{{ getQuantityLabel(item.quantity) }}</el-tag>
                  <el-tag size="mini" type="info">{{ getQualityLabel(item.quality) }}</el-tag>
                  <el-tag size="mini" effect="dark" :type="getStatusTagType(item.status)">{{ getStatusLabel(item.status) }}</el-tag>
                </div>
              </div>
              <div class="demand-price">
                <div class="price-label">期望价格</div>
                <div class="price-value">{{ item.desiredPrice || '面议' }}</div>
              </div>
            </div>
            <div class="demand-content">
              {{ item.content }}
            </div>
            <div class="demand-footer">
              <div class="footer-info">
                <span><i class="el-icon-s-custom"></i> {{ item.publisher }}</span>
                <span><i class="el-icon-location-outline"></i> {{ item.location || '未填写' }}</span>
                <span><i class="el-icon-time"></i> 截止 {{ formatDate(item.deadline) }}</span>
              </div>
              <div class="footer-actions">
                <el-button type="primary" size="mini" @click.stop="contactBuyer(item)">联系采购</el-button>
                <el-button size="mini" @click.stop="openDemandDetail(item)">查看详情</el-button>
              </div>
            </div>
          </div>
          <div v-if="filteredDemands.length === 0" class="empty-state">
            <i class="el-icon-box"></i>
            <p>暂无需求信息</p>
          </div>
        </div>
      </div>

      <!-- 侧边栏 -->
      <div class="side-column">
        <div class="info-card">
          <div class="card-title">采购流程  一目了然</div>
          <div class="timeline">
            <div class="timeline-step" v-for="(step, index) in demandFlow" :key="index">
              <div class="step-index">{{ index + 1 }}</div>
              <div class="step-body">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-desc">{{ step.desc }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="info-card">
          <div class="card-title">数据统计</div>
          <div class="stats">
            <div class="stat-item" v-for="stat in stats" :key="stat.label">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情抽屉 -->
    <el-drawer
      title="需求详情"
      :visible.sync="detailDrawerVisible"
      direction="rtl"
      size="36%"
      :with-header="true"
    >
      <div v-if="activeDemand" class="drawer-content">
        <div class="drawer-section">
          <div class="drawer-title">{{ activeDemand.title }}</div>
          <div class="drawer-tags">
            <el-tag type="success">{{ getUrgencyLabel(activeDemand.urgency) }}</el-tag>
            <el-tag type="warning">{{ getQuantityLabel(activeDemand.quantity) }}</el-tag>
            <el-tag type="info">{{ getQualityLabel(activeDemand.quality) }}</el-tag>
            <el-tag effect="dark" :type="getStatusTagType(activeDemand.status)">{{ getStatusLabel(activeDemand.status) }}</el-tag>
          </div>
          <p class="drawer-content-text">{{ activeDemand.content }}</p>
        </div>
        <div class="drawer-section">
          <div class="section-subtitle">需求信息</div>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="产品品类">{{ getCategoryLabel(activeDemand.category) }}</el-descriptions-item>
            <el-descriptions-item label="期望价格">{{ activeDemand.desiredPrice || '面议' }}</el-descriptions-item>
            <el-descriptions-item label="采购规模">{{ getQuantityLabel(activeDemand.quantity) }}</el-descriptions-item>
            <el-descriptions-item label="品质要求">{{ getQualityLabel(activeDemand.quality) }}</el-descriptions-item>
            <el-descriptions-item label="交货地点">{{ activeDemand.location || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="截止时间">{{ formatDate(activeDemand.deadline) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="drawer-section">
          <div class="section-subtitle">发布方信息</div>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="发布单位">{{ activeDemand.publisher }}</el-descriptions-item>
            <el-descriptions-item label="联系方式">{{ activeDemand.contact || '登录查看' }}</el-descriptions-item>
            <el-descriptions-item label="联系职位">{{ activeDemand.position || '采购专员' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="drawer-section">
          <div class="section-subtitle">立即行动</div>
          <el-button type="primary" icon="el-icon-phone" @click="contactBuyer(activeDemand)">立即联系</el-button>
          <el-button icon="el-icon-share" style="margin-left:12px;">分享需求</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { selectNeedsPage } from "../api/order";

export default {
  name: "HomePurchase",
  data() {
    return {
      demands: [],
      filteredDemands: [],
      searchValue: "",
      filters: {
        category: "all",
        urgency: "all",
        quantity: "all",
        quality: "all",
        status: "all"
      },
      categoryOptions: [
        { value: "fruit", label: "水果" },
        { value: "vegetable", label: "蔬菜" },
        { value: "grain", label: "粮食" },
        { value: "livestock", label: "畜牧产品" },
        { value: "other", label: "其他" }
      ],
      quickTags: [
        { label: "紧急采购", value: "urgent" },
        { label: "批发采购", value: "wholesale" },
        { label: "大宗采购", value: "bulk" }
      ],
      detailDrawerVisible: false,
      activeDemand: null,
      processSteps: [
        { icon: "📝", title: "发布需求", desc: "填写需求信息确认产品品类、要求" },
        { icon: "🔍", title: "智能匹配", desc: "平台智能匹配和推荐对口供应商" },
        { icon: "💬", title: "在线沟通", desc: "支持在线沟通及时确认细节" },
        { icon: "🤝", title: "签约合作", desc: "确认最终价格细节签订合同" }
      ],
      demandFlow: [
        { title: "发布需求", desc: "采购方在平台发布" },
        { title: "供应商响应", desc: "供应商查看并响应" },
        { title: "确认细节", desc: "确认价格细节、质量" },
        { title: "签订合同", desc: "在线签订合同" },
        { title: "完成交易", desc: "货物交付完成" }
      ],
      stats: [
        { label: "今日需求", value: 0 },
        { label: "紧急采购", value: 0 },
        { label: "进行中项目", value: 0 }
      ],
      defaultDemands: [
        {
          id: "need1",
          title: "急需苹果5吨",
          content: "上海农贸公司急需一批红富士苹果要求有机认证产地直供支持到付地址上海",
          category: "fruit",
          urgency: "urgent",
          quantity: "bulk",
          quality: "premium",
          desiredPrice: "8-10 元/公斤",
          location: "上海闵行",
          deadline: "2025-12-15",
          publisher: "上海农贸公司",
          contact: "138****5678",
          position: "采购经理",
          status: "open"
        },
        {
          id: "need2",
          title: "长期采购大米",
          content: "需要稳定的东北大米供应商要求水分含量低于15%蛋白质含量7.5%以上真空包装",
          category: "grain",
          urgency: "long_term",
          quantity: "bulk",
          quality: "premium",
          desiredPrice: "5200-5600 元/吨",
          location: "广州白云",
          deadline: "2026-01-20",
          publisher: "广州超市集团",
          contact: "137****2323",
          position: "供应商管理",
          status: "in_progress"
        },
        {
          id: "need3",
          title: "批发采购蔬菜套餐",
          content: "连锁餐饮平台每天需要蔬菜套餐包含叶菜类、根茎类和瓜果类保证新鲜直达门店。",
          category: "vegetable",
          urgency: "normal",
          quantity: "wholesale",
          quality: "normal",
          desiredPrice: "38 元/份，每份5斤",
          location: "深圳宝安",
          deadline: "2025-12-05",
          publisher: "连锁餐饮集团",
          contact: "136****9988",
          position: "采购专员",
          status: "open"
        },
        {
          id: "need4",
          title: "寻找散养鸡蛋",
          content: "需要全国范围内的散养鸡蛋供应商要求无抗生素无激素每月稳定供应 20000 枚",
          category: "livestock",
          urgency: "long_term",
          quantity: "bulk",
          quality: "premium",
          desiredPrice: "68 元/箱，每箱30枚",
          location: "杭州西湖区",
          deadline: "2025-12-20",
          publisher: "健康食品公司",
          contact: "139****8866",
          position: "产品经理",
          status: "in_progress"
        },
        {
          id: "need5",
          title: "采购优质猪肉",
          content: "需要稳定的猪肉供应商要求检疫合格证冷鲜包装运输符合国家标准",
          category: "livestock",
          urgency: "normal",
          quantity: "bulk",
          quality: "premium",
          desiredPrice: "21000-23000 元/吨",
          location: "南京鼓楼",
          deadline: "2025-12-08",
          publisher: "餐饮连锁",
          contact: "135****4567",
          position: "采购总监",
          status: "open"
        }
      ]
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
    this.loadDemands();
  },
  methods: {
    loadDemands() {
      selectNeedsPage({
        pageNum: 1,
        keys: ""
      })
        .then((res) => {
          if (res.flag === true && res.data.list && res.data.list.length > 0) {
            this.demands = res.data.list;
          } else {
            this.demands = this.defaultDemands;
          }
          this.applyFilters();
          this.updateStats();
        })
        .catch(() => {
          this.demands = this.defaultDemands;
          this.applyFilters();
          this.updateStats();
        });
    },
    applyFilters() {
      let list = [...this.demands];

      if (this.filters.category !== "all") {
        list = list.filter((item) => item.category === this.filters.category);
      }
      if (this.filters.urgency !== "all") {
        list = list.filter((item) => item.urgency === this.filters.urgency);
      }
      if (this.filters.quantity !== "all") {
        list = list.filter((item) => item.quantity === this.filters.quantity);
      }
      if (this.filters.quality !== "all") {
        list = list.filter((item) => item.quality === this.filters.quality);
      }
      if (this.filters.status !== "all") {
        list = list.filter((item) => item.status === this.filters.status);
      }
      if (this.searchValue) {
        const keyword = this.searchValue.toLowerCase();
        list = list.filter((item) => {
          return (
            (item.title && item.title.toLowerCase().includes(keyword)) ||
            (item.content && item.content.toLowerCase().includes(keyword)) ||
            (item.publisher && item.publisher.toLowerCase().includes(keyword)) ||
            (item.category && this.getCategoryLabel(item.category).includes(this.searchValue))
          );
        });
      }
      this.filteredDemands = list;
    },
    applyQuickTag(tag) {
      this.filters.urgency = tag.value;
      this.applyFilters();
    },
    handlePublish() {
      this.$router.push("/home/publishNeed").catch((err) => err);
    },
    goToGoods() {
      this.$router.push("/home/goods").catch((err) => err);
    },
    contactBuyer(item) {
      this.$message.info(`请登录查看 ${item.publisher} 联系方式`);
    },
    openDemandDetail(item) {
      this.activeDemand = item;
      this.detailDrawerVisible = true;
    },
    updateStats() {
      const todayCount = this.demands.length;
      const urgentCount = this.demands.filter((item) => item.urgency === "urgent").length;
      const runningCount = this.demands.filter((item) => item.status === "in_progress").length;
      this.stats = [
        { label: "今日需求", value: todayCount },
        { label: "紧急采购", value: urgentCount },
        { label: "对接中项目", value: runningCount }
      ];
    },
    getUrgencyLabel(value) {
      const map = {
        urgent: "24小时内",
        normal: "近3天内",
        long_term: "长期有效"
      };
      return map[value] || "";
    },
    getUrgencyTagType(value) {
      const map = {
        urgent: "danger",
        normal: "warning",
        long_term: "success"
      };
      return map[value] || "info";
    },
    getQuantityLabel(value) {
      const map = {
        retail: "零售采购",
        wholesale: "批发采购",
        bulk: "大宗采购"
      };
      return map[value] || "采购";
    },
    getQualityLabel(value) {
      const map = {
        normal: "普通品质",
        premium: "优质品质",
        superior: "特级品质"
      };
      return map[value] || "普通品质";
    },
    getStatusLabel(value) {
      const map = {
        open: "开放中",
        in_progress: "对接中",
        completed: "已完成"
      };
      return map[value] || "开放中";
    },
    getStatusTagType(value) {
      const map = {
        open: "success",
        in_progress: "warning",
        completed: "info"
      };
      return map[value] || "info";
    },
    getCategoryLabel(value) {
      const option = this.categoryOptions.find((cat) => cat.value === value);
      return option ? option.label : "";
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    }
  },
  watch: {
    filters: {
      deep: true,
      handler() {
        this.applyFilters();
      }
    }
  }
};
</script>

<style lang="less" scoped>
.purchase-page {
  width: 1100px;
  margin: 0 auto;
  padding: 20px 0 40px;
}

.purchase-hero {
  background: linear-gradient(120deg, #66c23a 0%, #60b4ff 100%);
  border-radius: 16px;
  padding: 30px 35px;
  color: #fff;
  margin-bottom: 25px;

  .hero-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 30px;
  }

  .hero-label {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 8px;
  }

  .hero-title {
    font-size: 34px;
    margin: 0 0 10px;
  }

  .hero-desc {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 15px;
  }

  .hero-actions {
    display: flex;
    gap: 12px;
  }

  .hero-steps {
    display: grid;
    grid-template-columns: repeat(2, minmax(200px, 1fr));
    gap: 12px;
  }

  .step-card {
    background: rgba(255, 255, 255, 0.15);
    border-radius: 10px;
    padding: 12px;
    display: flex;
    gap: 12px;
    align-items: flex-start;
  }

  .step-icon {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.25);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
  }

  .step-title {
    font-weight: bold;
    font-size: 14px;
  }

  .step-desc {
    font-size: 12px;
    opacity: 0.9;
  }
}

.purchase-layout {
  display: flex;
  gap: 20px;
}

.main-column {
  flex: 3;
}

.side-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;

  .filter-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  .filter-tags {
    margin-top: 12px;
    font-size: 13px;
    color: #606266;

    .quick-tag {
      margin-left: 8px;
      cursor: pointer;
    }
  }
}

.demand-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.demand-card {
  background: #fff;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .demand-card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
  }

  .demand-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  .demand-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .demand-price {
    text-align: right;

    .price-label {
      font-size: 12px;
      color: #909399;
    }

    .price-value {
      font-size: 20px;
      color: #f56c6c;
      font-weight: bold;
    }
  }

  .demand-content {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 12px;
  }

  .demand-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #909399;

    .footer-info {
      display: flex;
      gap: 15px;
    }

    .footer-actions {
      display: flex;
      gap: 8px;
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;

  i {
    font-size: 60px;
    margin-bottom: 15px;
  }

  p {
    font-size: 14px;
  }
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  .card-title {
    font-weight: bold;
    margin-bottom: 15px;
    font-size: 16px;
  }
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.timeline-step {
  display: flex;
  gap: 12px;
  align-items: flex-start;

  .step-index {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: #409eff;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    flex-shrink: 0;
  }

  .step-title {
    font-weight: bold;
    font-size: 13px;
    margin-bottom: 4px;
  }

  .step-desc {
    font-size: 12px;
    color: #606266;
    line-height: 1.4;
  }
}

.stats {
  display: flex;
  justify-content: space-around;
  text-align: center;

  .stat-item {
    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #409eff;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 12px;
      color: #909399;
    }
  }
}

.drawer-content {
  padding: 0 20px;

  .drawer-section {
    margin-bottom: 25px;
  }

  .drawer-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 12px;
  }

  .drawer-tags {
    margin-bottom: 15px;

    .el-tag {
      margin-right: 8px;
      margin-bottom: 8px;
    }
  }

  .drawer-content-text {
    line-height: 1.6;
    color: #606266;
  }

  .section-subtitle {
    font-weight: bold;
    margin-bottom: 12px;
    font-size: 15px;
  }
}
</style>