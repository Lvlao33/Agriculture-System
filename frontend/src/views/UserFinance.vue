<template>
  <div class="user-finance">
    <div class="section-header">
      <div>
        <h2>我的贷款</h2>
        <p>查看当前账户提交的所有贷款申请状态。</p>
      </div>
      <el-button type="primary" size="small" @click="$router.push('/home/smartMatch')">
        继续申请
      </el-button>
    </div>

    <el-alert
      v-if="!currentUserId"
      title="请先登录后再查看贷款信息"
      type="info"
      show-icon
    />

    <el-skeleton v-else-if="loading" animated :rows="6" />

    <el-empty
      v-else-if="!loans.length && !loadError"
      description="暂无贷款记录，去申请一个吧"
    />

    <el-alert
      v-if="loadError"
      :title="loadError"
      type="error"
      show-icon
      class="mt-16"
    />

    <div v-if="loans.length" class="loan-list">
      <el-card
        v-for="loan in loans"
        :key="loan.id"
        class="loan-card"
        shadow="hover"
      >
        <div class="card-header">
          <div class="title-group">
            <span class="loan-id">贷款编号 #{{ loan.id }}</span>
            <span class="product-name">{{ loan.loanProduct?.name || '自定义贷款' }}</span>
            <span class="product-bank">{{ loan.loanProduct?.bank || '银行待确认' }}</span>
          </div>
          <div class="card-actions">
            <el-tag :type="getStatusType(loan.status)">
              {{ getStatusLabel(loan.status) }}
            </el-tag>
            <el-tooltip
              effect="dark"
              :content="canEditLoan(loan.status) ? '修改这条贷款资料' : '当前状态不可修改'"
              placement="top"
            >
              <el-button
                :type="canEditLoan(loan.status) ? 'primary' : 'info'"
                size="mini"
                :disabled="!canEditLoan(loan.status)"
                plain
                @click="openEditDialog(loan)"
              >
                修改资料
              </el-button>
            </el-tooltip>
          </div>
        </div>

        <el-descriptions :column="3" size="small" border>
          <el-descriptions-item label="申请金额">
            {{ formatCurrency(loan.loanAmount) }}
          </el-descriptions-item>
          <el-descriptions-item label="贷款期限">
            {{ formatTerm(loan.loanTermMonths) }}
          </el-descriptions-item>
          <el-descriptions-item label="利率（年化）">
            {{ formatRate(loan.interestRate) }}
          </el-descriptions-item>

          <el-descriptions-item label="提交时间">
            {{ formatDateTime(loan.applicationDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="最近更新时间">
            {{ formatDateTime(loan.updateDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="还款截止日">
            {{ formatDate(loan.repaymentDueDate) }}
          </el-descriptions-item>

          <el-descriptions-item label="贷款用途" :span="3">
            {{ loan.loanPurpose || '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="3">
            {{ loan.remark || '暂无备注' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

    <el-dialog
      title="修改贷款资料"
      :visible.sync="editDialogVisible"
      width="500px"
      @close="resetEditDialog"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
      >
        <el-form-item label="贷款金额" prop="loanAmount">
          <el-input v-model="editForm.loanAmount" placeholder="请输入贷款金额" />
        </el-form-item>
        <el-form-item label="贷款期限(个月)" prop="loanTermMonths">
          <el-input v-model="editForm.loanTermMonths" placeholder="请输入贷款期限" />
        </el-form-item>
        <el-form-item label="利率(%)" prop="interestRate">
          <el-input v-model="editForm.interestRate" placeholder="请输入年化利率" />
        </el-form-item>
        <el-form-item label="贷款用途" prop="loanPurpose">
          <el-input
            v-model="editForm.loanPurpose"
            type="textarea"
            :rows="3"
            placeholder="请输入贷款用途"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="可选填写备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="editLoading" @click="submitEdit">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getLoanList, updateLoan } from "../api/finance.js";

export default {
  name: "UserFinance",
  data() {
    return {
      loans: [],
      loading: false,
      loadError: "",
      editDialogVisible: false,
      editLoading: false,
      editForm: {
        id: null,
        loanAmount: "",
        loanTermMonths: "",
        interestRate: "",
        loanPurpose: "",
        remark: "",
      },
      editRules: {
        loanAmount: [{ required: true, message: "请输入贷款金额", trigger: "blur" }],
        loanTermMonths: [{ required: true, message: "请输入贷款期限", trigger: "blur" }],
        interestRate: [{ required: true, message: "请输入利率", trigger: "blur" }],
        loanPurpose: [{ required: true, message: "请输入贷款用途", trigger: "blur" }],
      },
      statusMap: {
        CREATED: { label: "待审核", type: "warning" },
        REVIEWING: { label: "审核中", type: "warning" },
        APPROVED: { label: "审核通过", type: "success" },
        REJECTED: { label: "已驳回", type: "danger" },
        SIGNED: { label: "已签约", type: "success" },
        REPAYING: { label: "还款中", type: "info" },
        CLEARED_NORMAL: { label: "正常结清", type: "success" },
        CLEARED_EARLY: { label: "提前结清", type: "success" },
      },
    };
  },
  computed: {
    currentUserId() {
      return this.$store.state.loginUserId || window.localStorage.getItem("loginUserId");
    },
  },
  watch: {
    currentUserId(newVal, oldVal) {
      if (newVal && newVal !== oldVal) {
        this.fetchLoans();
      }
    },
  },
  methods: {
    async fetchLoans() {
      if (!this.currentUserId) {
        return;
      }
      this.loading = true;
      this.loadError = "";
      try {
        const response = await getLoanList(this.currentUserId);
        if (Array.isArray(response)) {
          this.loans = response;
        } else if (response && Array.isArray(response.data)) {
          this.loans = response.data;
        } else {
          this.loans = [];
        }
      } catch (error) {
        this.loadError = "获取贷款列表失败，请稍后重试";
        console.error("Failed to fetch loans:", error);
      } finally {
        this.loading = false;
      }
    },
    getStatusLabel(status) {
      return this.statusMap[status]?.label || status || "未知状态";
    },
    getStatusType(status) {
      return this.statusMap[status]?.type || "info";
    },
    canEditLoan(status) {
      return status === "CREATED" || status === "REVIEWING";
    },
    formatCurrency(value) {
      if (value === null || value === undefined) return "--";
      const number = Number(value);
      if (Number.isNaN(number)) return value;
      return new Intl.NumberFormat("zh-CN", {
        style: "currency",
        currency: "CNY",
        minimumFractionDigits: 2,
      }).format(number);
    },
    formatTerm(months) {
      if (!months) return "--";
      return `${months} 个月`;
    },
    formatRate(value) {
      if (value === null || value === undefined) return "--";
      const number = Number(value);
      if (Number.isNaN(number)) return value;
      return `${number}%`;
    },
    formatDateTime(value) {
      if (!value) return "--";
      return new Date(value).toLocaleString("zh-CN");
    },
    formatDate(value) {
      if (!value) return "--";
      return new Date(value).toLocaleDateString("zh-CN");
    },
    openEditDialog(loan) {
      if (!this.canEditLoan(loan?.status)) {
        this.$message.warning("当前状态不可修改");
        return;
      }
      // 确保 loanId 存在
      if (!loan || !loan.id) {
        this.$message.error("无法获取贷款记录ID");
        return;
      }
      console.log("Opening edit dialog for loan ID:", loan.id);
      this.editForm = {
        id: loan.id,
        loanAmount: loan.loanAmount,
        loanTermMonths: loan.loanTermMonths,
        interestRate: loan.interestRate,
        loanPurpose: loan.loanPurpose || "",
        remark: loan.remark || "",
      };
      this.editDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.editFormRef && this.$refs.editFormRef.clearValidate();
      });
    },
    resetEditDialog() {
      this.editDialogVisible = false;
      this.editLoading = false;
      this.editForm = {
        id: null,
        loanAmount: "",
        loanTermMonths: "",
        interestRate: "",
        loanPurpose: "",
        remark: "",
      };
    },
    submitEdit() {
      if (!this.$refs.editFormRef) return;
      // 验证 loanId 是否存在
      if (!this.editForm.id) {
        this.$message.error("贷款ID不能为空");
        return;
      }
      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) return;
        this.editLoading = true;
        try {
          const loanId = this.editForm.id;
          const payload = this.buildUpdatePayload();
          console.log("Updating loan with ID:", loanId, "Payload:", payload);
          const response = await updateLoan(loanId, payload);
          if (response && response.success === false) {
            this.$message.error(response.message || "修改失败，请稍后再试");
          } else {
            this.$message.success("贷款资料修改成功");
            this.editDialogVisible = false;
            await this.fetchLoans();
          }
        } catch (error) {
          console.error("Failed to update loan:", error);
          this.$message.error("修改失败，请稍后再试");
        } finally {
          this.editLoading = false;
        }
      });
    },
    buildUpdatePayload() {
      return {
        id: this.editForm.id,
        loanAmount: Number(this.editForm.loanAmount),
        loanTermMonths: Number(this.editForm.loanTermMonths),
        interestRate: Number(this.editForm.interestRate),
        loanPurpose: this.editForm.loanPurpose,
        remark: this.editForm.remark,
      };
    },
  },
  created() {
    this.fetchLoans();
  },
};
</script>

<style scoped>
.user-finance {
  padding: 24px;
  background-color: #f7f9fb;
  min-height: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.section-header p {
  margin: 4px 0 0;
  color: #666;
  font-size: 14px;
}

.loan-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.loan-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: baseline;
}

.loan-id {
  font-weight: 600;
  color: #333;
}

.product-name {
  font-size: 16px;
  color: #409EFF;
}

.product-bank {
  font-size: 13px;
  color: #999;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-actions .el-button {
  padding: 6px 12px;
}

.mt-16 {
  margin-top: 16px;
}
</style>