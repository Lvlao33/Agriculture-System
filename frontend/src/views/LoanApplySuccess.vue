<template>
  <div class="loan-apply-success">
    <div class="success-container">
      <div class="success-icon">
        <i class="el-icon-success"></i>
      </div>
      <h2 class="success-title">申请资料提交成功</h2>
      <p class="success-message">您的贷款申请已成功提交，我们将在3-5个工作日内完成审核，请耐心等待。</p>
      
      <div class="success-info" v-if="loanId">
        <p>申请编号：<span class="info-value">{{ loanId }}</span></p>
        <p class="info-tip">您可以在"个人中心-融资申请"中查看申请进度</p>
      </div>

      <div class="success-actions">
        <el-button type="primary" size="large" @click="goToHome">
          <i class="el-icon-house"></i> 返回首页
        </el-button>
        <el-button type="success" size="large" @click="goToMyLoans">
          <i class="el-icon-document"></i> 查看我的申请
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoanApplySuccess',
  data() {
    return {
      loanId: ''
    }
  },
  mounted() {
    // 从路由参数获取申请ID
    this.loanId = this.$route.query.loanId || '';
  },
  methods: {
    goToHome() {
      this.$router.push('/home/front');
    },
    goToMyLoans() {
      // 跳转到个人中心的融资申请页面
      this.$router.push('/home/user/userfinance');
    }
  }
}
</script>

<style lang="less" scoped>
.loan-apply-success {
  width: 100%;
  background-color: #f5f5f5;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.success-container {
  width: 600px;
  background-color: white;
  border-radius: 12px;
  padding: 60px 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.success-icon {
  margin-bottom: 30px;

  i {
    font-size: 80px;
    color: #67c23a;
  }
}

.success-title {
  font-size: 28px;
  color: #333;
  margin: 0 0 20px 0;
  font-weight: bold;
}

.success-message {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 30px;
}

.success-info {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 40px;
  text-align: left;

  p {
    font-size: 14px;
    color: #666;
    margin: 10px 0;
    line-height: 1.6;

    .info-value {
      color: #4CAF50;
      font-weight: bold;
      font-size: 16px;
    }
  }

  .info-tip {
    color: #999;
    font-size: 12px;
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px solid #e0e0e0;
  }
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 20px;

  .el-button {
    padding: 12px 40px;
    font-size: 16px;

    i {
      margin-right: 5px;
    }
  }
}

@media (max-width: 768px) {
  .success-container {
    width: 95%;
    padding: 40px 20px;
  }

  .success-actions {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }
}
</style>

