<template>
  <div class="expert-guide-container">
    <!-- 顶部横幅 -->
    <div class="expert-banner">
      <div class="banner-content">
        <h1 class="banner-title">专家指导服务</h1>
        <p class="banner-desc">汇聚农业领域专业人才，为您提供全方位的技术指导与咨询服务</p>
        <div class="banner-stats">
          <div class="stat-item">
            <span class="stat-number">{{ expertTotal }}</span>
            <span class="stat-label">已入驻专家</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速入口 -->
    <div class="quick-actions">
      <div class="action-card" @click="handleQuickAction('question')" v-if="!$store.getters.isAdmin && !$store.getters.isExpert">
        <div class="action-icon question-icon">💡</div>
        <div class="action-title">在线提问</div>
        <div class="action-desc">个性化问题在线提问</div>
      </div>
      <div class="action-card" @click="handleQuickAction('appointment')" v-if="!$store.getters.isAdmin && !$store.getters.isExpert">
        <div class="action-icon appointment-icon">📅</div>
        <div class="action-title">预约指导</div>
        <div class="action-desc">预约专家线上连线指导</div>
      </div>
      <div class="action-card" @click="handleQuickAction('allExpert')">
        <div class="action-icon expert-icon">🎓</div>
        <div class="action-title">专家信息</div>
        <div class="action-desc">浏览全部专家信息</div>
      </div>
      <div class="action-card" @click="handleQuickAction('knowledge')">
        <div class="action-icon knowledge-icon">📚</div>
        <div class="action-title">农业知识</div>
        <div class="action-desc">学习农业技术知识</div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧：指导知识和热门问答 -->
      <div class="left-content">
        <!-- 指导知识 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">📖 专家推荐</h2>
            <a class="more-link" @click="goToAllKnowledgeList">查看更多 ></a>
          </div>
          <div class="knowledge-list-simple">
            <div 
              class="knowledge-item-simple" 
              v-for="(item, index) in displayedKnowledgeList" 
              :key="index"
            >
              <a 
                v-if="item.url"
                class="knowledge-title-link" 
                :href="item.url"
                target="_blank"
                rel="noopener noreferrer"
              >
                {{ item.title }}
              </a>
              <a 
                v-else
                class="knowledge-title-link" 
                @click="handleKnowledgeDetail(item)"
              >
                {{ item.title }}
              </a>
              <span class="knowledge-date">{{ formatDate(item.createTime || item.updateTime) }}</span>
            </div>
            <div v-if="knowledgeList.length === 0" class="empty-state">
              <p>暂无专家推荐</p>
            </div>
          </div>
        </div>

        <!-- 热门问答 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">🔥 热门问答</h2>
            <a class="more-link" @click="goToAllQuestions">查看更多 ></a>
          </div>
          <div class="question-list">
            <div 
              class="question-item" 
              v-for="(item, index) in hotQuestions" 
              :key="index"
              @click="handleQuestionDetail(item)"
            >
              <div class="question-content">
                <div class="question-title">{{ item.title }}</div>
                <div class="question-meta">
                  <span>提问者：{{ item.questioner }}</span>
                  <span v-if="item.expertName">专家：{{ item.expertName }}</span>
                </div>
              </div>
            </div>
            <div v-if="hotQuestions.length === 0" class="empty-state">
              <p>暂无热门问答</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：专家列表 -->
      <div class="right-content">
        <div class="expert-section">
          <div class="section-header">
            <h2 class="section-title">⭐ 推荐专家</h2>
            <a class="more-link" @click="goToAllExperts">更多 ></a>
          </div>
          <div class="expert-list">
            <div 
              class="expert-item" 
              v-for="(item, index) in expertList" 
              :key="index"
              :style="index === expertList.length - 1 ? '' : 'border-bottom: 1px dashed #f2f2f2;'"
            >
              <img 
                :src="$store.state.imgShowRoad + '/file/experta.png'" 
                alt="专家头像" 
                class="expert-avatar"
              />
              <div class="expert-info">
                <div class="expert-name">{{ item.realName }}</div>
                <div class="expert-position">{{ item.position }}</div>
                <div class="expert-profession">专业：{{ item.profession }}</div>
                <div class="expert-belong">单位：{{ item.belong }}</div>
                <div class="expert-phone">电话：{{ item.phone }}</div>
                <div class="expert-actions" v-if="!$store.getters.isAdmin && !$store.getters.isExpert">
                  <span class="action-btn" @click.stop="handleQuestion(item)">提问</span>
                  <span class="action-btn" @click.stop="handleAppointment(item)">预约</span>
                </div>
              </div>
            </div>
            <div v-if="expertList.length === 0" class="empty-state">
              <p>暂无专家信息</p>
            </div>
          </div>
        </div>

        <!-- 服务说明 -->
        <div class="service-info">
          <div class="section-header">
            <h2 class="section-title">💡 服务说明</h2>
          </div>
          <div class="service-content">
            <div class="service-item">
              <div class="service-icon">✓</div>
              <div class="service-text">专业农业专家在线答疑</div>
            </div>
            <div class="service-item">
              <div class="service-icon">✓</div>
              <div class="service-text">支持线上连线专家指导</div>
            </div>
            <div class="service-item">
              <div class="service-icon">✓</div>
              <div class="service-text">丰富的农业技术知识库</div>
            </div>
            <div class="service-item">
              <div class="service-icon">✓</div>
              <div class="service-text">及时响应，专业解答</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { selectQuestions, selectExpert } from "../api/order";
