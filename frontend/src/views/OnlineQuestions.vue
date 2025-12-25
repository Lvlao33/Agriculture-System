<template>
  <div class="online-questions-container">
    <div class="questions-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <h2 class="page-title">在线问答</h2>
        <div class="search-action-row">
          <el-input
            v-model.trim="searchKeyword"
            placeholder="输入关键词搜索问答记录"
            clearable
            prefix-icon="el-icon-search"
            class="search-input"
            @keyup.enter.native="handleSearch"
            @clear="handleSearch"
          ></el-input>
          <div class="action-buttons">
            <el-button
              class="ask-mini-btn"
              type="primary"
              plain
              @click="goToAskQuestion"
            >
              我要提问
            </el-button>
            <el-button
              class="records-btn"
              type="primary"
              plain
              @click="goToMyQuestions"
            >
              我的问答记录
            </el-button>
            <el-button
              class="back-to-guide-btn"
              type="default"
              plain
              @click="goBackGuide"
            >
              返回
            </el-button>
          </div>
        </div>
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
                <span>提问者：{{ item.userName || item.questioner || '匿名用户' }}</span>
              </span>
              <span class="meta-item">
                <i class="el-icon-time"></i>
                <span>最后更新：{{ formatDate(item.updateTime || item.createTime) }}</span>
              </span>
              <span v-if="item.expert && item.expert.name" class="meta-item">
                <i class="el-icon-medal"></i>
                <span>专家：{{ item.expert.name }}</span>
              </span>
            </div>

          </div>
          <div class="question-status">
            <el-tag v-if="item.status === 'ANSWERED' || item.answerCount > 0" type="success" size="small">已解答</el-tag>
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
import { getQuestionsList } from '@/api/qa'

export default {
  name: 'OnlineQuestions',
  data() {
    return {
      questionsList: [],
      allQuestions: [],
      total: 0,
      pageSize: 10,
      currentPage: 1,
      searchKeyword: ''
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '4')
    this.loadQuestions()
  },
  methods: {
    // 加载问答列表
    async loadQuestions() {
      try {
        const res = await getQuestionsList({
          pageNum: 1,
          pageSize: 1000,
          mine: false
        })
        console.log('获取全部问题 - 返回数据:', res)
        
        // 处理不同的返回格式
        let list = []
        if (res) {
          // 后端返回格式: { flag: true, data: [...], total: x, pageNum: x, pageSize: x }
          if (res.flag && res.data && Array.isArray(res.data)) {
            list = res.data
            console.log('使用 res.data，数量:', list.length)
          }
          // ApiResponse格式: { success: true, data: [...], message: "OK" }
          else if (res.data && Array.isArray(res.data)) {
            list = res.data
            console.log('使用 res.data (无flag)，数量:', list.length)
          } 
          // 直接是数组
          else if (Array.isArray(res)) {
            list = res
            console.log('res本身是数组，数量:', list.length)
          }
          // 其他格式尝试
          else if (res.list && Array.isArray(res.list)) {
            list = res.list
            console.log('使用 res.list，数量:', list.length)
          } else {
            console.warn('无法解析数据格式，res:', res)
          }
        } else {
          console.warn('返回数据为空')
        }
        
        console.log('解析后的问题列表:', list)
        this.allQuestions = list || []
        this.applyFilter()
      } catch (e) {
        console.error('加载问答列表失败:', e)
        // 如果API调用失败，使用模拟数据作为降级方案
      const allData = this.getMockData()
      this.allQuestions = allData
      this.applyFilter()
      }
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
      this.applyFilter()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    // 关键词搜索
    handleSearch() {
      this.currentPage = 1
      this.applyFilter()
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
    // 返回到来源页面（优先使用历史回退），若无法回退则返回到专家指导页
    goBackGuide() {
      try {
        if (window.history && window.history.length > 1) {
          // 尝试回退到上一个页面
          this.$router.go(-1);
        } else {
          // 没有历史记录可回退，回到专家指导页作为兜底
          this.$router.push('/home/guide').catch(() => {});
        }
      } catch (e) {
        // 保底跳转
        this.$router.push('/home/guide').catch(() => {});
      }
    },
    // 根据关键词过滤并分页
    applyFilter() {
      const keyword = this.searchKeyword.trim().toLowerCase()
      const source = this.allQuestions || []
      const filtered = keyword
        ? source.filter(item => {
            const title = (item.title || '').toLowerCase()
            const userName = ((item.userName || item.questioner) || '').toLowerCase()
            const content = (item.content || '').toLowerCase()
            return title.includes(keyword) || userName.includes(keyword) || content.includes(keyword)
          })
        : source
      // 按最后更新时间倒序排列（优先使用updateTime，如果没有则使用createTime）
      filtered.sort((a, b) => {
        const timeA = new Date(a.updateTime || a.createTime || 0).getTime()
        const timeB = new Date(b.updateTime || b.createTime || 0).getTime()
        return timeB - timeA
      })
      this.total = filtered.length
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      this.questionsList = filtered.slice(start, end)
    },
    goToMyQuestions() {
      if (localStorage.getItem('token')) {
        this.$router.push('/home/myQuestions').catch(() => {})
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
      flex-direction: column;
      align-items: stretch;
      gap: 6px;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 2px solid #67C23A;

      .page-title {
        font-size: 28px;
        font-weight: bold;
        color: #333;
        margin: 0;
        line-height: 1.2;
        height: auto;
      }

      .search-action-row {
        width: 100%;
        display: flex;
        align-items: center;
        gap: 18px;
        margin-top: -4px;
      }

      .search-input {
        flex: 1;
        max-width: 680px; /* 缩短搜索框，不再占满整行 */

        &::v-deep .el-input__inner {
          height: 46px;
          line-height: 46px;
          border-radius: 999px;
          border: none;
          background: #f5f6fb;
          box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.06);
          padding-left: 46px;
          font-size: 15px;
        }

        &::v-deep .el-input__prefix {
          left: 18px;
          display: flex;
          align-items: center;
          color: #6b6f7b;
        }

        &::v-deep .el-input__suffix {
          right: 14px;
          display: flex;
          align-items: center;
        }
      }


      .action-buttons {
        margin-left: auto;
        display: flex;
        flex-direction: row;
        align-items: center;
        flex-shrink: 0;
        gap: 12px;
      }

      .ask-mini-btn,
      .records-btn {
        height: 46px;
        line-height: 44px;
        padding: 0 22px;
        border-radius: 999px;
        font-size: 14px;
        border: 1px solid #5f8bff;
        transition: all 0.2s ease;
        color: #3062ff;
        background: rgba(95, 139, 255, 0.08);

        &:hover {
          background: rgba(95, 139, 255, 0.18);
          color: #1f3fbf;
          border-color: #2f54eb;
        }
      }
 
      /* 返回按钮样式：放在“我的问答记录”右侧 */
      .back-to-guide-btn {
        height: 48px;
        line-height: 46px;
        padding: 0 26px; /* 更长一些以增强可见性 */
        border-radius: 999px;
        font-size: 15px;
        border: none;
        color: #fff;
        background: linear-gradient(90deg, #4b79ff 0%, #3057ff 100%);
        font-weight: 600;
        box-shadow: 0 6px 14px rgba(48,87,255,0.12);
        transition: all 160ms ease;

        &:hover {
          background: linear-gradient(90deg, #405fef 0%, #274be6 100%);
          transform: translateY(-2px);
          box-shadow: 0 8px 18px rgba(39,75,230,0.14);
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


