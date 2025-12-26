<template>
  <div class="my-knowledge-page">
    <div class="page-header">
      <h2>我的知识</h2>
      <p class="subtitle">管理您发布的所有知识内容</p>
    </div>

    <div class="toolbar">
      <el-button type="success" @click="publishKnowledgeClick">
        <i class="el-icon-plus"></i>
        发布知识
      </el-button>
      <el-button 
        :type="isSelectMode ? 'danger' : 'warning'" 
        plain 
        @click="toggleSelectMode"
      >
        <i class="el-icon-check"></i>
        {{ isSelectMode ? '取消选择' : '选择' }}
      </el-button>
      <el-button 
        v-if="isSelectMode && selectedKnowledgeIds.length > 0"
        type="danger" 
        @click="handleBatchDelete"
        style="margin-left: 10px;"
      >
        <i class="el-icon-delete"></i>
        删除 ({{ selectedKnowledgeIds.length }})
      </el-button>
    </div>

    <div class="knowledge-list" v-loading="loading">
      <div 
        class="knowledge-card" 
        v-for="(item, index) in knowledgeList" 
        :key="item.knowledgeId || index"
        :class="{ 'selected': isSelectMode && selectedKnowledgeIds.includes(item.knowledgeId) }"
        @click="handleKnowledgeClick(item.knowledgeId)"
      >
        <div v-if="isSelectMode" class="checkbox-wrapper">
          <el-checkbox 
            :value="selectedKnowledgeIds.includes(item.knowledgeId)"
            @change="toggleKnowledgeSelection(item.knowledgeId)"
            @click.stop
          ></el-checkbox>
        </div>
        <div class="knowledge-image">
          <img 
            v-if="item.picPath" 
            :src="$store.state.imgShowRoad + '/file/' + item.picPath" 
            alt="知识图片"
            @error="handleImageError"
          />
          <img 
            v-else 
            src="../assets/img/wutu.gif"
            alt="默认图片"
          />
        </div>
        <div class="knowledge-content">
          <h3 class="knowledge-title">{{ item.title || '无标题' }}</h3>
          <p class="knowledge-summary">{{ item.summary || item.content || '暂无摘要' }}</p>
          <div class="knowledge-meta">
            <span class="meta-item">
              <i class="el-icon-view"></i>
              阅读 {{ item.viewCount || 0 }}
            </span>
            <span class="meta-item">
              <i class="el-icon-star-on"></i>
              点赞 {{ item.likeCount || 0 }}
            </span>
            <span class="meta-item">
              <i class="el-icon-time"></i>
              {{ formatDate(item.createTime) }}
            </span>
          </div>
          <div class="knowledge-tags" v-if="item.tags && item.tags.length > 0">
            <el-tag 
              v-for="tag in item.tags" 
              :key="tag" 
              size="small" 
              style="margin-right: 5px;"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <div v-if="knowledgeList.length === 0 && !loading" class="empty-state">
        <i class="el-icon-document"></i>
        <p>暂无知识内容</p>
        <el-button type="primary" @click="publishKnowledgeClick">发布第一条知识</el-button>
      </div>
    </div>

    <!-- 确认删除弹窗 -->
    <el-dialog
      title="确认删除"
      :visible.sync="deleteDialogVisible"
      width="400px"
    >
      <div class="delete-dialog-content">
        <i class="el-icon-warning" style="color: #E6A23C; font-size: 24px; margin-right: 10px;"></i>
        <p>确定要删除选中的 <strong style="color: #F56C6C;">{{ selectedKnowledgeIds.length }}</strong> 条知识吗？</p>
        <p style="color: #909399; font-size: 12px; margin-top: 10px;">删除后无法恢复，这些知识将从农业知识页面和我的知识页面中移除。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取 消</el-button>
        <el-button type="danger" :loading="deleting" @click="confirmDelete">确认删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { selectKnowledgeByUsername, deleteKnowledgeById } from "../api/knowledge";