import { selectKnowledgesPage } from "../api/knowledge";

export default {
  name: "ExpertGuide",
  data() {
    return {
      expertList: [],
      hotQuestions: [],
      knowledgeList: [],
      expertTotal: 0,
      questionTotal: 0,
      knowledgeTotal: 0,
      searchValue: '',
      expertCount: 1,
      questionCount: 1,
      knowledgeCount: 1,
      initialDisplayCount: 5, // 初始显示数量
      // 静态热门问答列表
      staticHotQuestions: [
        {
          questionId: 'static-q-1',
          title: '冬季大棚蔬菜如何预防冻害？',
          questioner: '张农户',
          expertName: '李教授',
          status: 1, // 已回答
          createTime: '2025-11-15'
        },
        {
          questionId: 'static-q-2',
          title: '小麦晚播后如何管理才能保证产量？',
          questioner: '王农户',
          expertName: '刘专家',
          status: 1, // 已回答
          createTime: '2025-11-14'
        },
        {
          questionId: 'static-q-3',
          title: '油菜田出现黄叶是什么原因？应该怎么处理？',
          questioner: '赵农户',
          expertName: '陈教授',
          status: 1, // 已回答
          createTime: '2025-11-13'
        },
        {
          questionId: 'static-q-4',
          title: '连阴雨天气对冬小麦播种有什么影响？',
          questioner: '孙农户',
          expertName: '周专家',
          status: 1, // 已回答
          createTime: '2025-11-12'
        },
        {
          questionId: 'static-q-5',
          title: '土壤墒情不好，如何改善？',
          questioner: '李农户',
          expertName: '吴教授',
          status: 1, // 已回答
          createTime: '2025-11-11'
        },
        {
          questionId: 'static-q-6',
          title: '如何科学防治小麦田杂草？',
          questioner: '周农户',
          expertName: '郑专家',
          status: 1, // 已回答
          createTime: '2025-11-10'
        },
        {
          questionId: 'static-q-7',
          title: '大豆收获期遇到连阴雨怎么办？',
          questioner: '吴农户',
          status: 0, // 待回答
          createTime: '2025-11-09'
        },
        {
          questionId: 'static-q-8',
          title: '冬油菜苗期病虫害如何防控？',
          questioner: '郑农户',
          expertName: '王教授',
          status: 1, // 已回答
          createTime: '2025-11-08'
        }
      ],
      // 静态农业技术指导文档列表
      staticKnowledgeList: [
        {
          knowledgeId: 'static-1',
          title: '2025年冬季蔬菜生产技术指导意见',
          createTime: '2025-11-11',
          updateTime: '2025-11-11',
          url: 'https://www.natesc.org.cn/News/des?kind=&id=b1210a7b-fb54-4f15-96db-705a92001334&CategoryId=11a63552-05c9-475e-a504-0392e64ead0b'
        },
        {
          knowledgeId: 'static-2',
          title: '11月11日北方冬麦区土壤墒情状况',
          createTime: '2025-11-11',
          updateTime: '2025-11-11',
          url: 'https://www.natesc.org.cn/News/des?kind=&id=be44b30f-bb35-4b2e-88f9-0f4ea7841424&CategoryId=11a63552-05c9-475e-a504-0392e64ead0b'
        },
        {
          knowledgeId: 'static-3',
          title: '江淮冬油菜抗湿应急飞播技术意见',
          createTime: '2025-11-10',
          updateTime: '2025-11-10',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-4',
          title: '11月4日北方冬麦区土壤墒情状况',
          createTime: '2025-11-04',
          updateTime: '2025-11-04',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-5',
          title: '种植业成熟适用技术推广服务指导目录(2025)',
          createTime: '2025-10-31',
          updateTime: '2025-10-31',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-6',
          title: '2025年秋播冬油菜苗期病虫害防控技术指导意见',
          createTime: '2025-10-29',
          updateTime: '2025-10-29',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-7',
          title: '10月28日北方冬麦区土壤墒情状况',
          createTime: '2025-10-28',
          updateTime: '2025-10-28',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-8',
          title: '10月24日北方冬小麦区土壤墒情状况',
          createTime: '2025-10-24',
          updateTime: '2025-10-24',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-9',
          title: '用好十招应对晚播——————小麦抗湿晚播技术明白纸',
          createTime: '2025-10-23',
          updateTime: '2025-10-23',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-10',
          title: '2025年冬油菜抗渍涝保播种促壮苗技术意见',
          createTime: '2025-10-21',
          updateTime: '2025-10-21',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-11',
          title: '晚播小麦抗湿保苗科学施肥指导意见',
          createTime: '2025-10-20',
          updateTime: '2025-10-20',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-12',
          title: '2025年秋播小麦田和油菜田杂草科学防控技术方案',
          createTime: '2025-10-15',
          updateTime: '2025-10-15',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-13',
          title: '2025年黄淮海小麦播种期病虫草害防控技术指导意见',
          createTime: '2025-10-14',
          updateTime: '2025-10-14',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-14',
          title: '大豆花生收获期应对连阴雨防灾减损技术指导意见',
          createTime: '2025-10-13',
          updateTime: '2025-10-13',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-15',
          title: '因墒分类施策科学应对"烂秋雨"',
          createTime: '2025-10-09',
          updateTime: '2025-10-09',
          url: '' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-16',
          title: '黄淮海小麦应对连阴雨抗湿晚播技术意见',
          createTime: '2025-10-09',
          updateTime: '2025-10-09',
          url: '' // 可以在这里添加外部链接URL
        }
      ]
    };
  },
  created() {
    this.$store.commit("updateActiveIndex", "5");
    this.loadData();
  },
  methods: {
    loadData() {
      this.loadExperts();
      this.loadQuestions();
      this.loadKnowledge();
    },
    // 加载专家列表
    loadExperts() {
      selectExpert({
        pageNum: this.expertCount,
        keys: '',
        pageSize: 5
      }).then((res) => {
        if (res && res.flag == true) {
          this.expertList = res.data.list || [];
          this.expertTotal = res.data.total || 0;
        }
      }).catch(err => {
        console.log(err);
      });
    },
    // 加载热门问答
    loadQuestions() {
      // 先设置静态数据，确保页面有内容显示
      this.hotQuestions = [...this.staticHotQuestions].slice(0, 3);
      
      // 尝试从后端加载数据（异步更新）
      selectQuestions({
        pageNum: this.questionCount,
        keys: '',
        pageSize: 3
      }).then((res) => {
        if (res && res.flag == true && res.data && res.data.list && res.data.list.length > 0) {
          // 将后端数据合并到列表中
          const backendList = res.data.list.map(item => ({
            questionId: item.questionId,
            title: item.title,
            questioner: item.questioner || '匿名用户',
            expertName: item.expertName || '',
            status: item.status || 0,
            createTime: item.createTime
          }));
          // 合并静态数据和后端数据，按时间倒序排列
          let combinedList = [...this.staticHotQuestions, ...backendList];
          combinedList.sort((a, b) => {
            const dateA = new Date(a.createTime || 0);
            const dateB = new Date(b.createTime || 0);
            return dateB - dateA;
          });
          this.hotQuestions = combinedList.slice(0, 3);
          this.questionTotal = res.data.total || combinedList.length;
        } else {
          // 如果后端没有数据，使用静态数据
          this.questionTotal = this.staticHotQuestions.length;
        }
      }).catch(err => {
        console.log('加载后端数据失败，使用静态数据:', err);
        // 如果后端请求失败，保持使用静态数据（已经在上面设置了）
        this.questionTotal = this.staticHotQuestions.length;
      });
    },
    // 加载指导知识
    loadKnowledge() {
      // 先使用静态数据
      let combinedList = [...this.staticKnowledgeList];
      
      // 尝试加载后端数据，如果成功则合并
      selectKnowledgesPage({
        pageNum: this.knowledgeCount,
        pageSize: 100 // 获取更多数据以便排序和显示
      }).then((res) => {
        if (res && res.flag == true) {
          let list = res.data.list || [];
          // 将后端数据映射为包含url字段的格式
          const backendList = list.map(item => ({
            knowledgeId: item.knowledgeId,
            title: item.title,
            createTime: item.createTime,
            updateTime: item.updateTime,
            url: item.url || '' // 支持后端返回的URL字段
          }));
          // 合并静态数据和后端数据
          combinedList = [...this.staticKnowledgeList, ...backendList];
          this.knowledgeTotal = res.data.total || 0;
        }
      }).catch(err => {
        console.log(err);
        // 如果后端请求失败，只使用静态数据
      }).finally(() => {
        // 按日期倒序排列（最新的在前）
        combinedList.sort((a, b) => {
          const dateA = new Date(a.createTime || a.updateTime || 0);
          const dateB = new Date(b.createTime || b.updateTime || 0);
          return dateB - dateA;
        });
        this.knowledgeList = combinedList;
        this.knowledgeTotal = combinedList.length;
      });
    },
    // 跳转到所有知识列表页面
    goToAllKnowledgeList() {
      this.$router.push("/home/expertKnowledgeList").catch((err) => err);
    },
    // 快速操作
    handleQuickAction(type) {
      if (type === 'question') {
        this.$router.push("/home/onlineQuestions").catch((err) => err);
      } else if (type === 'appointment') {
        this.$router.push("/home/appointment").catch((err) => err);
      } else if (type === 'allExpert') {
        this.$router.push("/home/allExpert").catch((err) => err);
      } else if (type === 'knowledge') {
        this.$router.push("/home/knowledge").catch((err) => err);
      }
    },
    // 查看所有专家
    goToAllExperts() {
      this.$router.push("/home/allExpert").catch((err) => err);
    },
    // 查看所有问答
    goToAllQuestions() {
      this.$router.push("/home/allQuestions").catch((err) => err);
    },
    // 查看所有知识（保留用于其他地方）
    goToKnowledge() {
      this.$router.push("/home/knowledge").catch((err) => err);
    },
    // 提问
    handleQuestion(item) {
      if (localStorage.getItem('token')) {
        this.$router.push(`/home/question?id=${item.userName}`).catch((err) => err);
      } else {
        this.$message.error('请先登录');
        this.$router.push(`/login`).catch((err) => err);
      }
    },
    // 预约
    handleAppointment(item) {
      if (localStorage.getItem('token')) {
        this.$router.push(`/home/appointment?id=${item.userName}`).catch((err) => err);
      } else {
        this.$message.error('请先登录');
        this.$router.push(`/login`).catch((err) => err);
      }
    },
    // 问答详情
    handleQuestionDetail(item) {
      this.$router.push(`/home/guide/${item.id}`).catch((err) => err);
    },
    // 知识详情
    handleKnowledgeDetail(item) {
      this.$router.push(`/home/knowledge/${item.knowledgeId}`).catch((err) => err);
    },
    // 格式化日期
    formatDate(value) {
      if (!value) return '';
      let date = new Date(value);
      let y = date.getFullYear();
      let MM = date.getMonth() + 1;
      MM = MM < 10 ? "0" + MM : MM;
      let d = date.getDate();
      d = d < 10 ? "0" + d : d;
      return y + "-" + MM + "-" + d;
    },
    // 处理图片加载错误
    handleImageError(e) {
      e.target.src = require('@/assets/img/placeholder.jpg');
    }
  },
  computed: {
    // 只显示前几条知识
    displayedKnowledgeList() {
      return this.knowledgeList.slice(0, this.initialDisplayCount);
    }
  }
};
</script>

