<template>
  <div class="smart-match-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">智能匹配</h1>
        <p class="page-subtitle">基于您的需求，为您推荐最适合的金融产品</p>
      </div>

      <!-- 匹配表单区域 -->
      <div class="match-form-section">
        <el-card class="form-card">
          <div slot="header" class="card-header">
            <span>请填写您的需求信息</span>
          </div>
          
          <el-form :model="matchForm" :rules="rules" ref="matchForm" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="贷款金额" prop="amount">
                  <el-input-number
                    v-model="matchForm.amount"
                    :min="1000"
                    :max="1000000"
                    :step="1000"
                    placeholder="请输入贷款金额"
                    style="width: 100%"
                  ></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="贷款期限" prop="period">
                  <el-select v-model="matchForm.period" placeholder="请选择贷款期限" style="width: 100%">
                    <el-option label="6个月" value="6"></el-option>
                    <el-option label="1年" value="12"></el-option>
                    <el-option label="2年" value="24"></el-option>
                    <el-option label="3年" value="36"></el-option>
                    <el-option label="5年" value="60"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="贷款用途" prop="purpose">
                  <el-select v-model="matchForm.purpose" placeholder="请选择贷款用途" style="width: 100%">
                    <el-option label="农业生产" value="agriculture"></el-option>
                    <el-option label="设备采购" value="equipment"></el-option>
                    <el-option label="流动资金" value="working_capital"></el-option>
                    <el-option label="其他" value="other"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年收入" prop="income">
                  <el-input-number
                    v-model="matchForm.income"
                    :min="0"
                    :step="1000"
                    placeholder="请输入年收入"
                    style="width: 100%"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="其他需求" prop="requirements">
              <el-input
                type="textarea"
                v-model="matchForm.requirements"
                placeholder="请描述您的其他需求"
                :rows="3"
              ></el-input>
            </el-form-item>
            
            <el-form-item class="form-actions">
              <el-button type="primary" @click="startMatch" :loading="matching">
                <i class="el-icon-search"></i>
                开始智能匹配
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 匹配结果区域 -->
      <div class="match-results-section" v-if="showResults">
        <el-card class="results-card">
          <div slot="header" class="card-header">
            <span>匹配结果</span>
            <el-button type="text" @click="showResults = false" class="close-btn">
              <i class="el-icon-close"></i>
            </el-button>
          </div>
          
          <div class="results-content">
            <div class="no-results" v-if="matchResults.length === 0">
              <i class="el-icon-warning"></i>
              <p>暂无匹配的金融产品，请调整您的需求条件</p>
            </div>
            
            <div class="results-list" v-else>
              <div class="result-item" v-for="(result, index) in matchResults" :key="index">
                <div class="result-header">
                  <h3>{{ result.productName }}</h3>
                  <span class="bank-name">{{ result.bankName }}</span>
                </div>
                <div class="result-details">
                  <div class="detail-item">
                    <span class="label">匹配度：</span>
                    <el-progress :percentage="result.matchRate" :color="getProgressColor(result.matchRate)"></el-progress>
                  </div>
                  <div class="detail-item">
                    <span class="label">利率：</span>
                    <span class="value">{{ result.interestRate }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">放款时间：</span>
                    <span class="value">{{ result.loanTime }}</span>
                  </div>
                </div>
                <div class="result-actions">
                  <el-button type="success" size="small">立即申请</el-button>
                  <el-button size="small">查看详情</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SmartMatchPage',
  data() {
    return {
      matchForm: {
        amount: null,
        period: '',
        purpose: '',
        income: null,
        requirements: ''
      },
      rules: {
        amount: [
          { required: true, message: '请输入贷款金额', trigger: 'blur' }
        ],
        period: [
          { required: true, message: '请选择贷款期限', trigger: 'change' }
        ],
        purpose: [
          { required: true, message: '请选择贷款用途', trigger: 'change' }
        ],
        income: [
          { required: true, message: '请输入年收入', trigger: 'blur' }
        ]
      },
      matching: false,
      showResults: false,
      matchResults: []
    };
  },
  methods: {
    startMatch() {
      this.$refs.matchForm.validate((valid) => {
        if (valid) {
          this.matching = true;
          // 模拟匹配过程
          setTimeout(() => {
            this.performMatch();
            this.matching = false;
            this.showResults = true;
          }, 2000);
        }
      });
    },
    
    performMatch() {
      // 模拟匹配结果
      this.matchResults = [
        {
          productName: '农业专项贷款',
          bankName: '中国农业银行',
          matchRate: 95,
          interestRate: '3.85%',
          loanTime: '1-3个工作日'
        },
        {
          productName: '惠农e贷',
          bankName: '中国建设银行',
          matchRate: 88,
          interestRate: '4.2%',
          loanTime: '2-5个工作日'
        },
        {
          productName: '乡村振兴贷',
          bankName: '中国工商银行',
          matchRate: 82,
          interestRate: '4.5%',
          loanTime: '3-7个工作日'
        }
      ];
    },
    
    resetForm() {
      this.$refs.matchForm.resetFields();
      this.showResults = false;
      this.matchResults = [];
    },
    
    getProgressColor(percentage) {
      if (percentage >= 90) return '#67C23A';
      if (percentage >= 80) return '#E6A23C';
      return '#F56C6C';
    }
  }
};
</script>

