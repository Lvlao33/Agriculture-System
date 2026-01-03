<template>
  <div class="expert-dashboard">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="greeting">👋 欢迎回来，王教授</h1>
          <p class="subtitle">
            您有 <span class="highlight">{{ overview.pendingQuestions }}</span> 条待回复咨询，
            <span class="highlight">{{ overview.todayAppointments }}</span> 场今日预约。
            <br>
            您的专业指导是农户丰收的保障。
          </p>
          <div class="hero-btns">
            <el-button type="primary" icon="el-icon-chat-dot-round" @click="handleQuickAction('question')">处理咨询</el-button>
            <el-button plain icon="el-icon-edit-outline" @click="goPublishKnowledge">发布新知</el-button>
          </div>
        </div>
        <div class="hero-image">
          <img src="/kn/expert-hero.svg" alt="Expert Working" @error="handleImageFallback">
        </div>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card" v-for="(stat, index) in statList" :key="index">
        <div class="stat-icon" :class="`icon-${index}`">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-num">{{ stat.value }}</div>
          <div class="stat-trend" :class="{ 'trend-up': stat.trend.includes('+') }">
            {{ stat.trend }}
          </div>
        </div>
      </div>
    </div>

    <div class="main-layout">
      <div class="left-panel">
        <div class="content-card">
          <div class="card-header">
            <div class="header-title">
              <i class="el-icon-question header-icon"></i>
              <h3>最新农技问答</h3>
            </div>
            <el-button type="text" @click="handleQuickAction('question')">查看全部 <i class="el-icon-arrow-right"></i></el-button>
          </div>
          <div class="list-container">
            <div class="task-item" v-for="question in questionList" :key="question.id" @click="handleQuickAction('question')">
              <div class="task-priority" :class="question.statusClass">
                {{ question.priority }}
              </div>
              <div class="task-content">
                <div class="task-title">{{ question.title }}</div>
                <div class="task-meta">
                  <span class="source"><i class="el-icon-user"></i> {{ question.source }}</span>
                  <span class="time">{{ question.time }}</span>
                </div>
              </div>
              <el-tag size="small" :type="getStatusType(question.statusClass)">{{ question.statusText }}</el-tag>
            </div>
            <el-empty v-if="questionList.length === 0" description="暂无待处理问答" :image-size="60"></el-empty>
          </div>
        </div>

        <div class="content-card">
          <div class="card-header">
            <div class="header-title">
              <i class="el-icon-date header-icon"></i>
              <h3>预约服务安排</h3>
            </div>
            <el-button type="text" @click="handleQuickAction('appointment')">管理预约 <i class="el-icon-arrow-right"></i></el-button>
          </div>
          <div class="list-container">
            <div class="appointment-item" v-for="item in appointmentList" :key="item.id">
              <div class="app-time-box">
                <span class="app-time">{{ item.time }}</span>
                <span class="app-type">{{ item.type }}</span>
              </div>
              <div class="app-info">
                <div class="app-client">{{ item.client }}</div>
                <div class="app-topic">咨询主题：{{ item.topic }}</div>
              </div>
              <el-tag size="small" effect="plain" :type="getStatusType(item.statusClass)">{{ item.statusText }}</el-tag>
            </div>
            <el-empty v-if="appointmentList.length === 0" description="暂无预约安排" :image-size="60"></el-empty>
          </div>
        </div>

        <div class="content-card">
          <div class="card-header">
            <div class="header-title">
              <i class="el-icon-notebook-1 header-icon"></i>
              <h3>近期知识发布</h3>
            </div>
            <el-button type="text" @click="handleQuickAction('knowledge')">更多 <i class="el-icon-arrow-right"></i></el-button>
          </div>
          <div class="list-container">
            <div class="article-item" v-for="article in knowledgeList" :key="article.id">
              <div class="article-icon">📄</div>
              <div class="article-info">
                <div class="article-title">{{ article.title }}</div>
                <div class="article-meta">阅读量 {{ article.reads }}</div>
              </div>
              <el-tag size="mini" :type="getStatusType(article.statusClass)">{{ article.status }}</el-tag>
            </div>
            <el-empty v-if="knowledgeList.length === 0" description="暂无知识内容" :image-size="60"></el-empty>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="side-card calendar-card">
          <div class="side-header">
            <h4>工作日历</h4>
            <span class="date-summary">{{ calendarSummary }}</span>
          </div>
          <div class="mini-calendar">
            <div
              v-for="day in calendarDays"
              :key="day.date"
              class="day-cell"
              :class="{ 'is-today': day.isToday }"
            >
              <span class="day-label">{{ day.label }}</span>
              <span class="day-num">{{ day.date }}</span>
            </div>
          </div>
        </div>

        <div class="side-card">
          <div class="side-header"><h4>快捷入口</h4></div>
          <div class="shortcuts-grid">
            <div class="shortcut-btn" v-for="sc in shortcuts" :key="sc.type" @click="handleQuickAction(sc.type)">
              <i :class="getShortcutIcon(sc.type)"></i>
              <span>{{ sc.label }}</span>
            </div>
          </div>
        </div>

        <div class="side-card notification-card">
          <div class="side-header"><h4>消息通知</h4></div>
          <div class="notification-list">
            <div class="notice-item" v-for="notice in notificationList" :key="notice.id">
              <div class="notice-dot"></div>
              <div class="notice-content">
                <p class="notice-text">{{ notice.content }}</p>
                <span class="notice-time">{{ notice.time }}</span>
              </div>
            </div>
            <div v-if="notificationList.length === 0" class="no-notice">暂无新消息</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getExpertOverview,
  getExpertQuestions,
  getExpertAppointments,
  getExpertKnowledge,
  getExpertNotifications
} from "../api/expert";