<style lang="less" scoped>
.expert-guide-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

// 顶部横幅
.expert-banner {
  width: 100%;
  height: 280px;
  background: linear-gradient(135deg, #67C23A 0%, #035D1C 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 20px;

  .banner-content {
    width: 1100px;
    text-align: center;

    .banner-title {
      font-size: 36px;
      font-weight: bold;
      margin-bottom: 15px;
    }

    .banner-desc {
      font-size: 18px;
      margin-bottom: 30px;
      opacity: 0.9;
    }

    .banner-stats {
      display: flex;
      justify-content: center;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .stat-number {
          font-size: 32px;
          font-weight: bold;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          opacity: 0.9;
        }
      }
    }
  }
}

// 快速入口
.quick-actions {
  width: 1100px;
  margin: 0 auto 20px;
  display: flex;
  gap: 15px;
  justify-content: space-between;

  .action-card {
    flex: 1;
    background-color: white;
    border-radius: 8px;
    padding: 25px 20px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
    }

    .action-icon {
      font-size: 40px;
      margin-bottom: 10px;
    }

    .action-title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin-bottom: 8px;
    }

    .action-desc {
      font-size: 12px;
      color: #666;
    }
  }
}

// 主要内容区
.main-content {
  width: 1100px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  justify-content: space-between;
}

