<template>
  <div class="expert-knowledge-list-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">📖 专家推荐</h1>
        <p class="page-desc">专家推荐相关技术文章，供您按需选择</p>
      </div>
    </div>

    <!-- 知识列表 -->
    <div class="knowledge-list-wrapper">
      <div class="content-section">
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
        <!-- 分页组件 -->
        <div class="knowledge-pagination" v-if="knowledgeList.length > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :page-size="knowledgePageSize"
            :total="knowledgeList.length"
            :current-page="knowledgeCurrentPage"
            @current-change="handleKnowledgePageChange"
            small
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getKnowledgeList } from '../api/expert';

export default {
  name: 'ExpertKnowledgeList',
  data() {
    return {
      knowledgeList: [],
      knowledgeCount: 1,
      knowledgeCurrentPage: 1, // 当前页码
      knowledgePageSize: 8, // 每页显示8条
      // 静态农业技术指导文档列表
      staticKnowledgeList: [
        {
          knowledgeId: 'static-1',
          title: '2025年冬季蔬菜生产技术指导意见',
          createTime: '2025-11-11',
          updateTime: '2025-11-11',
          url: 'https://www.natesc.org.cn/News/des?kind=&id=b1210a7b-fb54-4f15-96db-705a92001334&CategoryId=11a63552-05c9-475e-a504-0392e64ead0b' // 可以在这里添加外部链接URL
        },
        {
          knowledgeId: 'static-2',
          title: '11月11日北方冬麦区土壤墒情状况',
          createTime: '2025-11-11',
          updateTime: '2025-11-11',
          url: 'https://www.natesc.org.cn/News/des?kind=&id=be44b30f-bb35-4b2e-88f9-0f4ea7841424&CategoryId=11a63552-05c9-475e-a504-0392e64ead0b' // 可以在这里添加外部链接URL
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
          title: '因墒分类施策科学应对"烂秋"',
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
    this.loadKnowledge();
  },
  methods: {
    // 加载知识列表
    loadKnowledge() {
      // 先设置静态数据，确保页面有内容显示
      let combinedList = [...this.staticKnowledgeList];

      // 按日期倒序排列（最新的在前）
      combinedList.sort((a, b) => {
        const dateA = new Date(a.createTime || a.updateTime || 0);
        const dateB = new Date(b.createTime || b.updateTime || 0);
        return dateB - dateA;
      });
      this.knowledgeList = combinedList;

      // 尝试从后端加载数据（异步更新）
      getKnowledgeList({
        pageNum: this.knowledgeCount,
        pageSize: 8
      }).then(res => {
        if (res.flag && res.data && res.data.list) {
          // 将后端数据合并到列表中
          const backendList = res.data.list.map(item => ({
            knowledgeId: item.knowledgeId,
            title: item.title,
            createTime: item.createTime,
            updateTime: item.updateTime,
            url: item.url || '' // 支持后端返回的URL字段
          }));
          combinedList = [...this.staticKnowledgeList, ...backendList];
          // 按日期倒序排列（最新的在前）
          combinedList.sort((a, b) => {
            const dateA = new Date(a.createTime || a.updateTime || 0);
            const dateB = new Date(b.createTime || b.updateTime || 0);
            return dateB - dateA;
          });
          this.knowledgeList = combinedList;
        }
      }).catch(err => {
        console.log('加载后端数据失败，使用静态数据:', err);
        // 如果后端请求失败，保持使用静态数据（已经在上面设置了）
      });
    },
    // 知识详情
    handleKnowledgeDetail(item) {
      console.log('即将根据ID进行跳转, item:', item); // 调试日志

      // 1. 安全检查
      if (!item || !item.knowledgeId) {
        this.$message.warning('文章数据无效或缺少ID，无法查看详情');
        return;
      }

      // 2. 强制转换为字符串以进行判断
      // 后端返回的 ID 可能是 Long 类型，如果是数字，直接用 startsWith 会报错
      const idStr = String(item.knowledgeId);

      // 3. 判断是静态数据、有URL数据，还是需要路由跳转
      if (idStr.startsWith('static-')) {
          if (item.url) {
              window.open(item.url, '_blank');
          } else {
              this.$message.info('这是一篇静态示例文章，暂无详情页');
          }
          return;
      }

      // 4. 路由跳转到详情页
      // 注意：请确保在 router/index.js 中已配置 path: '/home/knowledgeDetail/:id'
      this.$router.push({
        path: `/home/knowledgeDetail/${item.knowledgeId}`
      }).catch(err => {
        console.error('路由跳转失败:', err);
      });
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
    // 处理知识分页变化
    handleKnowledgePageChange(page) {
      this.knowledgeCurrentPage = page;
      // 滚动到列表顶部
      this.$nextTick(() => {
        const knowledgeSection = document.querySelector('.content-section');
        if (knowledgeSection) {
          knowledgeSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    }
  },
  computed: {
    // 分页显示的知识列表
    displayedKnowledgeList() {
      const start = (this.knowledgeCurrentPage - 1) * this.knowledgePageSize;
      const end = start + this.knowledgePageSize;
      return this.knowledgeList.slice(start, end);
    }
  }
};
</script>

<style lang="less" scoped>
.expert-knowledge-list-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40px;
}

// 页面标题
.page-header {
  background: linear-gradient(135deg, #67C23A 0%, #035D1C 100%);
  padding: 40px 0;
  margin-bottom: 30px;

  .header-content {
    width: 1100px;
    margin: 0 auto;
    text-align: center;

    .page-title {
      font-size: 32px;
      font-weight: bold;
      color: white;
      margin: 0 0 10px 0;
    }

    .page-desc {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.9);
      margin: 0;
    }
  }
}

// 知识列表容器
.knowledge-list-wrapper {
  width: 1100px;
  margin: 0 auto;
}

// 内容区块
.content-section {
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

// 简化的知识列表样式
.knowledge-list-simple {
  .knowledge-item-simple {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    transition: all 0.2s;

    &:hover {
      background-color: #f9f9f9;
      padding-left: 10px;
      padding-right: 10px;
      border-radius: 4px;
    }

    &:last-child {
      border-bottom: none;
    }

    .knowledge-title-link {
      flex: 1;
      font-size: 16px;
      font-weight: 500;
      color: #333;
      text-decoration: none;
      cursor: pointer;
      line-height: 1.6;
      transition: color 0.2s;

      &:hover {
        color: #67C23A;
        text-decoration: underline;
      }
    }

    .knowledge-date {
      font-size: 14px;
      color: #999;
      margin-left: 20px;
      white-space: nowrap;
    }
  }
}

// 空状态
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  p {
    font-size: 16px;
    margin: 0;
  }
}

// 知识分页样式
.knowledge-pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 20px 0;

  .el-pagination {
    /deep/ .el-pager li {
      min-width: 32px;
      height: 32px;
      line-height: 32px;
    }

    /deep/ .btn-prev,
    /deep/ .btn-next {
      min-width: 32px;
      height: 32px;
      line-height: 32px;
    }
  }
}
</style>