<template>
  <div class="expert-dashboard">
    <div class="hero-card">
      <div class="hero-text">
        <h1>欢迎回来，王教授�?</h1>
        <p>今日共有 {{ overview.pendingQuestions }} 条待回复咨询、{{ overview.todayAppointments }} 场预约。请优先完成紧急问题的解答�?</p>
        <div class="hero-actions">
          <el-button type="primary" round @click="handleQuickAction('question')">开始答�?</el-button>
          <el-button round style="margin-left: 12px;" @click="goPublishKnowledge">发布知识</el-button>
          <el-button round type="info" style="margin-left: 12px;" @click="goPublishedKnowledge">已运知识</el-button>
        </div>
      </div>
      <div class="hero-illustration">
        <img src="/kn/expert-hero.svg" alt="expert hero" @error="handleImageFallback">
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="stat in statList" :key="stat.label">
        <div class="stat-label">{{ stat.label }}</div>
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-trend">{{ stat.trend }}</div>
      </div>
    </div>

    <div class="content-grid">
      <div class="main-column">
        <section class="panel">
          <div class="panel-header">
            <h2>最新农技问答</h2>
            <el-link type="primary" @click="handleQuickAction('question')">查看全部 ></el-link>
          </div>
          <div class="list-item" v-for="question in questionList" :key="question.id">
            <div>
              <div class="item-title">【{{ question.priority }}】{{ question.title }}</div>
              <div class="item-meta">来自 {{ question.source }} · 提交�?? {{ question.time }}</div>
            </div>
            <span class="status-tag" :class="question.statusClass">{{ question.statusText }}</span>
          </div>
          <div v-if="questionList.length === 0" class="empty">暂无问答记录</div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>预约服务安排</h2>
            <el-link type="primary" @click="handleQuickAction('appointment')">管理预约 ></el-link>
          </div>
          <div class="list-item" v-for="item in appointmentList" :key="item.id">
            <div>
              <div class="item-title">{{ item.time }} · {{ item.type }}</div>
              <div class="item-meta">对象：{{ item.client }} �?? 主题：{{ item.topic }}</div>
            </div>
            <span class="status-tag" :class="item.statusClass">{{ item.statusText }}</span>
          </div>
          <div v-if="appointmentList.length === 0" class="empty">暂无预约安排</div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>知识内容创作</h2>
            <div>
              <el-link type="primary" @click="handleQuickAction('knowledge')" style="margin-right: 12px">发布知识 ></el-link>
              <el-link type="info" :underline="false" @click="goPublishedKnowledge">已运知识 ></el-link>
            </div>
          </div>
          <div class="list-item" v-for="article in knowledgeList" :key="article.id">
            <div>
              <div class="item-title">{{ article.title }}</div>
              <div class="item-meta">状态：{{ article.status }} · 阅读 {{ article.reads }}</div>
            </div>
            <span class="status-tag" :class="article.statusClass">{{ article.statusHint }}</span>
          </div>
          <div v-if="knowledgeList.length === 0" class="empty">暂无知识内容</div>
        </section>
      </div>

      <div class="side-column">
        <section class="panel">
          <div class="panel-header">
            <h2>工作日历</h2>
            <span class="panel-sub">今日：{{ calendarSummary }}</span>
          </div>
          <div class="calendar-grid">
            <div
              v-for="day in calendarDays"
              :key="day.date"
              :class="['calendar-day', { active: day.isToday }]"
            >
              <div class="calendar-date">{{ day.date }}</div>
              <div class="calendar-label">{{ day.label }}</div>
            </div>
          </div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>系统通知</h2>
          </div>
          <div class="list-item" v-for="notice in notificationList" :key="notice.id">
            <div>
              <div class="item-title">{{ notice.content }}</div>
              <div class="item-meta">{{ notice.time }}</div>
            </div>
          </div>
          <div v-if="notificationList.length === 0" class="empty">暂无通知</div>
        </section>

        <section class="panel">
          <div class="panel-header">
            <h2>快捷入口</h2>
          </div>
          <div class="shortcut-grid">
            <el-button
              v-for="shortcut in shortcuts"
              :key="shortcut.type"
              plain
              @click="handleQuickAction(shortcut.type)"
            >{{ shortcut.label }}</el-button>
          </div>
        </section>
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
        { type: "question", label: "批量回复" },
        { type: "appointment", label: "预约管理" },
        { type: "knowledge", label: "发布知识" },
        { type: "published", label: "已运知识" },
        { type: "evaluation", label: "查看评价" }
      ],
      defaultData: {
        questions: [
          {
            id: "q1",
            priority: "紧�?",
            title: "柑橘叶片发黄如何处理�??",
            source: "赣南果农",
            time: "09:12",
            statusText: "待回�??",
            statusClass: "pending"
          },
          {
            id: "q2",
            priority: "高优",
            title: "大棚番茄卷叶是否缺钙�??",
            source: "寿光合作�??",
            time: "08:40",
            statusText: "待复�??",
            statusClass: "review"
          },
          {
            id: "q3",
            priority: "常规",
            title: "水稻穗期病害防控方案",
            source: "洞庭湖农�??",
            time: "昨天 21:05",
            statusText: "已回�??",
            statusClass: "done"
          }
        ],
        appointments: [
          {
            id: "a1",
            time: "10:30",
            type: "视频咨询",
            client: "昌吉番茄基地",
            topic: "肥水管理",
            statusText: "即将开�??",
            statusClass: "review"
          },
          {
            id: "a2",
            time: "14:00",
            type: "现场指导",
            client: "平谷大桃合作�??",
            topic: "病虫害监�??",
            statusText: "需出行",
            statusClass: "pending"
          },
          {
            id: "a3",
            time: "19:30",
            type: "群直播答�??",
            client: "平台入驻农户",
            topic: "秋冬保温",
            statusText: "已确�??",
            statusClass: "done"
          }
        ],
        knowledge: [
          {
            id: "k1",
            title: "小麦纹枯病绿色防控指�??",
            status: "已发�??",
            reads: 3200,
            statusHint: "通过审核",
            statusClass: "done"
          },
          {
            id: "k2",
            title: "设施蔬菜温湿调控技�??",
            status: "待审�??",
            reads: 0,
            statusHint: "审核�??",
            statusClass: "review"
          },
          {
            id: "k3",
            title: "农机检修保养月�??",
            status: "草稿",
            reads: 0,
            statusHint: "完善�??",
            statusClass: "pending"
          }
        ],
        notifications: [
          {
            id: "n1",
            content: "平台推送：11 月农情监测报告上�??",
            time: "1 小时�??"
          },
          {
            id: "n2",
            content: "预约提醒：请确认 12/01 田间指导",
            time: "昨天"
          },
          {
            id: "n3",
            content: "知识审核：有 2 篇内容待编辑",
            time: "2 天前"
          }
        ]
      }
    };
  },
  computed: {
    statList() {
      return [
        {
          label: "待回复问�??",
          value: this.overview.pendingQuestions,
          trend: `紧急：${this.overview.urgentCount || 0}`
        },
        {
          label: "今日预约",
          value: this.overview.todayAppointments,
          trend: this.overview.appointmentSummary || "�??"
        },
        {
          label: "本周知识发布",
          value: this.overview.publishedKnowledge,
          trend: this.overview.knowledgeTrend || "暂无数据"
        },
        {
          label: "专家评分",
          value: this.overview.expertScore,
          trend: `�?? ${this.overview.reviewCount || 0} 条评价`
        }
      ];
    }
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
    this.loadOverview();
    this.loadQuestions();
    this.loadAppointments();
    this.loadKnowledge();
    this.loadNotifications();
    this.buildCalendar();
  },
  methods: {
    loadOverview() {
      getExpertOverview()
        .then((res) => {
          if (res && res.flag && res.data) {
            this.overview = res.data;
          } else {
            this.setDefaultOverview();
          }
        })
        .catch(() => {
          this.setDefaultOverview();
        });
    },
    loadQuestions() {
      getExpertQuestions()
        .then((res) => {
          if (res && res.flag && Array.isArray(res.data) && res.data.length) {
            this.questionList = res.data;
          } else {
            this.questionList = this.defaultData.questions;
          }
        })
        .catch(() => {
          this.questionList = this.defaultData.questions;
        });
    },
    loadAppointments() {
      getExpertAppointments()
        .then((res) => {
          if (res && res.flag && Array.isArray(res.data) && res.data.length) {
            this.appointmentList = res.data;
          } else {
            this.appointmentList = this.defaultData.appointments;
          }
        })
        .catch(() => {
          this.appointmentList = this.defaultData.appointments;
        });
    },
    loadKnowledge() {
      getExpertKnowledge()
        .then((res) => {
          if (res && res.flag && Array.isArray(res.data) && res.data.length) {
            this.knowledgeList = res.data;
          } else {
            this.knowledgeList = this.defaultData.knowledge;
          }
        })
        .catch(() => {
          this.knowledgeList = this.defaultData.knowledge;
        });
    },
    loadNotifications() {
      getExpertNotifications()
        .then((res) => {
          if (res && res.flag && Array.isArray(res.data) && res.data.length) {
            this.notificationList = res.data;
          } else {
            this.notificationList = this.defaultData.notifications;
          }
        })
        .catch(() => {
          this.notificationList = this.defaultData.notifications;
        });
    },
    buildCalendar() {
      const today = new Date();
      const days = [];
      for (let i = -2; i < 12; i++) {
        const date = new Date(today);
        date.setDate(date.getDate() + i);
        days.push({
          date: date.getDate(),
          label: i === 0 ? "今日" : date.toLocaleDateString("zh-CN", { weekday: "short" }),
          isToday: i === 0
        });
      }
      this.calendarDays = days;
      this.calendarSummary = `答疑 ${this.overview.pendingQuestions} · 预约 ${this.overview.todayAppointments}`;
    },
    setDefaultOverview() {
      this.overview = {
        pendingQuestions: this.defaultData.questions.length,
        urgentCount: 2,
        todayAppointments: this.defaultData.appointments.length,
        appointmentSummary: "视频 2 · 线下 1",
        publishedKnowledge: 7,
        knowledgeTrend: "阅读�?? +18%",
        expertScore: 4.9,
        reviewCount: 126
      };
    },
    handleQuickAction(type) {
      switch (type) {
        case "question":
          this.$router.push("/home/question").catch((err) => err);
          break;
        case "appointment":
          this.$router.push("/home/appointment").catch((err) => err);
          break;
        case "knowledge":
          this.$router.push("/home/knowledge").catch((err) => err);
          break;
        case "published":
          this.goPublishedKnowledge();
          break;
        case "evaluation":
          this.$router.push("/home/user/userbuy").catch((err) => err);
          break;
        default:
          break;
      }
    },
    // 快捷进入发布知识页面
    goPublishKnowledge() {
      this.$router.push("/home/addmessage/publishknowledges").catch((err) => err);
    },
    // 全部已运知识列表
    goPublishedKnowledge() {
      this.$router.push("/home/user/publishedknowledges").catch((err) => err);
    },
    handleImageFallback(event) {
      event.target.src = "/order/wutu.gif";
    }
  }
};
</script>

