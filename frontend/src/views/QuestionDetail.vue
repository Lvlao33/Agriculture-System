<template>
  <div class="question-detail-container">
    <div class="detail-wrapper">
      <!-- 返回按钮 -->
      <div class="back-btn" @click="goBack">
        <i class="el-icon-arrow-left"></i>
        <span>返回列表</span>
      </div>

      <!-- 问题详情 -->
      <div class="question-section">
        <div class="question-header">
          <h2 class="question-title">{{ questionDetail.title }}</h2>
          <el-tag v-if="questionDetail.answerCount > 0" type="success" size="medium">已解答</el-tag>
          <el-tag v-else type="info" size="medium">待解答</el-tag>
        </div>
        
        <div class="question-content">
          <div class="content-text">{{ questionDetail.content }}</div>
          <div class="question-info">
            <span class="info-item">
              <i class="el-icon-user"></i>
              <span>提问者：{{ questionDetail.questioner }}</span>
            </span>
            <span class="info-item">
              <i class="el-icon-time"></i>
              <span>提问时间：{{ formatDate(questionDetail.createTime) }}</span>
            </span>
            <span v-if="questionDetail.lastUpdateTime" class="info-item">
              <i class="el-icon-refresh"></i>
              <span>最后更新：{{ formatDate(questionDetail.lastUpdateTime) }}</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 回答列表 -->
      <div class="answers-section">
        <div class="section-header">
          <h3 class="section-title">
            <i class="el-icon-chat-line-round"></i>
            回答 ({{ answersList.length }})
          </h3>
        </div>

        <div class="answers-list">
          <div 
            class="answer-item" 
            v-for="(answer, index) in answersList" 
            :key="index"
          >
            <div class="answer-header">
              <div class="answer-author">
                <i class="el-icon-medal"></i>
                <span class="author-name">{{ answer.expertName || '专家' }}</span>
                <el-tag v-if="answer.expertName" type="warning" size="mini">专家</el-tag>
              </div>
              <span class="answer-time">{{ formatDate(answer.createTime) }}</span>
            </div>
            <div class="answer-content">{{ answer.content }}</div>
          </div>

          <div v-if="answersList.length === 0" class="empty-answers">
            <i class="el-icon-chat-line-square"></i>
            <p>暂无回答，等待专家解答...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import { getQuestionDetail, getAnswersList } from '@/api/qa'

