<template>
  <div class="loan-review-container">
    <div class="page-header">
      <h1>贷款审批管理</h1>
      <p class="page-desc">管理贷款申请，进行审核、签约、放款等操作</p>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="待分配" name="CREATED">
        <template slot="label">
          <span>待分配 <el-badge :value="createdLoans.length" class="tab-badge" v-if="createdLoans.length > 0"></el-badge></span>
        </template>
        <loan-list
          :loans="createdLoans"
          :loading="loading"
          status="CREATED"
          @view-detail="handleViewDetail"
          @assign="handleAssign"
        />
      </el-tab-pane>

      <el-tab-pane label="审核中" name="REVIEWING">
        <template slot="label">
          <span>审核中 <el-badge :value="reviewingLoans.length" class="tab-badge" v-if="reviewingLoans.length > 0"></el-badge></span>
        </template>
        <loan-list
          :loans="reviewingLoans"
          :loading="loading"
          status="REVIEWING"
          @view-detail="handleViewDetail"
          @approve="handleApprove"
          @reject="handleReject"
          @comment="handleComment"
          @contact="handleContact"
        />
      </el-tab-pane>

      <el-tab-pane label="待签约" name="APPROVED">
        <template slot="label">
          <span>待签约 <el-badge :value="approvedLoans.length" class="tab-badge" v-if="approvedLoans.length > 0"></el-badge></span>
        </template>
        <loan-list
          :loans="approvedLoans"
          :loading="loading"
          status="APPROVED"
          @view-detail="handleViewDetail"
          @sign="handleSign"
          @contact="handleContact"
        />
      </el-tab-pane>

      <el-tab-pane label="待放款" name="SIGNED">
        <template slot="label">
          <span>待放款 <el-badge :value="signedLoans.length" class="tab-badge" v-if="signedLoans.length > 0"></el-badge></span>
        </template>
        <loan-list
          :loans="signedLoans"
          :loading="loading"
          status="SIGNED"
          @view-detail="handleViewDetail"
          @disburse="handleDisburse"
          @contact="handleContact"
        />
      </el-tab-pane>

      <el-tab-pane label="还款中" name="REPAYING">
        <template slot="label">
          <span>还款中 <el-badge :value="repayingLoans.length" class="tab-badge" v-if="repayingLoans.length > 0"></el-badge></span>
        </template>
        <loan-list
          :loans="repayingLoans"
          :loading="loading"
          status="REPAYING"
          @view-detail="handleViewDetail"
          @clear="handleClear"
          @contact="handleContact"
        />
      </el-tab-pane>

      <el-tab-pane label="已结束" name="CLOSED">
        <template slot="label">
          <span>已结束</span>
        </template>
        <loan-list
          :loans="closedLoans"
          :loading="loading"
          status="CLOSED"
          @view-detail="handleViewDetail"
          @contact="handleContact"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 贷款详情对话框 -->
    <loan-detail-dialog
      :visible="detailDialogVisible"
      :loan="selectedLoan"
      :status="activeTab"
      @close="detailDialogVisible = false"
      @assign="handleAssign"
      @approve="handleApprove"
      @reject="handleReject"
      @comment="handleComment"
      @sign="handleSign"
      @disburse="handleDisburse"
      @clear="handleClear"
      @contact="handleContact"
      @download-file="handleDownloadFile"
    />
  </div>
</template>

<script>
import {
  getCreatedLoans,
  getLoansByStatus,
  getLoansByStatuses,
  assignLoan,
  approveLoan,
  rejectLoan,
  commentLoan,
  signLoan,
  disburseLoan,
  clearLoan
} from "../api/bank";
import { getUserId } from "../utils/tokenManager";
import LoanList from "../components/LoanReviewList/LoanList.vue";
import LoanDetailDialog from "../components/LoanReviewList/LoanDetailDialog.vue";