<style lang="less" scoped>
.expert-dashboard {
  width: 1100px;
  margin: 0 auto;
  padding: 24px 0 60px;

  .hero-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    border-radius: 18px;
    padding: 28px;
    box-shadow: 0 10px 30px rgba(75, 143, 226, 0.15);
    margin-bottom: 24px;

    .hero-text {
      h1 {
        margin: 0 0 12px;
        font-size: 28px;
        color: #1f2937;
      }

      p {
        margin: 0 0 18px;
        font-size: 15px;
        color: #6b7280;
      }
    }

    .hero-illustration img {
      width: 220px;
      height: auto;
    }
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 26px;

    .stat-card {
      background: #fff;
      border-radius: 14px;
      padding: 18px;
      box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);

      .stat-label {
        font-size: 13px;
        color: #6b7280;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        margin: 6px 0;
        color: #1f2937;
      }

      .stat-trend {
        font-size: 12px;
        color: #10b981;
      }
    }
  }

  .content-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;

    .panel {
      background: #fff;
      border-radius: 16px;
      padding: 20px 22px;
      box-shadow: 0 10px 28px rgba(15, 23, 42, 0.05);
      margin-bottom: 20px;

      .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h2 {
          margin: 0;
          font-size: 18px;
          color: #1f2937;
        }

        .panel-sub {
          font-size: 13px;
          color: #6b7280;
        }
      }

      .list-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 14px 0;
        border-bottom: 1px solid #f1f5f9;

        &:last-child {
          border-bottom: none;
        }

        .item-title {
          font-size: 15px;
          color: #1f2937;
          margin-bottom: 4px;
        }

        .item-meta {
          font-size: 12px;
          color: #94a3b8;
        }

        .status-tag {
          padding: 4px 12px;
          border-radius: 999px;
          font-size: 12px;
          color: #fff;

          &.pending {
            background: #f97316;
          }

          &.review {
            background: #3b82f6;
          }

          &.done {
            background: #10b981;
          }
        }
      }

      .empty {
        text-align: center;
        color: #94a3b8;
        padding: 20px 0;
      }

      .calendar-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 12px;

        .calendar-day {
          border-radius: 12px;
          background: #f1f5f9;
          padding: 12px;
          text-align: center;
          color: #475569;

          &.active {
            background: linear-gradient(135deg, #4b8fe2, #1f6fd1);
            color: #fff;
            box-shadow: 0 8px 18px rgba(31, 111, 209, 0.25);
          }

          .calendar-date {
            font-size: 20px;
            font-weight: 700;
          }

          .calendar-label {
            font-size: 12px;
            margin-top: 4px;
          }
        }
      }

      .shortcut-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;

        .el-button {
          width: 100%;
        }
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
    .hero-card {
      flex-direction: column;
      text-align: center;

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


