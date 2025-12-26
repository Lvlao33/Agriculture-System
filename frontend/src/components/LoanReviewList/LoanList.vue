<template>
  <div class="loan-list">
    <el-table
      v-loading="loading"
      :data="loans"
      stripe
      style="width: 100%"
      @row-click="handleRowClick"
    >
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column label="贷款提交人" width="130" show-overflow-tooltip align="left">
        <template slot-scope="scope">
          <span>{{ scope.row.applicantName }}</span>
          <el-tag v-if="scope.row.loanUserStatuses && scope.row.loanUserStatuses.length >= 2" type="warning" size="mini" effect="plain" style="margin-left: 5px; padding: 0 4px; height: 18px; line-height: 16px;">联合</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applicantPhone" label="电话" width="110" align="left" />
      <el-table-column label="金额" width="120" align="right">
        <template slot-scope="scope">
          ¥{{ formatAmount(scope.row.loanAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="loanTermMonths" label="期限" width="70" align="center">
        <template slot-scope="scope">
          {{ scope.row.loanTermMonths }}月
        </template>
      </el-table-column>
      <el-table-column prop="loanPurpose" label="用途" min-width="150" show-overflow-tooltip align="left" />
      <el-table-column label="申请时间" width="140" align="left">
        <template slot-scope="scope">
          {{ formatDate(scope.row.applicationDate) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template slot-scope="scope">
          <el-button type="primary" size="small" plain @click.stop="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!loading && loans.length === 0" class="empty-state">
      <p>暂无数据</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "LoanList",
  props: {
    loans: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    status: {
      type: String,
      required: true
    }
  },
  methods: {
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
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      return `${year}/${month}/${day} ${hour}:${minute}`;
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
    handleRowClick(row) {
      this.handleView(row);
    },
    handleView(loan) {
      this.$emit("view-detail", loan);
    }
  }
};
</script>

<style lang="less" scoped>
.loan-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow-x: auto;

  .empty-state {
    text-align: center;
    padding: 60px 0;
    color: #909399;
    font-size: 14px;
  }

  /deep/ .el-table {
    .el-table__row {
      cursor: pointer;
      
      &:hover {
        background-color: #f5f7fa;
      }
    }

    // 确保单元格内容垂直居中
    .el-table__cell {
      vertical-align: middle;
      padding: 12px 0;
      
      .cell {
        line-height: 1.5;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    // 左对齐的列
    .el-table__cell[style*="text-align: left"] .cell,
    .el-table__cell[style*="text-align:left"] .cell {
      justify-content: flex-start;
    }

    // 右对齐的列
    .el-table__cell[style*="text-align: right"] .cell,
    .el-table__cell[style*="text-align:right"] .cell {
      justify-content: flex-end;
    }

    // 状态标签和按钮对齐
    .el-tag {
      margin: 0;
      display: inline-flex;
      align-items: center;
      vertical-align: middle;
    }

    .el-button {
      margin: 0;
      display: inline-flex;
      align-items: center;
      vertical-align: middle;
    }
  }
}
</style>

