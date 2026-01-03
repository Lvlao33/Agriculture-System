<template>
  <div class="my-knowledge-page">
    <div class="page-header">
      <h2>我的知识</h2>
      <p class="subtitle">管理您发布的所有知识内容</p>
    </div>

    <div class="toolbar">
      <el-button type="success" @click="publishKnowledgeClick">
        <i class="el-icon-plus"></i> 发布知识
      </el-button>
      <el-button 
        :type="isSelectMode ? 'danger' : 'warning'" 
        plain 
        @click="toggleSelectMode"
      >
        <i class="el-icon-check"></i> {{ isSelectMode ? '取消选择' : '选择' }}
      </el-button>
      <el-button 
        v-if="isSelectMode && selectedKnowledgeIds.length > 0"
        type="danger" 
        @click="handleBatchDelete"
        style="margin-left: 10px;"
      >
        <i class="el-icon-delete"></i> 删除 ({{ selectedKnowledgeIds.length }})
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
            @error="handleImageError"
          />
          <img v-else src="../assets/img/wutu.gif" alt="默认图片" />
        </div>
        <div class="knowledge-content">
          <h3 class="knowledge-title">{{ item.title || '无标题' }}</h3>
          <p class="knowledge-summary">{{ item.summary || item.content || '暂无摘要' }}</p>
          <div class="knowledge-meta">
            <span class="meta-item"><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
            <span class="meta-item"><i class="el-icon-time"></i> {{ formatDate(item.createTime) }}</span>
          </div>
        </div>
      </div>
      <div v-if="knowledgeList.length === 0 && !loading" class="empty-state">
        <p>暂无知识内容</p>
      </div>
    </div>
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
      selectedKnowledgeIds: []
    };
  },
  methods: {
    loadKnowledgeList() {
      this.loading = true;
      selectKnowledgeByUsername({})
        .then((res) => {
          // 修正点：后端 selectByUsername 现在的 data 直接是 List 数组
          if (res && res.flag && Array.isArray(res.data)) {
            this.knowledgeList = res.data;
          } else {
            this.knowledgeList = [];
          }
        })
        .catch((err) => {
          this.$message.error("加载知识列表失败");
          this.knowledgeList = [];
        })
        .finally(() => {
          this.loading = false;
        });
    },
    toggleSelectMode() {
      this.isSelectMode = !this.isSelectMode;
      this.selectedKnowledgeIds = [];
    },
    handleKnowledgeClick(knowledgeId) {
      if (this.isSelectMode) this.toggleKnowledgeSelection(knowledgeId);
      else this.$router.push(`/home/knowledgeDetail/${knowledgeId}`);
    },
    toggleKnowledgeSelection(id) {
      const idx = this.selectedKnowledgeIds.indexOf(id);
      if (idx > -1) this.selectedKnowledgeIds.splice(idx, 1);
      else this.selectedKnowledgeIds.push(id);
    },
    handleBatchDelete() {
        this.$confirm('确认删除选中的知识吗?', '提示', { type: 'warning' }).then(() => {
            const promises = this.selectedKnowledgeIds.map(id => deleteKnowledgeById({ knowledgeId: id }));
            Promise.all(promises).then(() => {
                this.$message.success('删除成功');
                this.selectedKnowledgeIds = [];
                this.isSelectMode = false;
                this.loadKnowledgeList();
            });
        }).catch(() => {});
    },
    publishKnowledgeClick() {
      this.$router.push("/home/addmessage/publishknowledges");
    },
    formatDate(str) {
      return str ? new Date(str).toLocaleDateString() : '';
    },
    handleImageError(e) { e.target.src = require("../assets/img/wutu.gif"); }
  },
  created() {
    this.loadKnowledgeList();
  }
};
</script>

<style lang="less" scoped>
.my-knowledge-page { width: 1100px; margin: 0 auto; padding: 20px; background: #fff; min-height: 600px; }
.page-header { margin-bottom: 30px; border-bottom: 1px solid #f0f0f0; padding-bottom: 10px; h2 { margin: 0; } .subtitle { color: #999; font-size: 14px; } }
.toolbar { margin-bottom: 20px; }
.knowledge-list { display: flex; flex-wrap: wrap; gap: 20px; }
.knowledge-card { width: calc(50% - 10px); border: 1px solid #eee; padding: 15px; display: flex; cursor: pointer; position: relative;
    &:hover { border-color: #67C23A; box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
    &.selected { background: #f0f9ff; border-color: #67C23A; }
    .knowledge-image { width: 160px; height: 100px; margin-right: 15px; img { width: 100%; height: 100%; object-fit: cover; } }
    .knowledge-content { flex: 1; .knowledge-title { margin: 0 0 10px; font-size: 16px; } .knowledge-meta { color: #999; font-size: 12px; } }
}
.checkbox-wrapper { position: absolute; top: 10px; left: 10px; }
</style>