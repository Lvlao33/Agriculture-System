<template>
  <div class="online-questions-container">
    <div class="questions-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <h2 class="page-title">我的问答记录</h2>
        <div class="filter-row">
          <el-radio-group v-model="statusFilter" size="small" @change="applyFilter">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="answered">已解答</el-radio-button>
            <el-radio-button label="pending">待解答</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 问答列表 -->
      <div class="questions-list">
        <div
          class="question-item"
          v-for="item in questionsList"
          :key="item.id"
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
                <i class="el-icon-time"></i>
                <span>提问时间：{{ formatDate(item.createTime) }}</span>
              </span>
              <span v-if="item.lastUpdateTime" class="meta-item">
                <i class="el-icon-refresh"></i>
                <span>最后更新：{{ formatDate(item.lastUpdateTime) }}</span>
              </span>
            </div>
          </div>
          <div class="question-status">
            <el-tag v-if="item.status === 'ANSWERED' || (item.answerCount || 0) > 0"
                    type="success"
                    size="small">
              已解答
            </el-tag>
            <el-tag v-else type="info" size="small">待解答</el-tag>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="questionsList.length === 0" class="empty-state">
          <i class="el-icon-chat-line-square"></i>
          <p>还没有提过问题</p>
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
import { getQuestionsList } from '@/api/qa'

export default {
  name: 'MyQuestions',
  data() {
    return {
      allQuestions: [],
      questionsList: [],
      total: 0,
      pageSize: 10,
      currentPage: 1,
      statusFilter: 'all'
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '4')
    this.fetchMyQuestions()
  },
  methods: {
    async fetchMyQuestions() {
      try {
        const res = await getQuestionsList({
          pageNum: 1,
          pageSize: 1000,
          mine: true
        })
        const list = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : [])
        this.allQuestions = list || []
        this.applyFilter()
      } catch (e) {
        console.error(e)
        this.$message.error('加载问答记录失败')
      }
    },
    applyFilter() {
      let filtered = this.allQuestions
      if (this.statusFilter === 'answered') {
        filtered = filtered.filter(
          q => q.status === 'ANSWERED' || (q.answerCount || 0) > 0
        )
      } else if (this.statusFilter === 'pending') {
        filtered = filtered.filter(
          q => q.status === 'PENDING' || (q.answerCount || 0) === 0
        )
      }
      this.total = filtered.length
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      this.questionsList = filtered.slice(start, end)
    },
    handlePageChange(page) {
      this.currentPage = page
      this.applyFilter()
    },
    goToDetail(item) {
      if (!item || !item.id) return
      this.$router.push(`/home/questionDetail/${item.id}`).catch(() => {})
    },
    goToAskQuestion() {
      this.$router.push('/home/askQuestion').catch(() => {})
    },
    formatDate(value) {
      if (!value) return ''
      return new Date(value).toLocaleString('zh-CN')
    }
  }
}
</script>

<style lang="less" scoped>
.online-questions-container {
  width: 100%;
  min-height: calc(100vh - 200px);
  background-color: #f5f5f5;
  padding: 10px 0;

  .questions-wrapper {
    width: 1100px;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 8px;
    padding: 8px 30px 30px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 2px solid #67C23A;

      .page-title {
        font-size: 28px;
        font-weight: bold;
        color: #333;
        margin: 0;
      }

      .filter-row {
        display: flex;
        align-items: center;
        gap: 10px;
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


