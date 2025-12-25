<template>
  <div class="published-needs-container">
    <div class="page-header">
      <h2><i class="el-icon-document"></i> 我发布的需求</h2>
      <el-button type="primary" icon="el-icon-plus" @click="goToPublish">发布新需求</el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索需求标题或描述..."
        clearable
        style="width: 300px;"
        @keyup.enter.native="loadDemands"
      >
        <el-button slot="append" icon="el-icon-search" @click="loadDemands"></el-button>
      </el-input>
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 150px; margin-left: 10px;" @change="loadDemands">
        <el-option label="全部" value=""></el-option>
        <el-option label="进行中" value="ACTIVE"></el-option>
        <el-option label="已完成" value="COMPLETED"></el-option>
        <el-option label="已关闭" value="CLOSED"></el-option>
      </el-select>
    </div>

    <!-- 需求列表 -->
    <div class="needs-list" v-loading="loading">
      <div v-if="demands.length === 0 && !loading" class="empty-state">
        <i class="el-icon-box"></i>
        <p>暂无发布的需求</p>
        <el-button type="primary" @click="goToPublish">立即发布</el-button>
      </div>

      <div
        v-for="demand in demands"
        :key="demand.id"
        class="need-card"
      >
        <div class="card-header">
          <div class="need-title">{{ demand.title }}</div>
          <div class="need-actions">
            <el-tag :type="getStatusTagType(demand.status)" size="small">{{ getStatusLabel(demand.status) }}</el-tag>
            <el-button type="text" icon="el-icon-edit" @click="editDemand(demand)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" style="color: #f56c6c;" @click="deleteDemand(demand)">删除</el-button>
          </div>
        </div>

        <div class="card-content">
          <div class="need-description">{{ demand.description || '暂无描述' }}</div>
          <div class="need-meta">
            <span><i class="el-icon-s-goods"></i> 分类：{{ demand.category || '未分类' }}</span>
            <span v-if="demand.quantity"><i class="el-icon-s-data"></i> 数量：{{ demand.quantity }}{{ demand.unit || '件' }}</span>
            <span v-if="demand.contactInfo"><i class="el-icon-phone"></i> 联系方式：{{ demand.contactInfo }}</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="footer-info">
            <span><i class="el-icon-time"></i> 发布时间：{{ formatDate(demand.createTime) }}</span>
            <span v-if="demand.expireTime"><i class="el-icon-calendar"></i> 截止时间：{{ formatDate(demand.expireTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getDemands, deleteDemand as deleteDemandAPI } from "../api/trade";

export default {
  name: "PublishedNeeds",
  data() {
    return {
      loading: false,
      demands: [],
      searchKeyword: "",
      statusFilter: ""
    };
  },
  mounted() {
    this.$store.commit("updatePublishActiveIndex", "2-2");
    this.loadDemands();
  },
  methods: {
    loadDemands() {
      const userId = this.$store.state.loginUserId;
      if (!userId) {
        this.$message.warning("请先登录");
        this.$router.push("/login").catch((err) => err);
        return;
      }

      this.loading = true;
      const params = {
        userId: userId
      };
      if (this.searchKeyword) {
        params.keys = this.searchKeyword;
      }

      getDemands(params)
        .then((res) => {
          this.loading = false;
          if (res.flag === true && res.data) {
            let demandsList = Array.isArray(res.data) ? res.data : (res.data.list || []);
            
            // 如果设置了状态筛选，进行过滤
            if (this.statusFilter) {
              demandsList = demandsList.filter(d => d.status === this.statusFilter);
            }

            this.demands = demandsList;
          } else {
            this.demands = [];
            if (res.message) {
              this.$message.warning(res.message);
            }
          }
        })
        .catch((err) => {
          this.loading = false;
          this.demands = [];
          this.$message.error("加载需求列表失败");
          console.error("加载需求列表失败", err);
        });
    },
    editDemand(demand) {
      // 跳转到编辑页面，可以复用发布页面
      this.$router.push({
        path: "/home/publishNeed",
        query: { id: demand.id }
      }).catch((err) => err);
    },
    deleteDemand(demand) {
      this.$confirm(`确定要删除需求"${demand.title}"吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          deleteDemandAPI(demand.id)
            .then((res) => {
              if (res.flag === true) {
                this.$message.success(res.message || "删除成功");
                this.loadDemands();
              } else {
                this.$message.error(res.message || "删除失败");
              }
            })
            .catch((err) => {
              console.error("删除需求失败", err);
              let errorMsg = "删除失败";
              
              // 处理不同的错误格式
              if (err && err.message) {
                errorMsg = err.message;
              } else if (err && err.response && err.response.data && err.response.data.message) {
                errorMsg = err.response.data.message;
              } else if (typeof err === 'string') {
                errorMsg = err;
              }
              
              this.$message.error(errorMsg);
            });
        })
        .catch(() => {});
    },
    goToPublish() {
      this.$router.push("/home/publishNeed").catch((err) => err);
    },
    getStatusLabel(status) {
      const map = {
        ACTIVE: "进行中",
        COMPLETED: "已完成",
        CLOSED: "已关闭"
      };
      return map[status] || status;
    },
    getStatusTagType(status) {
      const map = {
        ACTIVE: "success",
        COMPLETED: "info",
        CLOSED: "danger"
      };
      return map[status] || "info";
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      try {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (e) {
        return dateStr;
      }
    }
  }
};
</script>

<style lang="less" scoped>
.published-needs-container {
  width: 1100px;
  margin: 0 auto;
  padding: 30px;
  background: #fff;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;

    h2 {
      font-size: 24px;
      color: #333;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 10px;

      i {
        color: #409eff;
      }
    }
  }

  .filter-section {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }

  .needs-list {
    min-height: 400px;
  }

  .need-card {
    background: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 16px;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      border-color: #409eff;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .need-title {
        font-size: 18px;
        font-weight: bold;
        color: #303133;
        flex: 1;
      }

      .need-actions {
        display: flex;
        align-items: center;
        gap: 10px;
      }
    }

    .card-content {
      margin-bottom: 15px;

      .need-description {
        color: #606266;
        line-height: 1.6;
        margin-bottom: 12px;
        white-space: pre-wrap;
        word-break: break-word;
      }

      .need-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        font-size: 14px;
        color: #909399;

        span {
          display: flex;
          align-items: center;
          gap: 5px;

          i {
            color: #409eff;
          }
        }
      }
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 15px;
      border-top: 1px solid #f0f0f0;
      font-size: 13px;
      color: #909399;

      .footer-info {
        display: flex;
        gap: 20px;

        span {
          display: flex;
          align-items: center;
          gap: 5px;
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 80px 20px;
    color: #909399;

    i {
      font-size: 80px;
      margin-bottom: 20px;
      display: block;
      opacity: 0.5;
    }

    p {
      font-size: 16px;
      margin-bottom: 20px;
    }
  }
}
</style>