export default {
  name: 'QuestionDetail',
  data() {
    return {
      questionId: null,
      questionDetail: {},
      answersList: []
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '4')
    this.questionId = this.$route.params.id
    this.loadQuestionDetail()
    this.loadAnswers()
  },
  methods: {
    // 加载问题详情
    loadQuestionDetail() {
      // TODO: 调用后端接口
      // getQuestionDetail({ id: this.questionId }).then(res => {
      //   if (res.flag) {
      //     this.questionDetail = res.data
      //   }
      // })

      // 临时使用模拟数据
      const mockQuestions = this.getMockQuestions()
      this.questionDetail = mockQuestions.find(q => q.id == this.questionId) || {
        id: this.questionId,
        title: '示例问题：如何提高农作物产量？',
        content: '我在种植过程中遇到了一些问题，想请教专家如何提高农作物的产量。具体来说，我想了解施肥、灌溉、病虫害防治等方面的最佳实践。',
        questioner: '张农户',
        createTime: '2025-01-15 10:30:00',
        lastUpdateTime: '2025-01-15 14:20:00',
        answerCount: 2
      }
    },
    // 加载回答列表
    loadAnswers() {
      // TODO: 调用后端接口
      // getAnswersList({ questionId: this.questionId }).then(res => {
      //   if (res.flag) {
      //     this.answersList = res.data.list || []
      //   }
      // })

      // 临时使用模拟数据
      const mockAnswers = this.getMockAnswers()
      this.answersList = mockAnswers.filter(a => a.questionId == this.questionId)
    },
    // 模拟问题数据
    getMockQuestions() {
      return [
        {
          id: 1,
          title: '冬季大棚蔬菜如何预防冻害？需要采取哪些具体措施？',
          content: '最近天气转冷，我担心大棚里的蔬菜会受冻。请问专家，冬季大棚蔬菜如何预防冻害？需要采取哪些具体措施？包括温度控制、覆盖物选择、通风管理等方面。',
          questioner: '张农户',
          createTime: '2025-01-15 10:30:00',
          lastUpdateTime: '2025-01-15 14:20:00',
          answerCount: 3
        },
        {
          id: 2,
          title: '小麦晚播后如何管理才能保证产量？有什么注意事项？',
          content: '由于天气原因，我的小麦播种时间比往年晚了半个月。请问专家，小麦晚播后如何管理才能保证产量？有什么需要特别注意的事项吗？',
          questioner: '王农户',
          createTime: '2025-01-14 09:15:00',
          lastUpdateTime: '2025-01-14 16:45:00',
          answerCount: 2
        }
      ]
    },
    // 模拟回答数据
    getMockAnswers() {
      return [
        {
          id: 1,
          questionId: 1,
          expertName: '李教授',
          content: '冬季大棚蔬菜防冻害的关键措施包括：1. 保持棚内温度，夜间不低于8-10℃；2. 使用双层膜或保温被覆盖；3. 合理通风，避免湿度过大；4. 增施有机肥提高地温；5. 必要时使用加温设备。',
          createTime: '2025-01-15 11:20:00'
        },
        {
          id: 2,
          questionId: 1,
          expertName: '王专家',
          content: '补充一点，还要注意及时清理棚膜上的积雪，保持透光性。同时可以在棚内放置一些水桶，利用水的比热容大的特性来稳定温度。',
          createTime: '2025-01-15 13:15:00'
        },
        {
          id: 3,
          questionId: 1,
          expertName: '刘教授',
          content: '另外，建议选择抗寒性强的品种，并适当增加种植密度，利用群体效应提高抗寒能力。',
          createTime: '2025-01-15 14:20:00'
        },
        {
          id: 4,
          questionId: 2,
          expertName: '刘专家',
          content: '小麦晚播后管理要点：1. 适当增加播种量，保证基本苗数；2. 增施底肥，特别是磷肥；3. 加强冬前管理，促进分蘖；4. 春季早追肥，促进生长；5. 注意病虫害防治。',
          createTime: '2025-01-14 10:30:00'
        },
        {
          id: 5,
          questionId: 2,
          expertName: '陈专家',
          content: '晚播小麦要特别注意冬前管理，如果分蘖不足，可以在返青期适当增加追肥量，但要控制好氮肥用量，避免贪青晚熟。',
          createTime: '2025-01-14 16:45:00'
        }
      ]
    },
    // 返回上一页
    goBack() {
      this.$router.go(-1)
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
.question-detail-container {
  width: 100%;
  min-height: calc(100vh - 200px);
  background-color: #f5f5f5;
  padding: 20px 0;

  .detail-wrapper {
    width: 1100px;
    margin: 0 auto;

    .back-btn {
      display: inline-flex;
      align-items: center;
      gap: 5px;
      color: #67C23A;
      cursor: pointer;
      font-size: 14px;
      margin-bottom: 20px;
      padding: 8px 12px;
      border-radius: 4px;
      transition: all 0.3s;

      &:hover {
        background-color: #f0f9ff;
        color: #5daf34;
      }

      i {
        font-size: 16px;
      }
    }

    .question-section {
      background-color: #fff;
      border-radius: 8px;
      padding: 30px;
      margin-bottom: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .question-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 20px;
        padding-bottom: 20px;
        border-bottom: 2px solid #f0f0f0;

        .question-title {
          flex: 1;
          font-size: 24px;
          font-weight: bold;
          color: #333;
          margin: 0;
          line-height: 1.5;
        }
      }

      .question-content {
        .content-text {
          font-size: 16px;
          line-height: 1.8;
          color: #333;
          margin-bottom: 20px;
          white-space: pre-wrap;
          word-break: break-word;
        }

        .question-info {
          display: flex;
          flex-wrap: wrap;
          gap: 20px;
          padding-top: 15px;
          border-top: 1px solid #f0f0f0;
          font-size: 14px;
          color: #666;

          .info-item {
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
    }

    .answers-section {
      background-color: #fff;
      border-radius: 8px;
      padding: 30px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .section-header {
        margin-bottom: 25px;
        padding-bottom: 15px;
        border-bottom: 2px solid #f0f0f0;

        .section-title {
          font-size: 20px;
          font-weight: bold;
          color: #333;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 8px;

          i {
            color: #67C23A;
            font-size: 22px;
          }
        }
      }

      .answers-list {
        .answer-item {
          padding: 20px;
          margin-bottom: 20px;
          background-color: #fafafa;
          border-left: 4px solid #67C23A;
          border-radius: 4px;
          transition: all 0.3s;

          &:hover {
            background-color: #f0f9ff;
            box-shadow: 0 2px 8px rgba(103, 194, 58, 0.1);
          }

          .answer-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;

            .answer-author {
              display: flex;
              align-items: center;
              gap: 8px;

              i {
                color: #ff9800;
                font-size: 18px;
              }

              .author-name {
                font-weight: 600;
                color: #333;
                font-size: 16px;
              }
            }

            .answer-time {
              font-size: 14px;
              color: #999;
            }
          }

          .answer-content {
            font-size: 15px;
            line-height: 1.8;
            color: #333;
            white-space: pre-wrap;
            word-break: break-word;
          }
        }

        .empty-answers {
          text-align: center;
          padding: 60px 20px;
          color: #999;

          i {
            font-size: 48px;
            color: #ddd;
            margin-bottom: 15px;
          }

          p {
            font-size: 16px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .question-detail-container .detail-wrapper {
    width: 95%;
    padding: 0 10px;
  }
}

@media (max-width: 768px) {
  .question-detail-container .detail-wrapper {
    .question-section,
    .answers-section {
      padding: 20px;
    }

    .question-section .question-header {
      flex-direction: column;
      gap: 15px;
    }
  }
}
</style>

