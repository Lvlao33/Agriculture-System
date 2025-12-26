<template>
  <div class="bank-dashboard">
    <div class="hero">
      <div class="hero-text">
        <p class="hero-badge">银行端 · 融资管理</p>
        <h1>欢迎回来，金融服务专员！</h1>
        <p class="hero-desc">今日共有 {{ overview.pendingLoans }} 笔贷款待审核，当前共 {{ overview.matchedFarmersIntention }} 个意向农户待处理。请优先处理高优先级业务。</p>
        <div class="hero-actions">
          <el-button type="primary" round @click="handleAction('loan')">进入审批列表</el-button>
          <el-button type="success" round plain @click="handleAction('unassigned')">智能匹配</el-button>
        </div>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="stat in statCards" :key="stat.label">
        <div class="stat-label">{{ stat.label }}</div>
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-trend" :class="stat.trendClass">{{ stat.trend }}</div>
      </div>
    </div>

    <div class="content-grid">
      <div class="main-col">
        <section class="panel">
          <div class="panel-header">
            <h2>贷款审批队列</h2>
            <el-link type="primary" @click="handleAction('loan')">全部审批 ></el-link>
          </div>
          <div class="loan-list">
            <div class="loan-item" v-for="loan in loanList" :key="loan.id">
              <div class="loan-info">
                <div class="loan-title">{{ loan.farmer }} {{ loan.product }}</div>
                <div class="loan-meta">
                  <span>金额：¥{{ loan.amount.toLocaleString() }}</span>
                  <span>期限：{{ loan.term }}</span>
                  <span>申请时间：{{ loan.time }}</span>
                </div>
              </div>
              <span class="status-tag" :class="loan.level">{{ loan.levelText }}</span>
            </div>
            <div v-if="loanList.length === 0" class="empty">暂无贷款待处理</div>
          </div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>未分配贷款队列</h2>
            <el-link type="primary" @click="handleAction('unassigned')">查看全部 ></el-link>
          </div>
          <div class="match-list">
            <div class="match-item" v-for="loan in matchList" :key="loan.id">
              <div>
                <div class="match-title">{{ loan.farmer }} {{ loan.product }}</div>
                <div class="match-meta">
                  <span>金额：¥{{ loan.amount ? loan.amount.toLocaleString() : '0' }}</span>
                  <span>期限：{{ loan.term }}</span>
                  <span>申请时间：{{ loan.time }}</span>
                </div>
              </div>
              <el-button size="mini" type="primary" @click="handleAssign(loan)">分配负责人</el-button>
            </div>
            <div v-if="matchList.length === 0" class="empty">暂无待分配贷款</div>
          </div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>审批进度总览</h2>
          </div>
          <div class="progress-grid">
            <div class="progress-item" v-for="stage in stages" :key="stage.title">
              <div class="progress-top">
                <span>{{ stage.title }}</span>
                <strong>{{ stage.value }}</strong>
              </div>
              <el-progress :percentage="stage.percent" :color="stage.color"></el-progress>
            </div>
          </div>
        </section>
      </div>

      <div class="side-col">
        <section class="panel">
          <div class="panel-header">
            <h2>工作日历</h2>
          </div>
          <div class="calendar">
            <div
              v-for="(item, index) in calendar"
              :key="index"
              :class="['calendar-item', { active: item.active }]"
            >
              <div class="calendar-date">{{ item.date }}</div>
              <div class="calendar-text">{{ item.text }}</div>
            </div>
          </div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>消息通知</h2>
          </div>
          <div class="notice-list">
            <div class="notice-item" v-for="notice in notices" :key="notice.id">
              <div class="notice-title">{{ notice.content }}</div>
              <div class="notice-meta">{{ notice.time }}</div>
            </div>
            <div v-if="notices.length === 0" class="empty">暂无通知</div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getBankOverview,
  getBankLoans,
  getBankMatches,
  getBankNotifications,
  getLoanStatusStats
} from "../api/bank";

