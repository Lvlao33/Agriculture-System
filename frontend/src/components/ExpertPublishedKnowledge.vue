<template>
  <div class="published-message">
    <div class="toolbar">
      <el-button type="success" @click="publishKnowledgeClick">发布知识</el-button>
      <el-button 
        :type="isSelectMode ? 'danger' : 'warning'" 
        plain 
        @click="toggleSelectMode"
      >
        {{ isSelectMode ? '取消选择' : '选择' }}
      </el-button>
      <el-button 
        v-if="isSelectMode && selectedKnowledgeIds.length > 0"
        type="danger" 
        @click="handleBatchDelete"
        style="margin-left: 10px;"
      >
        删除 ({{ selectedKnowledgeIds.length }})
      </el-button>
    </div>
    <div class="publish-content">
      <div 
        class="message" 
        v-for="(item, index) in userKnowledges" 
        :key="index" 
        :class="{ 'selected': isSelectMode && selectedKnowledgeIds.includes(item.knowledgeId) }"
        :style="(index+1)%2===0?'margin-right:0':'margin-right:20px'"
        @click="handleKnowledgeClick(item.knowledgeId)"
      >
        <div v-if="isSelectMode" class="checkbox-wrapper">
          <el-checkbox 
            :value="selectedKnowledgeIds.includes(item.knowledgeId)"
            @change="toggleKnowledgeSelection(item.knowledgeId)"
            @click.stop
          ></el-checkbox>
        </div>
        <img v-if="item.picPath" class="knowleage-icon" :src="$store.state.imgShowRoad + '/file/' + item.picPath" alt="" />
        <img class="knowleage-icon" v-else src="../assets/img/wutu.gif">
        <div class="info">
          <h4 class="title">{{ item.title }}</h4>
          <span class="initiator">发起人：{{ item.ownName }}</span>
          <p class="content">{{ item.content }}</p>
          <div class="btns-style" v-if="!isSelectMode">
            <div @click.stop.once="changeKnowledgeInfo(item.knowledgeId)">
              <change-knowledge :cupdateKnowledgeInfo="updateInfo"></change-knowledge>
            </div>
            <delete-knowledge :knowledge-id="item.knowledgeId" @deleted="loadUserKnowledges" />
          </div>
        </div>
      </div>
      <div v-if="userKnowledges.length === 0" class="empty-state">
        <p>暂无知识内容</p>
      </div>
    </div>

    <!-- 确认删除弹窗 -->
    <el-dialog
      title="确认删除"
      :visible.sync="deleteDialogVisible"
      width="400px"
    >
      <p>确定要删除选中的 {{ selectedKnowledgeIds.length }} 条知识吗？删除后无法恢复。</p>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取 消</el-button>
        <el-button type="danger" :loading="deleting" @click="confirmDelete">确认删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  selectKnowledgeById,
  selectKnowledgeByUsername,
  deleteKnowledgeById,
} from "../api/knowledge";
import ChangeKnowledge from "./ChangeKnowledge.vue";
import DeleteKnowledge from "./DeleteKnowledge.vue";

export default {
  data() {
    return {
      userKnowledges: [],
      updateInfo: {},
      isSelectMode: false,
      selectedKnowledgeIds: [],
      deleteDialogVisible: false,
      deleting: false,
    };
  },
  components: { DeleteKnowledge, ChangeKnowledge },
  methods: {
    publishKnowledgeClick() {
      this.$router
        .push("/home/addmessage/publishknowledges")
        .catch((err) => err);
    },
    toggleSelectMode() {
      this.isSelectMode = !this.isSelectMode;
      if (!this.isSelectMode) {
        // 退出选择模式时清空选择
        this.selectedKnowledgeIds = [];
      }
    },
    handleKnowledgeClick(knowledgeId) {
      if (this.isSelectMode) {
        this.toggleKnowledgeSelection(knowledgeId);
      }
    },
    toggleKnowledgeSelection(knowledgeId) {
      const index = this.selectedKnowledgeIds.indexOf(knowledgeId);
      if (index > -1) {
        this.selectedKnowledgeIds.splice(index, 1);
      } else {
        this.selectedKnowledgeIds.push(knowledgeId);
      }
    },
    handleBatchDelete() {
      if (this.selectedKnowledgeIds.length === 0) {
        this.$message.warning("请先选择要删除的知识");
        return;
      }
      this.deleteDialogVisible = true;
    },
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
          this.loadUserKnowledges();
        })
        .catch((err) => {
          console.error("批量删除失败:", err);
          this.$message.error("删除失败，请重试");
        })
        .finally(() => {
          this.deleting = false;
        });
    },
    loadUserKnowledges() {
      selectKnowledgeByUsername({})
        .then((res) => {
          this.userKnowledges = res.data || [];
        })
        .catch((err) => {
          console.log(err);
          this.userKnowledges = [];
        });
    },
    changeKnowledgeInfo(item) {
      this.$store.commit("updateChangedKnowledgeId", item);
      selectKnowledgeById({
        knowledgeId: this.$store.state.changedKnowledgeId,
      })
        .then((res) => {
          this.updateInfo = res.data;
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
  created() {
    this.$store.commit("updateUserActiveIndex", "4-3");
    this.loadUserKnowledges();
  },
};
</script>

<style lang="less" scoped>
.published-message {
  margin: 0 auto;
  width: 900px;
  height: 100%;
  background: #fff;
  .toolbar {
    padding: 20px;
    border-bottom: 1px solid #f2f2f2;
    display: flex;
    align-items: center;
  }
  .publish-content {
    background: #fff;
    display: flex;
    flex-wrap: wrap;
    padding: 10px 20px;
    min-height: 400px;
  }
  .message {
    width: 420px;
    height: 180px;
    border: 1px solid #f2f2f2;
    background-color: white;
    margin: 10px 0 10px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    position: relative;
    cursor: pointer;
    transition: all 0.3s;
    
    &.selected {
      border: 2px solid #67C23A;
      background-color: #f0f9ff;
    }
    
    &:hover {
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .checkbox-wrapper {
      position: absolute;
      top: 10px;
      left: 10px;
      z-index: 10;
      background: rgba(255, 255, 255, 0.9);
      padding: 5px;
      border-radius: 4px;
    }
    
    img {
      border-radius: 6px;
    }
    .knowleage-icon {
      width: 200px;
      height: 178px;
      margin-right: 10px;
      border-radius: 6px;
    }
    .info {
      width: 200px;
      padding: 0 10px;
      .initiator {
        color: #666;
      }
      .title {
        height: 30px;
        line-height: 30px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        line-clamp: 1;
        -webkit-box-orient: vertical;
        word-break: break-all;
      }
      .content {
        height: 75px;
        line-height: 25px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        line-clamp: 3;
        -webkit-box-orient: vertical;
        word-break: break-all;
      }
    }
    .btns-style {
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
    }
  }
  .empty-state {
    width: 100%;
    text-align: center;
    padding: 60px 0;
    color: #999;
    font-size: 16px;
  }
}
</style>