export default {
  name: "HomeExpert",
  data() {
    return {
      overview: {
        pendingQuestions: 0,
        todayAppointments: 0,
        publishedKnowledge: 0,
        expertScore: 0
      },
      questionList: [],
      appointmentList: [],
      knowledgeList: [],
      notificationList: [],
      calendarDays: [],
      calendarSummary: "",
      shortcuts: [
        { type: "question", label: "待回问答" },
        { type: "appointment", label: "我的预约" },
        { type: "knowledge", label: "发布文章" },
        { type: "evaluation", label: "用户评价" }
      ],
      // 默认数据保持不变，用于接口失败时的兜底
      defaultData: {
        questions: [
          { id: "q1", priority: "紧急", title: "柑橘叶片发黄如何处理？", source: "赣南果农", time: "09:12", statusText: "待回复", statusClass: "pending" },
          { id: "q2", priority: "高优", title: "大棚番茄卷叶是否缺钙？", source: "寿光合作社", time: "08:40", statusText: "待复核", statusClass: "review" },
          { id: "q3", priority: "普通", title: "水稻穗期病害防控方案", source: "洞庭湖农场", time: "昨天 21:05", statusText: "已回复", statusClass: "done" }
        ],
        appointments: [
          { id: "a1", time: "10:30", type: "视频咨询", client: "昌吉番茄基地", topic: "肥水管理", statusText: "即将开始", statusClass: "review" },
          { id: "a2", time: "14:00", type: "现场指导", client: "平谷大桃合作社", topic: "病虫害监测", statusText: "需出行", statusClass: "pending" }
        ],
        knowledge: [
          { id: "k1", title: "小麦纹枯病绿色防控指南", status: "已发布", reads: 3200, statusHint: "通过", statusClass: "done" },
          { id: "k2", title: "设施蔬菜温湿调控技术", status: "待审核", reads: 0, statusHint: "审核中", statusClass: "review" },
          { id: "k3", title: "农机检修保养月历", status: "草稿", reads: 0, statusHint: "编辑", statusClass: "pending" }
        ],
        notifications: [
          { id: "n1", content: "平台推送：11 月农情监测报告上线", time: "1 小时前" },
          { id: "n2", content: "预约提醒：请确认 12/01 田间指导", time: "昨天" }
        ]
      }
    };
  },
  computed: {
    statList() {
      return [
        { label: "待处理问答", value: this.overview.pendingQuestions, trend: "紧急待办", icon: "el-icon-chat-dot-square" },
        { label: "今日预约", value: this.overview.todayAppointments, trend: "日程安排", icon: "el-icon-date" },
        { label: "知识发布", value: this.overview.publishedKnowledge, trend: "累计贡献", icon: "el-icon-reading" },
        { label: "专家评分", value: this.overview.expertScore, trend: "综合信誉", icon: "el-icon-star-off" }
      ];
    }
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
    this.loadAllData();
    this.buildCalendar();
  },
  methods: {
    loadAllData() {
      this.loadOverview();
      this.loadQuestions();
      this.loadAppointments();
      this.loadKnowledge();
      this.loadNotifications();
    },
    // 将 Element UI 的 Tag 类型映射
    getStatusType(statusClass) {
      if (statusClass === 'done' || statusClass === 'success') return 'success';
      if (statusClass === 'pending' || statusClass === 'warning') return 'warning';
      if (statusClass === 'review' || statusClass === 'primary') return 'primary';
      return 'info';
    },
    getShortcutIcon(type) {
      const map = {
        question: "el-icon-message",
        appointment: "el-icon-alarm-clock",
        knowledge: "el-icon-edit",
        evaluation: "el-icon-trophy"
      };
      return map[type] || "el-icon-menu";
    },
    loadOverview() {
      getExpertOverview().then(res => {
        if (res?.flag && res.data) this.overview = res.data;
        else this.overview = { ...this.overview, ...this.defaultData.overview }; // Fallback logic simplified
      }).catch(() => {}); // Silent fail or default
    },
    loadQuestions() {
      getExpertQuestions().then(res => {
        this.questionList = (res?.flag && res.data?.length) ? res.data : this.defaultData.questions;
      }).catch(() => this.questionList = this.defaultData.questions);
    },
    loadAppointments() {
      getExpertAppointments().then(res => {
        this.appointmentList = (res?.flag && res.data?.length) ? res.data : this.defaultData.appointments;
      }).catch(() => this.appointmentList = this.defaultData.appointments);
    },
    loadKnowledge() {
      getExpertKnowledge().then(res => {
        this.knowledgeList = (res?.flag && res.data?.length) ? res.data : this.defaultData.knowledge;
      }).catch(() => this.knowledgeList = this.defaultData.knowledge);
    },
    loadNotifications() {
      getExpertNotifications().then(res => {
        this.notificationList = (res?.flag && res.data?.length) ? res.data : this.defaultData.notifications;
      }).catch(() => this.notificationList = this.defaultData.notifications);
    },
    buildCalendar() {
      const today = new Date();
      const days = [];
      // 只显示未来一周 + 过去两天
      for (let i = -1; i < 6; i++) {
        const date = new Date(today);
        date.setDate(date.getDate() + i);
        days.push({
          date: date.getDate(),
          label: i === 0 ? "今" : date.toLocaleDateString("zh-CN", { weekday: "short" }).replace("周",""),
          isToday: i === 0
        });
      }
      this.calendarDays = days;
      this.calendarSummary = `${today.getMonth() + 1}月${today.getDate()}日`;
    },
    handleQuickAction(type) {
      switch (type) {
        case "question": this.$router.push("/home/onlineQuestions"); break; // 修正路由
        case "appointment": this.$router.push("/home/appointment"); break;
        case "knowledge": this.$router.push("/home/myKnowledge"); break; // 修正为我的知识管理
        case "evaluation": this.$router.push("/home/user/userbuy"); break;
      }
    },
    goPublishKnowledge() {
      this.$router.push("/home/addmessage/publishknowledges");
    },
    handleImageFallback(event) {
      // 如果没有图片，隐藏图片区域或者显示默认占位
      event.target.style.display = 'none';
    }
  }
};
</script>

