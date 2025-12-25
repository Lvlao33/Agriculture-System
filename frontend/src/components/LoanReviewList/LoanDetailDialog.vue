<template>
  <el-dialog
    :title="`贷款详情 - ${loan ? loan.id : ''}`"
    :visible.sync="dialogVisible"
    width="900px"
    @close="handleClose"
  >
    <div v-if="loan" class="loan-detail">
      <!-- 基本信息 -->
      <el-card class="detail-section" shadow="never">
        <div slot="header">
          <span>基本信息</span>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="贷款ID">{{ loan.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(loan.status)">{{ getStatusText(loan.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ loan.applicantName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ loan.applicantPhone || "-" }}</el-descriptions-item>
          <el-descriptions-item label="贷款金额">
            ¥{{ formatAmount(loan.loanAmount) }}
          </el-descriptions-item>
          <el-descriptions-item label="贷款期限">{{ loan.loanTermMonths }} 个月</el-descriptions-item>
          <el-descriptions-item label="利率">{{ loan.interestRate ? loan.interestRate + "%" : "-" }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDate(loan.applicationDate) }}</el-descriptions-item>
          <el-descriptions-item label="贷款用途" :span="2">{{ loan.loanPurpose || "-" }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ loan.remark || "-" }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 文件资料 (仅REVIEWING状态显示) -->
      <el-card v-if="status === 'REVIEWING'" class="detail-section" shadow="never">
        <div slot="header">
          <span>文件资料</span>
        </div>
        <div v-if="files.length > 0" class="file-list">
          <div v-for="file in files" :key="file.id" class="file-item">
            <div class="file-info">
              <i class="el-icon-document"></i>
              <span class="file-name">{{ file.fileName }}</span>
              <span class="file-type">{{ file.fileType || "未分类" }}</span>
            </div>
            <el-button
              type="text"
              size="small"
              icon="el-icon-download"
              @click="handleDownload(file)"
            >
              下载
            </el-button>
          </div>
        </div>
        <div v-else class="empty-files">
          <p>暂无文件资料</p>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <!-- CREATED状态：审核按钮 -->
        <template v-if="status === 'CREATED'">
          <el-button type="primary" @click="handleAssign">审核</el-button>
        </template>

        <!-- REVIEWING状态：通过、拒绝、批注、联系客户 -->
        <template v-if="status === 'REVIEWING'">
          <el-button type="success" @click="showApproveDialog = true">通过</el-button>
          <el-button type="danger" @click="showRejectDialog = true">拒绝</el-button>
          <el-button type="warning" @click="showCommentDialog = true">批注</el-button>
          <el-button @click="handleContact">联系客户</el-button>
        </template>

        <!-- APPROVED状态：签约、联系客户 -->
        <template v-if="status === 'APPROVED'">
          <el-button type="primary" @click="showSignDialog = true">签约</el-button>
          <el-button @click="handleContact">联系客户</el-button>
        </template>

        <!-- SIGNED状态：放款、联系客户 -->
        <template v-if="status === 'SIGNED'">
          <el-button type="primary" @click="showDisburseDialog = true">放款</el-button>
          <el-button @click="handleContact">联系客户</el-button>
        </template>

        <!-- REPAYING状态：还清、联系客户 -->
        <template v-if="status === 'REPAYING'">
          <el-button type="success" @click="showClearDialog = true">还清</el-button>
          <el-button @click="handleContact">联系客户</el-button>
        </template>

        <!-- CLOSED状态（已结束）：只显示联系客户 -->
        <template v-if="status === 'CLOSED'">
          <el-button @click="handleContact">联系客户</el-button>
        </template>
      </div>
    </div>

    <!-- 审核通过对话框 -->
    <el-dialog
      title="审核通过"
      :visible.sync="showApproveDialog"
      width="500px"
      append-to-body
    >
      <el-input
        v-model="approveRemark"
        type="textarea"
        :rows="4"
        placeholder="请输入备注（可选）"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showApproveDialog = false">取消</el-button>
        <el-button type="success" @click="confirmApprove">确认</el-button>
      </div>
    </el-dialog>

    <!-- 审核拒绝对话框 -->
    <el-dialog
      title="审核拒绝"
      :visible.sync="showRejectDialog"
      width="500px"
      append-to-body
    >
      <el-input
        v-model="rejectRemark"
        type="textarea"
        :rows="4"
        placeholder="请输入拒绝原因"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认</el-button>
      </div>
    </el-dialog>

    <!-- 批注对话框 -->
    <el-dialog
      title="添加批注"
      :visible.sync="showCommentDialog"
      width="500px"
      append-to-body
    >
      <el-input
        v-model="commentRemark"
        type="textarea"
        :rows="4"
        placeholder="请输入批注内容"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showCommentDialog = false">取消</el-button>
        <el-button type="warning" @click="confirmComment">确认</el-button>
      </div>
    </el-dialog>

    <!-- 签约对话框 -->
    <el-dialog
      title="确认签约"
      :visible.sync="showSignDialog"
      width="500px"
      append-to-body
    >
      <el-input
        v-model="signRemark"
        type="textarea"
        :rows="4"
        placeholder="请输入备注（可选）"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showSignDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSign">确认</el-button>
      </div>
    </el-dialog>

    <!-- 放款对话框 -->
    <el-dialog
      title="确认放款"
      :visible.sync="showDisburseDialog"
      width="500px"
      append-to-body
    >
      <el-input
        v-model="disburseRemark"
        type="textarea"
        :rows="4"
        placeholder="请输入备注（可选）"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDisburseDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmDisburse">确认</el-button>
      </div>
    </el-dialog>

    <!-- 还清对话框 -->
    <el-dialog
      title="确认还清"
      :visible.sync="showClearDialog"
      width="500px"
      append-to-body
    >
      <el-input
        v-model="clearRemark"
        type="textarea"
        :rows="4"
        placeholder="请输入备注（可选）"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showClearDialog = false">取消</el-button>
        <el-button type="success" @click="confirmClear">确认</el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { getLoanFiles } from "../../api/loan";

export default {
  name: "LoanDetailDialog",
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    loan: {
      type: Object,
      default: null
    },
    status: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      dialogVisible: false,
      files: [],
      loadingFiles: false,
      showApproveDialog: false,
      showRejectDialog: false,
      showCommentDialog: false,
      showSignDialog: false,
      showDisburseDialog: false,
      showClearDialog: false,
      approveRemark: "",
      rejectRemark: "",
      commentRemark: "",
      signRemark: "",
      disburseRemark: "",
      clearRemark: ""
    };
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val && this.loan) {
        this.loadFiles();
      }
    },
    dialogVisible(val) {
      if (!val) {
        this.$emit("close");
        // 清空表单
        this.approveRemark = "";
        this.rejectRemark = "";
        this.commentRemark = "";
        this.signRemark = "";
        this.disburseRemark = "";
        this.clearRemark = "";
      }
    }
  },
  methods: {
    async loadFiles() {
      if (this.status !== "REVIEWING" || !this.loan) return;
      
      this.loadingFiles = true;
      try {
        const res = await getLoanFiles(this.loan.id);
        if (res && Array.isArray(res)) {
          this.files = res;
        }
      } catch (err) {
        console.error("加载文件失败:", err);
        this.$message.error("加载文件失败");
      } finally {
        this.loadingFiles = false;
      }
    },
    formatAmount(amount) {
      if (!amount) return "0.00";
      return Number(amount).toLocaleString("zh-CN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    },
    formatDate(dateStr) {
      if (!dateStr) return "-";
      const date = new Date(dateStr);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
      });
    },
    getStatusType(status) {
      const statusMap = {
        CREATED: "info",
        REVIEWING: "warning",
        APPROVED: "success",
        SIGNED: "primary",
        REPAYING: "",
        REJECTED: "danger",
        CLEARED_NORMAL: "success",
        CLEARED_EARLY: "success"
      };
      return statusMap[status] || "";
    },
    getStatusText(status) {
      const statusMap = {
        CREATED: "待分配",
        REVIEWING: "审核中",
        APPROVED: "待签约",
        SIGNED: "待放款",
        REPAYING: "还款中",
        REJECTED: "已拒绝",
        CLEARED_NORMAL: "已按时还清",
        CLEARED_EARLY: "已提前还清"
      };
      return statusMap[status] || status;
    },
    handleClose() {
      this.dialogVisible = false;
    },
    handleAssign() {
      this.$emit("assign", this.loan);
    },
    confirmApprove() {
      this.$emit("approve", this.loan, this.approveRemark);
      this.showApproveDialog = false;
      this.approveRemark = "";
    },
    confirmReject() {
      if (!this.rejectRemark || !this.rejectRemark.trim()) {
        this.$message.warning("请输入拒绝原因");
        return;
      }
      this.$emit("reject", this.loan, this.rejectRemark);
      this.showRejectDialog = false;
      this.rejectRemark = "";
    },
    confirmComment() {
      if (!this.commentRemark || !this.commentRemark.trim()) {
        this.$message.warning("请输入批注内容");
        return;
      }
      this.$emit("comment", this.loan, this.commentRemark);
      this.showCommentDialog = false;
      this.commentRemark = "";
    },
    confirmSign() {
      this.$emit("sign", this.loan, this.signRemark);
      this.showSignDialog = false;
      this.signRemark = "";
    },
    confirmDisburse() {
      this.$emit("disburse", this.loan, this.disburseRemark);
      this.showDisburseDialog = false;
      this.disburseRemark = "";
    },
    confirmClear() {
      this.$emit("clear", this.loan, this.clearRemark);
      this.showClearDialog = false;
      this.clearRemark = "";
    },
    handleContact() {
      this.$emit("contact", this.loan);
    },
    handleDownload(file) {
      this.$emit("download-file", file);
    }
  }
};
</script>

<style lang="less" scoped>
.loan-detail {
  .detail-section {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .file-list {
    .file-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }

      .file-info {
        display: flex;
        align-items: center;
        flex: 1;

        i {
          font-size: 20px;
          color: #409eff;
          margin-right: 8px;
        }

        .file-name {
          margin-right: 12px;
          color: #303133;
        }

        .file-type {
          padding: 2px 8px;
          background: #f0f2f5;
          border-radius: 4px;
          font-size: 12px;
          color: #606266;
        }
      }
    }
  }

  .empty-files {
    text-align: center;
    padding: 40px 0;
    color: #909399;
    font-size: 14px;
  }

  .action-buttons {
    margin-top: 24px;
    text-align: right;

    .el-button + .el-button {
      margin-left: 12px;
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>

