<template>
  <div class="home-knowledge">
    <el-backtop target=".home-content"></el-backtop>
    
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="search-section">
        <el-input 
          v-model="searchValue" 
          placeholder="搜索农业知识..."
          style="width: 350px;"
          clearable
          @keyup.enter.native="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
      
      <div class="filter-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'all' }"
          @click="switchTab('all')"
        >
          <i class="el-icon-menu"></i>
          <span>全部</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'text' }"
          @click="switchTab('text')"
        >
          <i class="el-icon-document"></i>
          <span>文字知识</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'image' }"
          @click="switchTab('image')"
        >
          <i class="el-icon-picture"></i>
          <span>图片知识</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'video' }"
          @click="switchTab('video')"
        >
          <i class="el-icon-video-camera"></i>
          <span>视频知识</span>
        </div>
      </div>
    </div>

    <!-- 知识列表 -->
    <knowledge :cknowledges="filteredKnowledges" @refresh="loadData"></knowledge>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="knowledgesCount"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        background
      />
    </div>

    <!-- 空状态 -->
    <div v-if="filteredKnowledges.length === 0 && !loading" class="empty-state">
      <i class="el-icon-document-delete"></i>
      <p>暂无知识内容</p>
    </div>
  </div>
</template>

<script>
import { selectKnowledgesPage } from "../api/knowledge";
import Knowledge from "../components/Knowledge.vue";
import Pagination from "../components/Pagination.vue";

export default {
  name: "HomeKnowledge",
  data() {
    return {
      knowledges: [],
      total: 0,
      pageSize: 10,
      url: "/knowledge/",
      knowledgesCount: sessionStorage.getItem("/knowledge/pageCode")
        ? parseInt(sessionStorage.getItem("/knowledge/pageCode"))
        : 1,
      searchValue: '',
      activeTab: 'all',
      loading: false
    };
  },
  computed: {
    filteredKnowledges() {
      let filtered = this.knowledges || [];
      
      // 按类型筛选
      if (this.activeTab !== 'all') {
        filtered = filtered.filter(item => {
          if (this.activeTab === 'text') {
            return !item.type || (item.type !== 'mp4' && item.type !== 'MP4' && !item.picPath);
          } else if (this.activeTab === 'image') {
            return item.type && item.type !== 'mp4' && item.type !== 'MP4' && item.picPath;
          } else if (this.activeTab === 'video') {
            return item.type === 'mp4' || item.type === 'MP4';
          }
          return true;
        });
      }
      
      // 按搜索关键词筛选
      if (this.searchValue) {
        const keyword = this.searchValue.toLowerCase();
        filtered = filtered.filter(item => {
          return (item.title && item.title.toLowerCase().includes(keyword)) ||
                 (item.content && item.content.toLowerCase().includes(keyword));
        });
      }
      
      return filtered;
    }
  },
  created() {
    this.$store.commit("updateActiveIndex", "4");
    this.loadData();
  },
  methods: {
    loadData() {
      this.loading = true;
      selectKnowledgesPage({
        pageNum: this.knowledgesCount,
      }).then((res) => {
        this.loading = false;
        if (res.flag == true) {
          let tmp = res.data.list || [];
          tmp.forEach(e => {
            if (e.picPath) {
              const fileArr = e.picPath.split('.');
              e.type = fileArr[fileArr.length - 1];
            } else {
              e.type = 'text'; // 纯文字知识
            }
          });
          this.knowledges = tmp;
          this.total = res.data.total || 0;
        }
      }).catch((err) => {
        this.loading = false;
        console.error('加载知识失败', err);
      });
    },
    handleSearch() {
      // 搜索功能已在 computed 中实现
    },
    switchTab(tab) {
      this.activeTab = tab;
    },
    handlePageChange(page) {
      this.knowledgesCount = page;
      sessionStorage.setItem("/knowledge/pageCode", page);
      this.loadData();
    },
    pageClick(item) {
      this.knowledges = item;
    }
  },
  components: { Knowledge, Pagination }
};
</script>

<style lang="less" scoped>
.home-knowledge {
  width: 1100px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background: #f5f5f5;

  .top-bar {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 15px;

    .search-section {
      flex: 1;
      min-width: 300px;
    }

    .filter-tabs {
      display: flex;
      gap: 10px;

      .tab-item {
        display: flex;
        align-items: center;
        padding: 8px 16px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s;
        color: #606266;
        font-size: 14px;
        border: 1px solid #e4e7ed;

        i {
          margin-right: 5px;
          font-size: 16px;
        }

        &:hover {
          color: #67c23a;
          border-color: #67c23a;
        }

        &.active {
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
          color: #fff;
          border-color: #67c23a;

          &:hover {
            background: linear-gradient(135deg, #5daf34 0%, #73c850 100%);
          }
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    padding: 20px 0;
    background: #fff;
    border-radius: 8px;
  }

  .empty-state {
    text-align: center;
    padding: 80px 20px;
    background: #fff;
    border-radius: 8px;
    color: #999;

    i {
      font-size: 64px;
      margin-bottom: 20px;
      display: block;
    }

    p {
      font-size: 16px;
    }
  }
}
</style>