export default {
  name: "HomeBank",
  data() {
    return {
      overview: {
        pendingLoans: 0,
        unassignedLoans: 0,
        matchedFarmersIntention: 0
      },
      loanList: [],
      matchList: [],
      notices: [],
      calendar: [],
      stages: [],
      fallbackLoans: [
        { id: "L001", farmer: "张家湾合作社", product: "智慧温室升级", amount: 1200000, term: "24 个月", time: "09:15", level: "urgent", levelText: "高优" },
        { id: "L002", farmer: "吴川果农", product: "柑橘冷链扩建", amount: 600000, term: "18 个月", time: "08:40", level: "normal", levelText: "常规" },
        { id: "L003", farmer: "青川牧业", product: "饲料采购周转", amount: 350000, term: "12 个月", time: "昨天 17:20", level: "review", levelText: "复核" }
      ],
      fallbackMatches: [
        { id: "M001", farmer: "黎川茶业", crop: "白茶加工", need: 800000, credit: "A级", bank: "绿色农业银行" },
        { id: "M002", farmer: "广水水产", crop: "小龙虾育苗", need: 500000, credit: "B+级", bank: "乡村振兴银行" }
      ],
      fallbackAlerts: [
        { id: "A001", title: "账户流动性异常", desc: "吴川果农近 7 日现金流波动，需复核账目", level: "复核" },
        { id: "A002", title: "抵押物估值到期", desc: "青川牧业抵押地块需重新评估", level: "提醒" }
      ],
      fallbackNotices: [
        { id: "N001", content: "总部发布：11 月涉农贷款政策更新", time: "1 小时前" },
        { id: "N002", content: "系统维护：11/30 01:00-03:00 暂停服务", time: "昨天" }
      ]
    };
  },
  computed: {
    statCards() {
      return [
        { label: "待审批贷款", value: this.overview.pendingLoans, trend: "需尽快进入审核", trendClass: "warning" },
        { label: "未分配贷款", value: this.overview.unassignedLoans, trend: "等待接收审批", trendClass: "neutral" },
        { label: "智能匹配意向农户", value: this.overview.matchedFarmersIntention, trend: "意向库记录数", trendClass: "positive" }
      ];
    }
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "4");
    this.loadOverview();
    this.loadLoans();
    this.loadMatches();
    this.loadStatusStats();
    this.loadNotices();
    this.buildCalendar();
  },
  methods: {
    loadOverview() {
      getBankOverview().then((res) => {
        if (res && (res.success || res.flag) && res.data) {
          this.overview = res.data;
        }
      }).catch(err => {
        console.error("Failed to load overview:", err);
      });
    },
    loadLoans() {
      getBankLoans().then((res) => {
        if (res && (res.success || res.flag) && Array.isArray(res.data)) {
          this.loanList = res.data;
        }
      }).catch(err => {
        console.error("Failed to load loans:", err);
      });
    },
    loadMatches() {
      getBankMatches().then((res) => {
        if (res && (res.success || res.flag) && Array.isArray(res.data)) {
          this.matchList = res.data;
        }
      });
    },
    loadStatusStats() {
      getLoanStatusStats().then((res) => {
        if (res && (res.success || res.flag) && Array.isArray(res.data)) {
          this.stages = res.data;
        }
      });
    },
    loadAlerts() {
      getBankAlerts().then((res) => {
        if (res && res.flag && Array.isArray(res.data) && res.data.length > 0) {
          this.alertList = res.data;
        } else {
          this.alertList = this.fallbackAlerts;
        }
      }).catch(() => {
        this.alertList = this.fallbackAlerts;
      });
    },
    loadNotices() {
      getBankNotifications().then((res) => {
        if (res && (res.success || res.flag) && Array.isArray(res.data) && res.data.length > 0) {
          this.notices = res.data;
        } else {
          this.notices = this.fallbackNotices;
        }
      }).catch(() => {
        this.notices = this.fallbackNotices;
      });
    },
    buildCalendar() {
      const today = new Date();
      this.calendar = Array.from({ length: 6 }).map((_, idx) => {
        const day = new Date(today);
        day.setDate(day.getDate() + idx);
        return {
          date: `${day.getMonth() + 1}/${day.getDate()}`,
          text: idx === 0 ? "今日" : day.toLocaleDateString("zh-CN", { weekday: "short" }),
          active: idx === 0
        };
      });
    },
    handleAction(type) {
      if (type === "loan") {
        this.$router.push("/home/loanApply/overview").catch((err) => err);
      } else if (type === "unassigned") {
        this.$router.push("/home/loanApply/overview").catch((err) => err);
      }
    },
    handleAssign(match) {
      this.$router.push("/home/loanApply/overview").catch((err) => err);
    },
    handleImageError(event) {
      event.target.src = "/order/wutu.gif";
    }
  }
};
</script>

