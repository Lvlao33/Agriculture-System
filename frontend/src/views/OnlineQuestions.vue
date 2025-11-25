<template>
  <div class="online-questions-container">
    <div class="questions-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <h2 class="page-title">在线问答</h2>
        <el-button 
          type="primary" 
          icon="el-icon-edit" 
          class="ask-btn"
          @click="goToAskQuestion"
        >
          我要提问
        </el-button>
      </div>

      <!-- 问答列表 -->
      <div class="questions-list">
        <div 
          class="question-item" 
          v-for="(item, index) in questionsList" 
          :key="index"
          @click="goToDetail(item)"
        >
          <div class="question-main">
            <h3 class="question-title">{{ item.title }}</h3>
            <div class="question-meta">
              <span class="meta-item">
                <i class="el-icon-chat-line-round"></i>
                <span>{{ item.answerCount || 0 }} 个回答</span>
              </span>
              <span class="meta-item">
                <i class="el-icon-user"></i>
                <span>提问者：{{ item.questioner }}</span>
              </span>
              <span class="meta-item">
                <i class="el-icon-time"></i>
                <span>最后更新：{{ formatDate(item.lastUpdateTime || item.createTime) }}</span>
              </span>
              <span v-if="item.expertName" class="meta-item">
                <i class="el-icon-medal"></i>
                <span>专家：{{ item.expertName }}</span>
              </span>
            </div>
          </div>
          <div class="question-status">
            <el-tag v-if="item.answerCount > 0" type="success" size="small">已解答</el-tag>
            <el-tag v-else type="info" size="small">待解答</el-tag>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="questionsList.length === 0" class="empty-state">
          <i class="el-icon-chat-line-square"></i>
          <p>暂无提问记录</p>
          <el-button type="primary" @click="goToAskQuestion">去提问</el-button>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :page-size="pageSize"
          :total="total"
          :current-page="currentPage"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
// import { getQuestionsList } from '@/api/qa'

