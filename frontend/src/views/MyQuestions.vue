<template>
  <div class="online-questions-container">
    <div class="questions-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <el-button class="header-back" icon="el-icon-arrow-left" @click="goBackHistory">返回</el-button>
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
                <span>{{ getAnswerCount(item) }} 个回答</span>
              </span>
              <span class="meta-item">
                <i class="el-icon-time"></i>
                <span>提问时间：{{ formatDate(item.createTime || item.create_time) }}</span>
              </span>
              <span class="meta-item">
                <i class="el-icon-refresh"></i>
                <span>最后更新：{{ formatDate(item.updateTime || item.update_time || item.createTime || item.create_time) }}</span>
              </span>
            </div>
          </div>
          <div class="question-status">
            <el-tag v-if="item.status === 'ANSWERED' || item.status === 'answered'"
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
import { selectQuestionByUser } from '@/api/question'

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
        console.log('=== 获取我的问题 ===', res)
        let list = []
        if (res) {
          if (res.flag && res.data && Array.isArray(res.data)) {
            list = res.data
          } else if (res.data && Array.isArray(res.data)) {
            list = res.data
          } else if (Array.isArray(res)) {
            list = res
          } else if (res.list && Array.isArray(res.list)) {
            list = res.list
          }
        }
        this.allQuestions = list || []
        this.applyFilter()
        console.log('最终显示的问题数量:', this.questionsList.length)
      } catch (e) {
        console.error('加载问答记录失败:', e)
        this.allQuestions = []
        this.applyFilter()
        this.$message.error('加载问答记录失败: ' + (e.message || '未知错误'))
      }
    },
    applyFilter() {
      let filtered = this.allQuestions
      if (this.statusFilter === 'answered') {
        filtered = filtered.filter(
          q => q.status === 'ANSWERED' || q.status === 'answered'
        )
      } else if (this.statusFilter === 'pending') {
        filtered = filtered.filter(
          q => !q.status || q.status === 'PENDING' || q.status === 'pending'
        )
      }
      // 按最后更新时间倒序排列（优先使用updateTime，如果没有则使用createTime）
      filtered.sort((a, b) => {
        const timeA = new Date(a.updateTime || a.update_time || a.createTime || a.create_time || 0).getTime()
        const timeB = new Date(b.updateTime || b.update_time || b.createTime || b.create_time || 0).getTime()
        return timeB - timeA
      })
      this.total = filtered.length
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      this.questionsList = filtered.slice(start, end)
    },
    getAnswerCount(item) {
      // 如果后端返回了answerCount字段，使用它；否则返回0
      return item.answerCount || 0
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
    // 返回到来源页面（优先使用历史回退），若无法回退则返回到专家指导页
    goBackHistory() {
      try {
        if (window.history && window.history.length > 1) {
          this.$router.go(-1);
        } else {
          this.$router.push('/home/guide').catch(() => {});
        }
      } catch (e) {
        this.$router.push('/home/guide').catch(() => {});
      }
    },
    formatDate(value) {
      if (!value) return ''
      try {
        const date = new Date(value)
        if (isNaN(date.getTime())) return value
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch (e) {
        return value
      }
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
        .header-back {
          height: 36px;
          padding: 6px 12px;
          border-radius: 6px;
          background: #fff;
          border: 1px solid #e8e8e8;
          color: #666;
          box-shadow: 0 2px 6px rgba(0,0,0,0.04);
          margin-right: 12px;
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