// 左侧内容
.left-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// 右侧内容
.right-content {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// 内容区块
.content-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f0f0f0;

    .section-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
      margin: 0;
    }

    .more-link {
      font-size: 14px;
      color: #67C23A;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        color: #035D1C;
        text-decoration: underline;
      }
    }
  }
}

// 问答列表
.question-list {
  .question-item {
    display: flex;
    gap: 15px;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background-color: #f9f9f9;
      padding-left: 10px;
    }

    &:last-child {
      border-bottom: none;
    }

    .question-status {
      .status-badge {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 4px;
        font-size: 12px;

        &.pending {
          background-color: #fff3cd;
          color: #856404;
        }

        &.answered {
          background-color: #d4edda;
          color: #155724;
        }
      }
    }

    .question-content {
      flex: 1;

      .question-title {
        font-size: 15px;
        font-weight: 500;
        color: #333;
        margin-bottom: 8px;
        line-height: 1.5;

        &:hover {
          color: #67C23A;
        }
      }

      .question-meta {
        font-size: 12px;
        color: #999;
        display: flex;
        gap: 15px;
      }
    }
  }
}

// 知识列表（旧样式，保留以防其他地方使用）
.knowledge-list {
  .knowledge-item {
    display: flex;
    gap: 15px;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background-color: #f9f9f9;
      padding-left: 10px;
    }

    &:last-child {
      border-bottom: none;
    }

    .knowledge-image {
      width: 120px;
      height: 80px;
      border-radius: 6px;
      overflow: hidden;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .knowledge-content {
      flex: 1;

      .knowledge-title {
        font-size: 15px;
        font-weight: 500;
        color: #333;
        margin-bottom: 8px;
        line-height: 1.5;

        &:hover {
          color: #67C23A;
        }
      }

      .knowledge-desc {
        font-size: 13px;
        color: #666;
        line-height: 1.6;
        margin-bottom: 8px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .knowledge-meta {
        font-size: 12px;
        color: #999;
        display: flex;
        gap: 15px;
      }
    }
  }
}

// 简化的知识列表样式（新样式）
.knowledge-list-simple {
  .knowledge-item-simple {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    transition: all 0.2s;

    &:hover {
      background-color: #f9f9f9;
    }

    &:last-child {
      border-bottom: none;
    }

    .knowledge-title-link {
      flex: 1;
      font-size: 14px;
      color: #333;
      text-decoration: none;
      cursor: pointer;
      line-height: 1.6;
      padding-right: 20px;
      transition: color 0.2s;

      &:hover {
        color: #67C23A;
        text-decoration: underline;
      }
    }

    .knowledge-date {
      font-size: 14px;
      color: #666;
      white-space: nowrap;
      flex-shrink: 0;
    }
  }
}

// 专家列表
.expert-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f0f0f0;

    .section-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
      margin: 0;
    }

    .more-link {
      font-size: 14px;
      color: #67C23A;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        color: #035D1C;
        text-decoration: underline;
      }
    }
  }

  .expert-list {
    .expert-item {
      display: flex;
      gap: 15px;
      padding: 15px 0;
      align-items: flex-start;

      .expert-avatar {
        width: 70px;
        height: 70px;
        border-radius: 6px;
        flex-shrink: 0;
      }

      .expert-info {
        flex: 1;

        .expert-name {
          font-size: 16px;
          font-weight: bold;
          color: #333;
          margin-bottom: 5px;
        }

        .expert-position {
          font-size: 13px;
          color: #67C23A;
          margin-bottom: 5px;
        }

        .expert-profession,
        .expert-belong,
        .expert-phone {
          font-size: 12px;
          color: #666;
          line-height: 1.8;
        }

        .expert-actions {
          margin-top: 10px;
          display: flex;
          gap: 10px;

          .action-btn {
            font-size: 13px;
            color: #67C23A;
            cursor: pointer;
            padding: 4px 10px;
            border: 1px solid #67C23A;
            border-radius: 4px;
            transition: all 0.2s;

            &:hover {
              background-color: #67C23A;
              color: white;
            }
          }
        }
      }
    }
  }
}

// 服务说明
.service-info {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .section-header {
    margin-bottom: 15px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f0f0f0;

    .section-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
      margin: 0;
    }
  }

  .service-content {
    .service-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 0;

      .service-icon {
        width: 24px;
        height: 24px;
        background-color: #67C23A;
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        font-weight: bold;
        flex-shrink: 0;
      }

      .service-text {
        font-size: 14px;
        color: #666;
        line-height: 1.6;
      }
    }
  }
}

// 空状态
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}
</style>

