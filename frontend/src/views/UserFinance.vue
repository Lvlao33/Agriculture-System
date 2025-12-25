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
        v-for="loan in paginatedLoans"
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
          <div class="status-chip">
            <el-tag :type="getStatusType(loan.status)">
              {{ getStatusLabel(loan.status) }}
            </el-tag>
          </div>
        </div>

        <el-descriptions :column="3" size="small" border>
          <el-descriptions-item label="申请金额">
            {{ formatCurrency(loan.loanAmount) }}
          </el-descriptions-item>
          <el-descriptions-item label="贷款期限">
            {{ formatTerm(loan.loanTermMonths) }}
          </el-descriptions-item>
          <el-descriptions-item label="利率(年化)">
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

        <!-- 申请人详细信息表格 (不折叠) -->
        <div class="applicants-table" style="margin-top: 16px; margin-bottom: 16px;">
           <el-table :data="loan.loanUserStatuses || []" size="mini" style="width: 100%" border class="consistent-height-table">
              <el-table-column label="用户" width="150">
                <template slot-scope="scope">
                  <div class="user-cell" style="display: flex; align-items: center; gap: 8px; height: 28px;">
                    <el-avatar :size="24" :src="scope.row.user?.avatar" style="flex-shrink: 0;"></el-avatar>
                    <span style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ scope.row.user?.nickname || scope.row.user?.username || '未知' }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="姓名" width="100" show-overflow-tooltip>
                <template slot-scope="scope">{{ scope.row.name || '--' }}</template>
              </el-table-column>
              <el-table-column prop="phone" label="电话" width="120" show-overflow-tooltip>
                  <template slot-scope="scope">{{ scope.row.phone || '--' }}</template>
              </el-table-column>
              <el-table-column label="分担金额" width="120">
                <template slot-scope="scope">{{ formatCurrency(scope.row.amount) }}</template>
              </el-table-column>
              <el-table-column prop="purpose" label="用途" show-overflow-tooltip></el-table-column>
              <el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
              <el-table-column label="操作" width="100" fixed="right">
                <template slot-scope="scope">
                  <el-button 
                    v-if="scope.row.user && scope.row.user.id == currentUserId"
                    type="primary" 
                    plain
                    size="mini"
                    @click="openApplicantEdit(scope.row, loan.id)"
                  >修改资料</el-button>
                </template>
              </el-table-column>
           </el-table>
        </div>

        <div class="card-footer">
          <el-button
            type="text"
            :icon="isExpanded(loan.id) ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"
            @click="toggleExpand(loan)"
          >
            {{ isExpanded(loan.id) ? '收起进度' : '查看进度' }}
          </el-button>
          <div v-if="canEditLoan(loan.status)">
            <el-button
              type="primary"
              size="mini"
              plain
              @click="openEditDialog(loan)"
            >
              修改资料
            </el-button>
            <el-button
              type="success"
              size="mini"
              plain
              @click="openFileDialog(loan)"
            >
              补充文件
            </el-button>
          </div>
        </div>

        <!-- 进度详情展开区域 -->
        <el-collapse-transition>
          <div v-show="isExpanded(loan.id)" class="detail-content">
            <el-divider></el-divider>
            
            <div v-loading="loadingDetails[loan.id]">
              <!-- 进度条 -->
              <div class="progress-steps">
                <h4 class="detail-title">申请进度</h4>
                <el-steps :active="getActiveStep(loan.status)" finish-status="success" align-center>
                  <el-step 
                    v-for="(step, index) in steps" 
                    :key="index" 
                    :title="step.title"
                    :status="getStepStatus(loan.status, index)"
                  ></el-step>
                </el-steps>
              </div>

              <el-divider content-position="left">用户状态</el-divider>
              <div class="user-statuses">
                <el-table :data="loanDetails[loan.id]?.userStatuses || []" size="mini" style="width: 100%">
                  <el-table-column label="用户">
                    <template slot-scope="scope">
                      <div class="user-cell" style="display: flex; align-items: center; gap: 8px;">
                        <el-avatar :size="24" :src="scope.row.user?.avatar"></el-avatar>
                        <span>{{ scope.row.user?.nickname || scope.row.user?.username || '未知用户' }}</span>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="120">
                    <template slot-scope="scope">
                      <el-tag size="mini" :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <el-divider content-position="left">处理记录</el-divider>
              <div class="records-timeline">
                <el-timeline>
                  <el-timeline-item
                    v-for="(record, index) in loanDetails[loan.id]?.records || []"
                    :key="index"
                    :timestamp="formatDateTime(record.recordDate)"
                    placement="top"
                  >
                    <el-card shadow="never" class="record-card">
                      <h4>{{ getStatusLabel(record.applyStatus) }}</h4>
                      <p v-if="record.recordDetails">{{ record.recordDetails }}</p>
                      <p class="operator-info" v-if="record.user">
                        操作人: {{ record.user.nickname || record.user.username }}
                      </p>
                    </el-card>
                  </el-timeline-item>
                  <el-timeline-item v-if="!loanDetails[loan.id]?.records?.length" timestamp="暂无记录">
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </div>
        </el-collapse-transition>
      </el-card>
    </div>

    <el-pagination
      v-if="loans.length > pageSize"
      @current-change="handlePageChange"
      :current-page="currentPage"
      :page-size="pageSize"
      layout="total, prev, pager, next, jumper"
      :total="loans.length"
      class="pagination-container"
    >
    </el-pagination>

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
          <el-input v-model="editForm.loanAmount" placeholder="系统自动汇总，不可修改" disabled>
             <template slot="append">元</template>
          </el-input>
          <div style="font-size: 12px; color: #909399; line-height: 1.5;">由所有申请人分担金额汇总得出</div>
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
    <el-dialog
      title="补充贷款资料"
      :visible.sync="fileDialogVisible"
      width="650px"
      @close="resetFileDialog"
    >
      <template v-if="fileLoan">
        <p class="dialog-subtitle">
          贷款编号 #{{ fileLoan.id }} · {{ fileLoan.loanProduct?.name || "自定义贷款" }}
        </p>
      </template>

      <el-skeleton v-if="fileLoading" animated :rows="6" />

      <div v-else>
        <section class="files-section">
          <div class="section-heading">
            <h4>已上传文件</h4>
            <el-button type="text" size="small" @click="fetchLoanFiles(fileLoan?.id)">
              刷新
            </el-button>
          </div>

          <el-empty v-if="!loanFiles.length" description="暂无已上传文件" />

          <el-table
            v-else
            :data="loanFiles"
            size="small"
            border
            class="file-table"
          >
            <el-table-column prop="fileType" label="文件类型" width="140" />
            <el-table-column prop="fileName" label="文件名" />
            <el-table-column prop="updatedAt" label="更新时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.updatedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-link type="primary" @click="openLoanFile(row)">
                  查看
                </el-link>
              </template>
            </el-table-column>
          </el-table>
        </section>

        <el-divider>上传新文件</el-divider>

        <el-form label-width="100px" class="supplement-form">
          <el-form-item label="文件类型" required>
            <el-select
              v-model="supplementForm.fileType"
              placeholder="请选择文件类型"
              style="width: 100%"
            >
              <el-option
                v-for="option in fileTypeOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="选择文件" required>
            <el-upload
              ref="supplementUploadRef"
              class="upload-block"
              :auto-upload="false"
              :limit="1"
              :before-upload="beforeSupplementUpload"
              :on-change="handleSupplementChange"
              :on-remove="handleRemoveSupplementFile"
              :http-request="handleSupplementUpload"
            >
              <el-button slot="trigger" type="primary" size="small">
                选择文件
              </el-button>
              <div slot="tip" class="upload-tip">
                支持 PDF、JPG、PNG、DOC、DOCX，大小不超过 10MB
              </div>
            </el-upload>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="fileDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="uploadingFile"
          @click="submitSupplementUpload"
        >
          上传文件
        </el-button>
      </template>
    </el-dialog>
    <el-dialog
      title="修改申请人信息"
      :visible.sync="applicantEditDialogVisible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="applicantEditFormRef"
        :model="applicantEditForm"
        :rules="applicantEditRules"
        label-width="100px"
      >
        <el-form-item label="分担金额" prop="amount">
          <el-input v-model="applicantEditForm.amount" type="number" placeholder="请输入分担金额" />
        </el-form-item>
        <el-form-item label="资金用途" prop="purpose">
           <el-input v-model="applicantEditForm.purpose" placeholder="请输入资金用途" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="name">
           <el-input v-model="applicantEditForm.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
           <el-input v-model="applicantEditForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
           <el-input v-model="applicantEditForm.remark" type="textarea" placeholder="可选备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="applicantEditDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="applicantEditLoading" @click="submitApplicantEdit">确 定</el-button>
      </div>
    </el-dialog>    
  </div>