export default {
  name: "LoanReviewList",
  components: {
    LoanList,
    LoanDetailDialog
  },
  data() {
    return {
      activeTab: "CREATED",
      loading: false,
      createdLoans: [],
      reviewingLoans: [],
      approvedLoans: [],
      signedLoans: [],
      repayingLoans: [],
      closedLoans: [],
      detailDialogVisible: false,
      selectedLoan: null,
      currentUserId: null
    };
  },
  mounted() {
    this.currentUserId = getUserId();
    if (!this.currentUserId) {
      this.$message.error("未获取到用户ID，请重新登录");
      this.$router.push("/login");
      return;
    }
    this.loadAllLoans();
  },
  methods: {
    async loadAllLoans() {
      this.loading = true;
      try {
        // 加载CREATED状态的贷款（未分配）
        const createdRes = await getCreatedLoans();
        if (createdRes && Array.isArray(createdRes)) {
          this.createdLoans = createdRes;
        }

        // 加载当前用户负责的其他状态的贷款
        const statuses = ["REVIEWING", "APPROVED", "SIGNED", "REPAYING"];
        for (const status of statuses) {
          try {
            const res = await getLoansByStatus(status, this.currentUserId);
            if (res && Array.isArray(res)) {
              this[`${status.toLowerCase()}Loans`] = res;
            }
          } catch (err) {
            console.error(`加载${status}状态贷款失败:`, err);
            this[`${status.toLowerCase()}Loans`] = [];
          }
        }

        // 加载已结束的贷款（REJECTED, CLEARED_NORMAL, CLEARED_EARLY）
        try {
          const closedStatuses = ["REJECTED", "CLEARED_NORMAL", "CLEARED_EARLY"];
          const res = await getLoansByStatuses(closedStatuses, this.currentUserId);
          if (res && Array.isArray(res)) {
            this.closedLoans = res;
          }
        } catch (err) {
          console.error("加载已结束贷款失败:", err);
          this.closedLoans = [];
        }
      } catch (err) {
        console.error("加载贷款列表失败:", err);
        this.$message.error("加载贷款列表失败");
      } finally {
        this.loading = false;
      }
    },
    handleTabClick(tab) {
      // 切换标签时不需要重新加载，数据已经在mounted时加载
    },
    handleViewDetail(loan) {
      this.selectedLoan = loan;
      this.detailDialogVisible = true;
    },
    async handleAssign(loan) {
      try {
        const res = await assignLoan(loan.id, this.currentUserId);
        if (res && res.flag) {
          this.$message.success("分配成功");
          // 重新加载数据
          await this.loadAllLoans();
          this.detailDialogVisible = false;
        } else {
          this.$message.error(res?.message || "分配失败");
        }
      } catch (err) {
        console.error("分配失败:", err);
        this.$message.error("分配失败");
      }
    },
    async handleApprove(loan, remark) {
      try {
        const res = await approveLoan(loan.id, this.currentUserId, remark);
        if (res && res.flag) {
          this.$message.success("审核通过");
          await this.loadAllLoans();
          this.detailDialogVisible = false;
        } else {
          this.$message.error(res?.message || "操作失败");
        }
      } catch (err) {
        console.error("审核失败:", err);
        this.$message.error("审核失败");
      }
    },
    async handleReject(loan, remark) {
      try {
        const res = await rejectLoan(loan.id, this.currentUserId, remark);
        if (res && res.flag) {
          this.$message.success("已拒绝");
          await this.loadAllLoans();
          this.detailDialogVisible = false;
        } else {
          this.$message.error(res?.message || "操作失败");
        }
      } catch (err) {
        console.error("拒绝失败:", err);
        this.$message.error("拒绝失败");
      }
    },
    async handleComment(loan, remark) {
      if (!remark || !remark.trim()) {
        this.$message.warning("请输入批注内容");
        return;
      }
      try {
        const res = await commentLoan(loan.id, this.currentUserId, remark);
        if (res && res.flag) {
          this.$message.success("批注成功");
          // 批注不改变状态，只需要刷新详情
          if (this.detailDialogVisible) {
            // 重新加载当前标签的数据以更新备注
            await this.loadAllLoans();
          }
        } else {
          this.$message.error(res?.message || "批注失败");
        }
      } catch (err) {
        console.error("批注失败:", err);
        this.$message.error("批注失败");
      }
    },
    async handleSign(loan, remark) {
      try {
        const res = await signLoan(loan.id, this.currentUserId, remark);
        if (res && res.flag) {
          this.$message.success("签约成功");
          await this.loadAllLoans();
          this.detailDialogVisible = false;
        } else {
          this.$message.error(res?.message || "操作失败");
        }
      } catch (err) {
        console.error("签约失败:", err);
        this.$message.error("签约失败");
      }
    },
    async handleDisburse(loan, remark) {
      try {
        const res = await disburseLoan(loan.id, this.currentUserId, remark);
        if (res && res.flag) {
          this.$message.success("放款成功");
          await this.loadAllLoans();
          this.detailDialogVisible = false;
        } else {
          this.$message.error(res?.message || "操作失败");
        }
      } catch (err) {
        console.error("放款失败:", err);
        this.$message.error("放款失败");
      }
    },
    async handleClear(loan, remark) {
      try {
        const res = await clearLoan(loan.id, this.currentUserId, remark);
        if (res && res.flag) {
          this.$message.success("还清成功");
          await this.loadAllLoans();
          this.detailDialogVisible = false;
        } else {
          this.$message.error(res?.message || "操作失败");
        }
      } catch (err) {
        console.error("还清失败:", err);
        this.$message.error("还清失败");
      }
    },
    handleContact(loan) {
      // 跳转到聊天页面或显示联系方式
      if (loan.applicantPhone) {
        this.$message.info(`联系电话: ${loan.applicantPhone}`);
      } else {
        this.$message.warning("未找到联系方式");
      }
    },
    handleDownloadFile(file) {
      // 下载文件
      if (file.filePath) {
        // 构建下载URL
        const baseUrl = process.env.NODE_ENV === 'production' 
          ? 'http://119.3.180.117:9090' 
          : 'http://localhost:8080';
        
        // filePath是绝对路径，需要转换为相对路径
        // 假设filePath格式为: /path/to/project/loans_files/1/fileType/filename.ext
        // 或者: C:\path\to\project\loans_files\1\fileType\filename.ext
        let downloadPath = file.filePath;
        
        // 如果路径包含loans_files，提取后面的部分
        const loansFilesIndex = downloadPath.indexOf('loans_files');
        if (loansFilesIndex !== -1) {
          downloadPath = downloadPath.substring(loansFilesIndex);
          // 将Windows路径分隔符转换为URL路径分隔符
          downloadPath = downloadPath.replace(/\\/g, '/');
        } else {
          this.$message.error("文件路径格式不正确");
          return;
        }
        
        // 构建完整的下载URL
        const downloadUrl = `${baseUrl}/${downloadPath}`;
        
        // 创建临时链接下载
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = file.fileName || 'download';
        link.target = '_blank';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } else {
        this.$message.error("文件路径不存在");
      }
    }
  }
};
</script>

<style lang="less" scoped>
.loan-review-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px 20px 60px;

  .page-header {
    margin-bottom: 24px;

    h1 {
      margin: 0 0 8px 0;
      font-size: 28px;
      color: #1f2937;
    }

    .page-desc {
      margin: 0;
      font-size: 14px;
      color: #6b7280;
    }
  }

  .tab-badge {
    margin-left: 8px;
  }

  /deep/ .el-tabs__header {
    margin-bottom: 20px;
  }

  /deep/ .el-tabs__item {
    font-size: 15px;
    padding: 0 20px;
  }
}
</style>