<style lang="less" scoped>
.bank-dashboard {
  width: 1100px;
  margin: 0 auto;
  padding: 20px 0 60px;

  .hero {
    display: flex;
    justify-content: space-between;
    background: linear-gradient(115deg, #1f4ed8, #60a5fa);
    border-radius: 20px;
    padding: 30px;
    color: #fff;
    margin-bottom: 24px;

    .hero-text {
      max-width: 60%;

      .hero-badge {
        font-size: 12px;
        letter-spacing: 2px;
        text-transform: uppercase;
        opacity: 0.8;
      }

      h1 {
        margin: 10px 0;
        font-size: 30px;
      }

      .hero-desc {
        font-size: 15px;
        line-height: 1.6;
        opacity: 0.9;
        margin-bottom: 16px;
      }

      .hero-actions .el-button + .el-button {
        margin-left: 12px;
      }
    }

    .hero-illustration img {
      width: 240px;
      height: auto;
    }
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 12px;
    margin-bottom: 24px;

    .stat-card {
      background: #fff;
      border-radius: 14px;
      padding: 16px;
      box-shadow: 0 6px 16px rgba(15, 23, 42, 0.08);

      .stat-label {
        font-size: 13px;
        color: #64748b;
      }

      .stat-value {
        font-size: 24px;
        font-weight: 700;
        margin: 8px 0;
      }

      .stat-trend {
        font-size: 12px;

        &.warning { color: #f97316; }
        &.positive { color: #10b981; }
        &.danger { color: #ef4444; }
        &.neutral { color: #475569; }
      }
    }
  }

  .content-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 18px;

    .panel {
      background: #fff;
      border-radius: 16px;
      padding: 20px;
      box-shadow: 0 10px 28px rgba(15, 23, 42, 0.05);
      margin-bottom: 18px;

      .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h2 {
          margin: 0;
          font-size: 18px;
        }
      }

      .loan-item,
      .match-item,
      .alert-item,
      .notice-item {
        padding: 12px 0;
        border-bottom: 1px solid #f1f5f9;

        &:last-child {
          border-bottom: none;
        }
      }

      .loan-title,
      .match-title,
      .alert-title,
      .notice-title {
        font-size: 15px;
        color: #1f2937;
        margin-bottom: 4px;
      }

      .loan-meta,
      .match-meta,
      .alert-meta,
      .notice-meta {
        font-size: 12px;
        color: #94a3b8;
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .status-tag {
        padding: 4px 10px;
        border-radius: 999px;
        font-size: 12px;
        color: #fff;

        &.urgent { background: #ef4444; }
        &.normal { background: #3b82f6; }
        &.review { background: #f59e0b; }
        &.warning { background: #f97316; }
      }

      .progress-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;

        .progress-item {
          background: #f8fafc;
          border-radius: 12px;
          padding: 12px;

          .progress-top {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            font-size: 13px;

            strong {
              font-size: 18px;
            }
          }
        }
      }

      .calendar {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 10px;

        .calendar-item {
          background: #f1f5f9;
          border-radius: 10px;
          padding: 12px;
          text-align: center;
          color: #475569;

          &.active {
            background: #2563eb;
            color: #fff;
          }

          .calendar-date {
            font-size: 18px;
            font-weight: 700;
          }

          .calendar-text {
            font-size: 12px;
            margin-top: 4px;
          }
        }
      }

      .empty {
        text-align: center;
        padding: 20px 0;
        color: #94a3b8;
      }
    }
  }

  @media (max-width: 1024px) {
    width: 100%;
    padding: 20px;

    .stats-grid {
      grid-template-columns: repeat(2, 1fr);
    }

    .content-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 640px) {
    .hero {
      flex-direction: column;

      .hero-text {
        max-width: 100%;
      }

      .hero-illustration {
        margin-top: 16px;
      }
    }

    .stats-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>