<style lang="less" scoped>
.expert-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #333;
}

/* --- Hero Section --- */
.hero-section {
  background: linear-gradient(135deg, #e0f2f1 0%, #e8f5e9 100%);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

  .hero-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 2;
  }

  .hero-text {
    max-width: 60%;
    .greeting {
      font-size: 28px;
      color: #2c3e50;
      margin-bottom: 16px;
      font-weight: 700;
    }
    .subtitle {
      font-size: 16px;
      color: #546e7a;
      line-height: 1.6;
      margin-bottom: 24px;
      .highlight {
        color: #2e7d32;
        font-weight: bold;
        font-size: 18px;
      }
    }
  }

  .hero-image img {
    height: 160px;
    width: auto;
    opacity: 0.9;
  }
}

/* --- Stats Grid --- */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;

  .stat-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    transition: transform 0.2s;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-right: 16px;
      
      &.icon-0 { background: #e3f2fd; color: #1976d2; }
      &.icon-1 { background: #e8f5e9; color: #2e7d32; }
      &.icon-2 { background: #fff3e0; color: #f57c00; }
      &.icon-3 { background: #f3e5f5; color: #7b1fa2; }
    }

    .stat-info {
      .stat-label { font-size: 13px; color: #78909c; margin-bottom: 4px; }
      .stat-num { font-size: 24px; font-weight: 700; color: #263238; line-height: 1; margin-bottom: 4px; }
      .stat-trend { font-size: 12px; color: #90a4ae; }
    }
  }
}

/* --- Main Layout --- */
.main-layout {
  display: grid;
  grid-template-columns: 2.5fr 1fr;
  gap: 24px;
}

/* --- Common Card Styles --- */
.content-card, .side-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid #f0f0f0;

  .card-header, .side-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f5f5f5;

    .header-title {
      display: flex;
      align-items: center;
      gap: 8px;
      .header-icon { color: #2e7d32; font-size: 18px; }
      h3 { margin: 0; font-size: 17px; font-weight: 600; color: #333; }
    }
    
    h4 { margin: 0; font-size: 16px; font-weight: 600; color: #333; }
    .date-summary { font-size: 12px; color: #999; }
  }
}

/* --- Task List Items --- */
.task-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background-color: #f9fafb;
  }

  .task-priority {
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 4px;
    margin-right: 12px;
    white-space: nowrap;
    
    &.pending { background: #fff3e0; color: #e65100; }
    &.review { background: #e3f2fd; color: #1565c0; }
    &.done { background: #e8f5e9; color: #2e7d32; }
  }

  .task-content {
    flex: 1;
    margin-right: 10px;
    
    .task-title {
      font-size: 15px;
      color: #333;
      margin-bottom: 4px;
      font-weight: 500;
    }
    
    .task-meta {
      font-size: 12px;
      color: #90a4ae;
      display: flex;
      gap: 15px;
      
      i { margin-right: 4px; }
    }
  }
}

/* --- Appointment Items --- */
.appointment-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  border-left: 3px solid transparent;
  
  &:hover {
    background-color: #f9fafb;
    border-left-color: #67C23A;
  }

  .app-time-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 70px;
    margin-right: 15px;
    
    .app-time { font-size: 16px; font-weight: bold; color: #333; }
    .app-type { font-size: 11px; color: #78909c; background: #eceff1; padding: 1px 4px; border-radius: 3px; margin-top: 2px;}
  }

  .app-info {
    flex: 1;
    .app-client { font-size: 14px; font-weight: 500; margin-bottom: 2px; }
    .app-topic { font-size: 12px; color: #90a4ae; }
  }
}

/* --- Article Items --- */
.article-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
  
  &:last-child { border-bottom: none; }

  .article-icon { font-size: 18px; margin-right: 12px; background: #f1f8e9; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
  
  .article-info {
    flex: 1;
    .article-title { font-size: 14px; color: #333; margin-bottom: 2px; }
    .article-meta { font-size: 12px; color: #b0bec5; }
  }
}

/* --- Calendar Mini --- */
.mini-calendar {
  display: flex;
  justify-content: space-between;
  
  .day-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 6px;
    border-radius: 8px;
    cursor: default;
    
    .day-label { font-size: 12px; color: #b0bec5; margin-bottom: 4px; }
    .day-num { font-size: 14px; font-weight: 600; color: #546e7a; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
    
    &.is-today {
      .day-label { color: #67C23A; }
      .day-num { background: #67C23A; color: #fff; box-shadow: 0 2px 6px rgba(103, 194, 58, 0.4); }
    }
  }
}

/* --- Shortcuts --- */
.shortcuts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  
  .shortcut-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    color: #546e7a;
    
    i { font-size: 24px; margin-bottom: 8px; color: #67C23A; }
    span { font-size: 13px; }
    
    &:hover {
      background: #e8f5e9;
      transform: translateY(-2px);
    }
  }
}

/* --- Notification --- */
.notification-list {
  .notice-item {
    display: flex;
    align-items: flex-start;
    padding: 10px 0;
    
    .notice-dot {
      width: 8px;
      height: 8px;
      background: #ff5252;
      border-radius: 50%;
      margin-top: 6px;
      margin-right: 10px;
      flex-shrink: 0;
    }
    
    .notice-content {
      .notice-text { margin: 0 0 4px; font-size: 13px; color: #37474f; line-height: 1.4; }
      .notice-time { font-size: 11px; color: #cfd8dc; }
    }
  }
  .no-notice { text-align: center; color: #ccc; font-size: 12px; padding: 10px; }
}

/* --- Responsive --- */
@media (max-width: 1024px) {
  .expert-dashboard { width: 100%; padding: 20px; }
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .main-layout { grid-template-columns: 1fr; }
}

@media (max-width: 600px) {
  .hero-section {
    padding: 20px;
    .hero-content { flex-direction: column; text-align: center; }
    .hero-text { max-width: 100%; margin-bottom: 20px; }
    .hero-image img { height: 100px; }
  }
  .stats-row { grid-template-columns: 1fr; }
}
</style>