export default {
  name: 'OnlineQuestions',
  data() {
    return {
      questionsList: [],
      total: 0,
      pageSize: 10,
      currentPage: 1
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '4')
    this.loadQuestions()
  },
  methods: {
    // 加载问答列表
    loadQuestions() {
      // TODO: 调用后端接口
      // getQuestionsList({
      //   pageNum: this.currentPage,
      //   pageSize: this.pageSize
      // }).then(res => {
      //   if (res.flag) {
      //     this.questionsList = res.data.list || []
      //     this.total = res.data.total || 0
      //   }
      // })

      // 临时使用模拟数据
      const allData = this.getMockData()
      this.total = allData.length
      // 分页处理
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      this.questionsList = allData.slice(start, end)
    },
    // 模拟数据
    getMockData() {
      return [
        {
          id: 1,
          title: '冬季大棚蔬菜如何预防冻害？需要采取哪些具体措施？',
          questioner: '张农户',
          expertName: '李教授',
          answerCount: 3,
          createTime: '2025-01-15 10:30:00',
          lastUpdateTime: '2025-01-15 14:20:00'
        },
        {
          id: 2,
          title: '小麦晚播后如何管理才能保证产量？有什么注意事项？',
          questioner: '王农户',
          expertName: '刘专家',
          answerCount: 2,
          createTime: '2025-01-14 09:15:00',
          lastUpdateTime: '2025-01-14 16:45:00'
        },
        {
          id: 3,
          title: '油菜田出现黄叶是什么原因？应该怎么处理？',
          questioner: '赵农户',
          expertName: '陈教授',
          answerCount: 1,
          createTime: '2025-01-13 11:20:00',
          lastUpdateTime: '2025-01-13 15:30:00'
        },
        {
          id: 4,
          title: '连阴雨天气对冬小麦播种有什么影响？如何应对？',
          questioner: '孙农户',
          expertName: '周专家',
          answerCount: 4,
          createTime: '2025-01-12 08:45:00',
          lastUpdateTime: '2025-01-12 17:10:00'
        },
        {
          id: 5,
          title: '土壤墒情不好，如何改善？有什么快速有效的方法？',
          questioner: '李农户',
          expertName: '吴教授',
          answerCount: 2,
          createTime: '2025-01-11 14:30:00',
          lastUpdateTime: '2025-01-11 18:20:00'
        },
        {
          id: 6,
          title: '如何科学防治小麦田杂草？使用什么药剂比较好？',
          questioner: '周农户',
          expertName: '郑专家',
          answerCount: 0,
          createTime: '2025-01-10 10:00:00',
          lastUpdateTime: '2025-01-10 10:00:00'
        },
        {
          id: 7,
          title: '大豆收获期遇到连阴雨怎么办？如何减少损失？',
          questioner: '吴农户',
          answerCount: 1,
          createTime: '2025-01-09 13:25:00',
          lastUpdateTime: '2025-01-09 16:40:00'
        },
        {
          id: 8,
          title: '冬油菜苗期病虫害如何防控？推荐什么防治方案？',
          questioner: '郑农户',
          expertName: '王教授',
          answerCount: 3,
          createTime: '2025-01-08 09:50:00',
          lastUpdateTime: '2025-01-08 14:15:00'
        },
        {
          id: 9,
          title: '玉米种植过程中如何合理施肥？不同生长阶段有什么要求？',
          questioner: '钱农户',
          expertName: '孙专家',
          answerCount: 2,
          createTime: '2025-01-07 11:15:00',
          lastUpdateTime: '2025-01-07 15:50:00'
        },
        {
          id: 10,
          title: '果树修剪的最佳时间是什么时候？有什么技巧？',
          questioner: '周农户',
          expertName: '李教授',
          answerCount: 1,
          createTime: '2025-01-06 08:30:00',
          lastUpdateTime: '2025-01-06 12:20:00'
        },
        {
          id: 11,
          title: '温室大棚温度控制有什么好方法？',
          questioner: '张农户',
          answerCount: 0,
          createTime: '2025-01-05 10:00:00',
          lastUpdateTime: '2025-01-05 10:00:00'
        },
        {
          id: 12,
          title: '有机肥和化肥如何搭配使用效果最好？',
          questioner: '王农户',
          expertName: '刘专家',
          answerCount: 4,
          createTime: '2025-01-04 14:20:00',
          lastUpdateTime: '2025-01-04 19:30:00'
        }
      ].sort((a, b) => {
        // 按最后更新时间倒序排列
        return new Date(b.lastUpdateTime) - new Date(a.lastUpdateTime)
      })
    },
    // 分页变化
    handlePageChange(page) {
      this.currentPage = page
      this.loadQuestions()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    // 跳转到提问页面
    goToAskQuestion() {
      if (localStorage.getItem('token')) {
        this.$router.push('/home/askQuestion').catch(() => {})
      } else {
        this.$message.warning('请先登录')
        this.$router.push('/login').catch(() => {})
      }
    },
    // 跳转到详情页面
    goToDetail(item) {
      this.$router.push(`/home/questionDetail/${item.id}`).catch(() => {})
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style lang="less" scoped>
.online-questions-container {
  width: 100%;
  min-height: calc(100vh - 200px);
  background-color: #f5f5f5;
  padding: 20px 0;

  .questions-wrapper {
    width: 1100px;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 2px solid #67C23A;

      .page-title {
        font-size: 28px;
        font-weight: bold;
        color: #333;
        margin: 0;
      }

      .ask-btn {
        background-color: #67C23A;
        border-color: #67C23A;
        font-size: 16px;
        padding: 12px 24px;

        &:hover {
          background-color: #5daf34;
          border-color: #5daf34;
        }
      }
    }

    .questions-list {
      min-height: 400px;

      .question-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px;
        margin-bottom: 15px;
        background-color: #fafafa;
        border: 1px solid #e8e8e8;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background-color: #f0f9ff;
          border-color: #67C23A;
          box-shadow: 0 4px 12px rgba(103, 194, 58, 0.15);
          transform: translateY(-2px);
        }

        .question-main {
          flex: 1;
          margin-right: 20px;

          .question-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin: 0 0 12px 0;
            line-height: 1.5;
            cursor: pointer;

            &:hover {
              color: #67C23A;
            }
          }

          .question-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            font-size: 14px;
            color: #666;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 5px;

              i {
                color: #67C23A;
                font-size: 16px;
              }
            }
          }
        }

        .question-status {
          flex-shrink: 0;
        }
      }

      .empty-state {
        text-align: center;
        padding: 80px 20px;
        color: #999;

        i {
          font-size: 64px;
          color: #ddd;
          margin-bottom: 20px;
        }

        p {
          font-size: 16px;
          margin-bottom: 20px;
        }
      }
    }

    .pagination-wrapper {
      margin-top: 30px;
      display: flex;
      justify-content: center;
      padding: 20px 0;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .online-questions-container .questions-wrapper {
    width: 95%;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .online-questions-container .questions-wrapper {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;

      .ask-btn {
        width: 100%;
      }
    }

    .questions-list .question-item {
      flex-direction: column;
      align-items: flex-start;

      .question-main {
        margin-right: 0;
        margin-bottom: 15px;
        width: 100%;
      }

      .question-status {
        align-self: flex-end;
      }
    }
  }
}
</style>