</template>

<script>
import { getLoanList, updateLoan, getLoanFiles, uploadLoanFile, getLoanUserStatuses, getLoanRecords, updateApplicantInfo } from "../api/finance.js";
import { searchUserById } from "../api/user";

export default {
  name: "UserFinance",
  data() {
    return {
      loans: [],
      loading: false,
      loadError: "",
      editDialogVisible: false,
      editLoading: false,
      fileDialogVisible: false,
      fileLoading: false,
      fileLoan: null,
      loanFiles: [],
      uploadingFile: false,
      supplementForm: {
        fileType: "",
        fileList: [],
      },
      fileTypeOptions: [
        { label: "身份证扫描件", value: "身份证扫描件" },
        { label: "营业执照", value: "营业执照" },
        { label: "银行流水", value: "银行流水" },
        { label: "贷款申请表", value: "贷款申请表" },
        { label: "收入证明", value: "收入证明" },
        { label: "其他", value: "其他" },
      ],
      editForm: {
        id: null,
        loanAmount: "",
        loanTermMonths: "",
        interestRate: "",
        loanPurpose: "",
        remark: "",
      },
      applicantEditDialogVisible: false,
      applicantEditLoading: false,
      applicantEditForm: {
        loanId: null,
        userId: null,
        amount: "",
        purpose: "",
        name: "",
        phone: "",
        remark: "",
      },
      applicantEditRules: {
        amount: [{ required: true, message: "请输入分担金额", trigger: "blur" }],
        name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
        phone: [{ required: true, message: "请输入联系电话", trigger: "blur" }],
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
      loanDetails: {},
      loadingDetails: {},
      steps: [
        { title: "提交申请", status: "CREATED" },
        { title: "银行审核", status: "REVIEWING" },
        { title: "审批结果", status: ["APPROVED", "REJECTED"] },
        { title: "签约", status: "SIGNED" },
        { title: "还款中", status: "REPAYING" },
        { title: "结清", status: ["CLEARED_NORMAL", "CLEARED_EARLY"] },
      ],
      expandedLoans: [],
      currentPage: 1,
      pageSize: 5,
    };
  },
  computed: {
    currentUserId() {
      return this.$store.state.loginUserId || window.localStorage.getItem("loginUserId");
    },
    paginatedLoans() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.loans.slice(start, end);
    },
    expandedSet() {
      return new Set(this.expandedLoans);
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
      if (!loan || !loan.id) {
        this.$message.error("无法获取贷款记录ID");
        return;
      }
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
    async openFileDialog(loan) {
      this.fileLoan = loan;
      this.fileDialogVisible = true;
      this.supplementForm = {
        fileType: "",
        fileList: [],
      };
      await this.fetchLoanFiles(loan?.id);
    },
    async fetchLoanFiles(loanId) {
      if (!loanId) return;
      this.fileLoading = true;
      try {
        const response = await getLoanFiles(loanId);
        if (Array.isArray(response)) {
          this.loanFiles = response;
        } else if (response && Array.isArray(response.data)) {
          this.loanFiles = response.data;
        } else {
          this.loanFiles = [];
        }
      } catch (error) {
        console.error("Failed to fetch loan files:", error);
        this.$message.error("获取文件列表失败，请稍后再试");
      } finally {
        this.fileLoading = false;
      }
    },
    resetFileDialog() {
      this.fileDialogVisible = false;
      this.fileLoan = null;
      this.loanFiles = [];
      this.supplementForm = {
        fileType: "",
        fileList: [],
      };
      this.uploadingFile = false;
      if (this.$refs.supplementUploadRef) {
        this.$refs.supplementUploadRef.clearFiles();
      }
    },
    beforeSupplementUpload(file) {
      const allowedTypes = /\.(pdf|jpg|jpeg|png|doc|docx)$/i;
      const isValid = allowedTypes.test(file.name);
      if (!isValid) {
        this.$message.error("上传文件格式不正确");
        return false;
      }
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        this.$message.error("上传文件大小不能超过 10MB");
        return false;
      }
      if (!this.supplementForm.fileType) {
        this.$message.warning("请先选择文件类型");
        return false;
      }
      return true;
    },
    handleSupplementChange(file, fileList) {
      this.supplementForm.fileList = fileList;
    },
    handleRemoveSupplementFile(file, fileList) {
      this.supplementForm.fileList = fileList;
    },
    submitSupplementUpload() {
      if (!this.fileLoan?.id) {
        this.$message.error("贷款ID缺失，无法上传文件");
        return;
      }
      if (!this.supplementForm.fileType) {
        this.$message.warning("请先选择文件类型");
        return;
      }
      if (!this.supplementForm.fileList.length) {
        this.$message.warning("请先选择要上传的文件");
        return;
      }
      if (this.$refs.supplementUploadRef) {
        this.uploadingFile = true;
        this.$refs.supplementUploadRef.submit();
      }
    },
    async handleSupplementUpload(options) {
      const { file, onSuccess, onError } = options;
      if (!this.fileLoan?.id) {
        this.uploadingFile = false;
        onError && onError(new Error("loanId missing"));
        return;
      }
      try {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("fileType", this.supplementForm.fileType || "其他");
        formData.append("operatorId", this.currentUserId);
        await uploadLoanFile(this.fileLoan.id, formData);
        this.$message.success("文件上传成功");
        this.supplementForm.fileList = [];
        if (this.$refs.supplementUploadRef) {
          this.$refs.supplementUploadRef.clearFiles();
        }
        await this.fetchLoanFiles(this.fileLoan.id);
        onSuccess && onSuccess();
      } catch (error) {
        console.error("Failed to upload file:", error);
        this.$message.error("文件上传失败，请稍后再试");
        onError && onError(error);
      } finally {
        this.uploadingFile = false;
      }
    },
    openLoanFile(file) {
      if (file?.filePath && /^https?:\/\//i.test(file.filePath)) {
        window.open(file.filePath, "_blank");
      } else if (file?.filePath) {
        this.$alert(
          `文件已存储在服务器：${file.filePath}\n请联系管理员获取该文件。`,
          "文件路径",
          { confirmButtonText: "知道了" }
        );
      } else {
        this.$message.info("当前文件暂无可预览的地址");
      }
    },
    isExpanded(loanId) {
      return this.expandedSet.has(loanId);
    },
    toggleExpand(loan) {
      const index = this.expandedLoans.indexOf(loan.id);
      if (index > -1) {
        this.expandedLoans.splice(index, 1);
      } else {
        this.expandedLoans.push(loan.id);
        this.fetchLoanDetails(loan.id);
      }
    },
    async fetchLoanDetails(loanId) {
      if (this.loanDetails[loanId]) return; // Already fetched
      
      this.$set(this.loadingDetails, loanId, true);
      try {
        const [statusesRes, recordsRes] = await Promise.all([
          getLoanUserStatuses(loanId),
          getLoanRecords(loanId)
        ]);
        
        const details = {
          userStatuses: [],
          records: []
        };

        if (statusesRes && (Array.isArray(statusesRes) || Array.isArray(statusesRes.data))) {
          details.userStatuses = Array.isArray(statusesRes) ? statusesRes : statusesRes.data;
        }
        
        if (recordsRes && (Array.isArray(recordsRes) || Array.isArray(recordsRes.data))) {
          details.records = Array.isArray(recordsRes) ? recordsRes : recordsRes.data;
        }

        this.$set(this.loanDetails, loanId, details);

        // Fetch user details for user statuses
        if (details.userStatuses && details.userStatuses.length > 0) {
          const userPromises = details.userStatuses.map(async (status) => {
            // Check if user object is already populated or if we only have userId
            // The backend might return a user object with null fields if not fully populated
            // But based on previous context, we might need to fetch it if it's missing or incomplete
            // Actually, the user removed @JsonIgnore, so user object SHOULD be there.
            // But the user request says: "根据返回的loanuserstatus中的userid可以调用...这个接口将信息加载出来"
            // So I should fetch it.
            // Let's assume status.user might be null or we need to enrich it.
            // Or maybe status.user.id is available.
            
            let userId = status.user?.id;
            // If status.user is null, maybe there is a userId field? 
            // The model has 'User user', so it should be in 'user' object.
            
            if (userId) {
              try {
                const userRes = await searchUserById(userId);
                if (userRes && userRes.success && userRes.data) {
                  // Update the user info in the status object
                  // We need to be careful to update the reactive object
                  this.$set(status, 'user', { ...status.user, ...userRes.data });
                }
              } catch (err) {
                console.error(`Failed to fetch user ${userId}`, err);
              }
            }
          });
          await Promise.all(userPromises);
        }

      } catch (error) {
        console.error("Fetch details error:", error);
        this.$message.error("获取详情失败");
      } finally {
        this.$set(this.loadingDetails, loanId, false);
      }
    },
    getActiveStep(status) {
      for (let i = 0; i < this.steps.length; i++) {
        const stepStatus = this.steps[i].status;
        if (Array.isArray(stepStatus)) {
          if (stepStatus.includes(status)) return i + 1;
        } else {
          if (stepStatus === status) return i + 1;
        }
      }
      return 0;
    },
    getStepStatus(currentStatus, stepIndex) {
      // If current status is REJECTED and this is the result step (index 2), return error
      if (currentStatus === 'REJECTED' && stepIndex === 2) return 'error';
      
      const activeStep = this.getActiveStep(currentStatus);
      if (stepIndex < activeStep) return 'success';
      if (stepIndex === activeStep - 1) return 'process'; // activeStep is 1-based
      return 'wait';
    },

    handlePageChange(page) {
      this.currentPage = page;
      // Scroll to top of loan list
      this.$nextTick(() => {
        const loanList = document.querySelector('.loan-list');
        if (loanList) {
          loanList.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    },
    openApplicantEdit(row, loanId) {
      if (!row || !row.user) return;
      this.applicantEditForm = {
        loanId: loanId, 
        userId: row.user.id,
        amount: row.amount || "",
        purpose: row.purpose || "",
        name: row.name || row.user.nickname || row.user.username || "",
        phone: row.phone || row.user.phone || "",
        remark: row.remark || "",
      };
      this.applicantEditDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.applicantEditFormRef && this.$refs.applicantEditFormRef.clearValidate();
      });
    },
    async submitApplicantEdit() {
      if (!this.$refs.applicantEditFormRef) return;
      this.$refs.applicantEditFormRef.validate(async (valid) => {
        if (!valid) return;
        this.applicantEditLoading = true;
        try {
          const payload = {
            ...this.applicantEditForm,
            amount: Number(this.applicantEditForm.amount),
          };
          const response = await updateApplicantInfo(payload);
           if (response && response.success === false) {
             this.$message.error(response.message || "修改失败");
           } else {
             this.$message.success("修改成功");
             this.applicantEditDialogVisible = false;
             // Refresh details to show new data
             if (this.applicantEditForm.loanId) {
                // Refresh loan list to update total amount
                this.fetchLoans();
                // Refresh details
                this.loanDetails[this.applicantEditForm.loanId] = null; // Clear cache
                this.fetchLoanDetails(this.applicantEditForm.loanId);
             }
           }
        } catch (error) {
          console.error("Failed to update applicant info:", error);
          this.$message.error("修改失败，请稍后再试");
        } finally {
          this.applicantEditLoading = false;
        }
      });
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
  gap: 10px;
}

.loan-card {
  border-radius: 12px;
  margin-bottom: 0;
}

.card-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.dialog-subtitle {
  margin: 0 0 12px;
  color: #666;
}

.files-section {
  margin-bottom: 16px;
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.file-table {
  margin-bottom: 12px;
}

.upload-block {
  width: 100%;
}

.upload-tip {
  color: #999;
  margin-top: 6px;
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

.status-chip {
  display: flex;
  justify-content: flex-end;
}

.mt-16 {
  margin-top: 16px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.detail-content {
  padding: 20px 0 0 0;
}

.detail-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.progress-steps {
  margin-bottom: 24px;
  padding: 0;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.records-timeline {
  margin-top: 16px;
  padding-left: 10px;
}

.record-card {
  padding: 10px;
}
.record-card h4 {
  margin: 0 0 8px 0;
  color: #303133;
}
.record-card p {
  margin: 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}
.operator-info {
  margin-top: 8px !important;
  color: #909399 !important;
  font-size: 12px !important;
}
/* Ensure consistent row height regardless of button presence */
.consistent-height-table .el-table__row {
  height: 50px; /* Adjust according to button height + padding */
}
.consistent-height-table .cell {
  line-height: 28px; /* Vertically align text with button */
}
</style>