export default {
  name: "MyKnowledge",
  data() {
    return {
      knowledgeList: [],
      loading: false,
      isSelectMode: false,
      selectedKnowledgeIds: [],
      deleteDialogVisible: false,
      deleting: false,
    };
  },
  methods: {
    // 加载知识列表
    loadKnowledgeList() {
      this.loading = true;
      selectKnowledgeByUsername({})
        .then((res) => {
          if (res && res.flag && res.data) {
            // 后端返回格式：{flag: true, data: [...]}
            this.knowledgeList = Array.isArray(res.data) ? res.data : [];
          } else {
            this.knowledgeList = [];
            if (res && res.message) {
              console.warn("加载知识列表警告:", res.message);
            }
          }
        })
        .catch((err) => {
          console.error("加载知识列表失败:", err);
          this.$message.error("加载知识列表失败，请重试");
          this.knowledgeList = [];
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 切换选择模式
    toggleSelectMode() {
      this.isSelectMode = !this.isSelectMode;
      if (!this.isSelectMode) {
        this.selectedKnowledgeIds = [];
      }
    },
    // 点击知识卡片
    handleKnowledgeClick(knowledgeId) {
      if (this.isSelectMode) {
        this.toggleKnowledgeSelection(knowledgeId);
      }
    },
    // 切换知识选择状态
    toggleKnowledgeSelection(knowledgeId) {
      const index = this.selectedKnowledgeIds.indexOf(knowledgeId);
      if (index > -1) {
        this.selectedKnowledgeIds.splice(index, 1);
      } else {
        this.selectedKnowledgeIds.push(knowledgeId);
      }
    },
    // 批量删除
    handleBatchDelete() {
      if (this.selectedKnowledgeIds.length === 0) {
        this.$message.warning("请先选择要删除的知识");
        return;
      }
      this.deleteDialogVisible = true;
    },
    // 确认删除
    confirmDelete() {
      this.deleting = true;
      const deletePromises = this.selectedKnowledgeIds.map(id => 
        deleteKnowledgeById({ knowledgeId: id })
      );

      Promise.all(deletePromises)
        .then((results) => {
          const successCount = results.filter(r => r && r.flag).length;
          if (successCount === this.selectedKnowledgeIds.length) {
            this.$message.success(`成功删除 ${successCount} 条知识`);
          } else {
            this.$message.warning(`部分删除失败，成功删除 ${successCount} 条`);
          }
          // 清空选择并退出选择模式
          this.selectedKnowledgeIds = [];
          this.isSelectMode = false;
          this.deleteDialogVisible = false;
          // 重新加载知识列表
          this.loadKnowledgeList();
        })
        .catch((err) => {
          console.error("批量删除失败:", err);
          this.$message.error("删除失败，请重试");
        })
        .finally(() => {
          this.deleting = false;
        });
    },
    // 发布知识
    publishKnowledgeClick() {
      this.$router.push("/home/addmessage/publishknowledges").catch((err) => err);
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return "未知时间";
      try {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      } catch (e) {
        return dateStr;
      }
    },
    // 图片加载错误处理
    handleImageError(event) {
      event.target.src = require("../assets/img/wutu.gif");
    },
  },
  created() {
    this.$store.commit("updateUserActiveIndex", "4-1");
    this.loadKnowledgeList();
  },
};
</script>

<style lang="less" scoped>
.my-knowledge-page {
  width: 1100px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  min-height: 600px;

  .page-header {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;

    h2 {
      margin: 0 0 10px 0;
      font-size: 24px;
      color: #303133;
    }

    .subtitle {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .toolbar {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .knowledge-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }

  .knowledge-card {
    width: calc(50% - 10px);
    min-width: 500px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    cursor: pointer;
    transition: all 0.3s;
    position: relative;
    background: #fff;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      border-color: #67C23A;
    }

    &.selected {
      border: 2px solid #67C23A;
      background-color: #f0f9ff;
      box-shadow: 0 2px 12px rgba(103, 194, 58, 0.2);
    }

    .checkbox-wrapper {
      position: absolute;
      top: 15px;
      left: 15px;
      z-index: 10;
      background: rgba(255, 255, 255, 0.95);
      padding: 5px;
      border-radius: 4px;
    }

    .knowledge-image {
      width: 200px;
      height: 150px;
      margin-right: 20px;
      flex-shrink: 0;
      border-radius: 6px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .knowledge-content {
      flex: 1;
      display: flex;
      flex-direction: column;

      .knowledge-title {
        margin: 0 0 10px 0;
        font-size: 18px;
        color: #303133;
        font-weight: 600;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        line-clamp: 1;
        -webkit-box-orient: vertical;
      }

      .knowledge-summary {
        flex: 1;
        margin: 0 0 15px 0;
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .knowledge-meta {
        display: flex;
        gap: 15px;
        margin-bottom: 10px;
        font-size: 12px;
        color: #909399;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 5px;

          i {
            font-size: 14px;
          }
        }
      }

      .knowledge-tags {
        margin-top: auto;
      }
    }
  }

  .empty-state {
    width: 100%;
    text-align: center;
    padding: 80px 0;
    color: #909399;

    i {
      font-size: 64px;
      margin-bottom: 20px;
      display: block;
      color: #c0c4cc;
    }

    p {
      margin: 0 0 20px 0;
      font-size: 16px;
    }
  }

  .delete-dialog-content {
    display: flex;
    align-items: flex-start;
    padding: 10px 0;

    p {
      margin: 0;
      line-height: 1.6;
    }
  }
}

@media (max-width: 1200px) {
  .my-knowledge-page {
    width: 100%;
    padding: 15px;

    .knowledge-card {
      width: 100%;
      min-width: auto;
    }
  }
}
</style>