<style lang="less" scoped>
.smart-match-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px 0;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  
  .page-title {
    font-size: 32px;
    color: #303133;
    margin: 0 0 10px 0;
    font-weight: 600;
  }
  
  .page-subtitle {
    font-size: 16px;
    color: #606266;
    margin: 0;
  }
}

.match-form-section {
  margin-bottom: 30px;
  
  .form-card {
    .card-header {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
    
    .form-actions {
      text-align: center;
      margin-top: 30px;
      
      .el-button {
        margin: 0 10px;
        padding: 12px 30px;
      }
    }
  }
}

.match-results-section {
  .results-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      
      .close-btn {
        padding: 0;
        font-size: 16px;
      }
    }
    
    .results-content {
      .no-results {
        text-align: center;
        padding: 40px 0;
        color: #909399;
        
        i {
          font-size: 48px;
          margin-bottom: 16px;
          display: block;
        }
      }
      
      .results-list {
        .result-item {
          border: 1px solid #EBEEF5;
          border-radius: 8px;
          padding: 20px;
          margin-bottom: 16px;
          background-color: #fff;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .result-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
            
            h3 {
              margin: 0;
              font-size: 18px;
              color: #303133;
            }
            
            .bank-name {
              color: #909399;
              font-size: 14px;
            }
          }
          
          .result-details {
            margin-bottom: 16px;
            
            .detail-item {
              display: flex;
              align-items: center;
              margin-bottom: 8px;
              
              &:last-child {
                margin-bottom: 0;
              }
              
              .label {
                width: 80px;
                color: #606266;
                font-size: 14px;
              }
              
              .value {
                color: #303133;
                font-weight: 500;
              }
            }
          }
          
          .result-actions {
            text-align: right;
            
            .el-button {
              margin-left: 8px;
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-container {
    padding: 0 10px;
  }
  
  .page-header {
    .page-title {
      font-size: 24px;
    }
  }
  
  .match-form-section {
    .form-card {
      .el-form {
        .el-form-item {
          .el-col {
            margin-bottom: 16px;
          }
        }
      }
    }
  }
  
  .match-results-section {
    .results-card {
      .results-content {
        .results-list {
          .result-item {
            .result-header {
              flex-direction: column;
              align-items: flex-start;
              
              h3 {
                margin-bottom: 8px;
              }
            }
            
            .result-actions {
              text-align: left;
              margin-top: 16px;
            }
          }
        }
      }
    }
  }
}
</style>
