<template>
  <div class="smart-match-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <i class="el-icon-lightbulb"></i>
          智能匹配
        </h1>
        <p class="page-subtitle">基于您的需求，为您推荐最适合的金融产品与合作伙伴</p>
      </div>

      <!-- 匹配表单区域 -->
      <div class="match-form-section">
        <el-card class="form-card" shadow="hover">
          <div slot="header" class="card-header">
            <i class="el-icon-edit"></i>
            <span>请填写您的需求信息</span>
          </div>
          
          <el-form :model="matchForm" :rules="rules" ref="matchForm" label-width="120px">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="您的姓名" prop="applicantName">
                  <el-input v-model="matchForm.applicantName" placeholder="请输入真实姓名"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="applicantPhone">
                  <el-input v-model="matchForm.applicantPhone" placeholder="请输入联系电话"></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="贷款金额" prop="amount">
                  <el-input-number
                    v-model="matchForm.amount"
                    :min="1000"
                    :max="1000000"
                    :step="1000"
                    :precision="0"
                    placeholder="请输入贷款金额"
                    style="width: 100%"
                    controls-position="right"
                  ></el-input-number>
                  <div class="form-tip">建议金额：1,000 - 1,000,000 元</div>
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
            
            <el-row :gutter="24">
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
                    :precision="0"
                    placeholder="请输入年收入"
                    style="width: 100%"
                    controls-position="right"
                  ></el-input-number>
                  <div class="form-tip">用于评估您的还款能力</div>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="其他需求" prop="requirements">
              <el-input
                type="textarea"
                v-model="matchForm.requirements"
                placeholder="请描述您的其他需求（选填）"
                :rows="3"
                maxlength="200"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <el-form-item class="form-actions">
              <el-button 
                type="primary" 
                size="large" 
                @click="startMatch" 
                :loading="matching"
                :icon="matching ? '' : 'el-icon-search'"
              >
                {{ matching ? '匹配中...' : '开始智能匹配' }}
              </el-button>
              <el-button size="large" @click="resetForm" :disabled="matching">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 匹配中动画 -->
      <div class="matching-section" v-if="matching">
        <el-card class="matching-card" shadow="never">
          <div class="matching-content">
            <div class="matching-icon">
              <i class="el-icon-loading"></i>
            </div>
            <p class="matching-text">正在为您智能匹配最适合的金融产品...</p>
            <p class="matching-subtext">请稍候</p>
          </div>
        </el-card>
      </div>

      <!-- 匹配结果区域 -->
      <div class="match-results-section" v-if="showResults && !matching">
        <el-card class="results-card" shadow="hover">
          <div slot="header" class="card-header">
            <div class="header-left">
              <i class="el-icon-success"></i>
              <span>匹配结果</span>
              <el-tag type="success" size="small" style="margin-left: 10px;">
                共找到 {{ matchResults.length }} 位推荐合作伙伴
              </el-tag>
            </div>
            <el-button type="text" @click="showResults = false" class="close-btn">
              <i class="el-icon-close"></i>
            </el-button>
          </div>
          
          <div class="results-content">
            <div class="no-results" v-if="matchResults.length === 0">
              <i class="el-icon-warning-outline"></i>
              <p>暂无匹配的推荐结果</p>
              <p class="no-results-tip">请调整您的需求条件后重新匹配</p>
            </div>
            
            <div class="results-list" v-else>
              <div 
                class="result-item" 
                v-for="(result, index) in matchResults" 
                :key="index"
                :class="{ 'top-match': index === 0 }"
              >
                <div class="result-badge" v-if="index === 0">
                  <i class="el-icon-star-on"></i>
                  最佳匹配
                </div>
                <div class="result-header">
                  <div class="header-info">
                    <h3>{{ result.productName }}</h3>
                    <span class="bank-name">
                      <i class="el-icon-office-building"></i>
                      {{ result.bankName }}
                    </span>
                  </div>
                  <div class="match-rate">
                    <div class="rate-value">{{ result.matchRate }}%</div>
                    <div class="rate-label">匹配度</div>
                  </div>
                </div>
                
                <div class="result-details">
                  <div class="detail-row">
                    <div class="detail-item">
                      <span class="label">
                        <i class="el-icon-coin"></i>
                        申请额度
                      </span>
                      <span class="value">{{ formatAmount(result.amount) }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">
                        <i class="el-icon-time"></i>
                        贷款期限
                      </span>
                      <span class="value">{{ result.term }}个月</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">
                        <i class="el-icon-trophy"></i>
                        参考利率
                      </span>
                      <span class="value highlight">{{ result.interestRate }}</span>
                    </div>
                      <div class="detail-item">
                       <span class="label">
                         <i class="el-icon-phone-outline"></i>
                         联系意向
                       </span>
                       <span class="value">{{ result.loanTime }}</span>
                     </div>
                  </div>
                  <div class="match-progress">
                    <span class="progress-label">匹配度</span>
                    <el-progress 
                      :percentage="result.matchRate" 
                      :color="getProgressColor(result.matchRate)"
                      :stroke-width="8"
                      :show-text="false"
                    ></el-progress>
                    <span class="progress-value">{{ result.matchRate }}%</span>
                  </div>
                </div>
                
                <div class="result-actions">
                  <el-button 
                    type="success" 
                    size="medium"
                    @click="goToApply(result)"
                  >
                    <i class="el-icon-check"></i>
                    立即申请
                  </el-button>
                  <el-button 
                    type="primary" 
                    size="medium"
                    plain
                    @click="goToDetail(result)"
                  >
                    <i class="el-icon-view"></i>
                    查看详情
                  </el-button>
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
import { selectIntention, updateIntention, insertIntention, selectRecommned } from '../api/finance'

export default {
  name: 'SmartMatchPage',
  data() {
    return {
      matchForm: {
        amount: null,
        period: '',
        purpose: '',
        income: null,
        amount: null,
        period: '',
        purpose: '',
        income: null,
        requirements: '',
        applicantName: '',
        applicantPhone: ''
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
        ],
        income: [
          { required: true, message: '请输入年收入', trigger: 'blur' }
        ],
        applicantName: [
          { required: true, message: '请输入您的姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        applicantPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      matching: false,
      showResults: false,
      matchResults: []
    };
  },
  methods: {
    async startMatch() {
      this.$refs.matchForm.validate(async (valid) => {
        if (valid) {
          this.matching = true;
          try {
            // 1. 构建意向数据对象
            const intentionDTO = {
              amount: this.matchForm.amount,
              repaymentPeriod: this.matchForm.period === '6' ? 6 : (parseInt(this.matchForm.period) || 12),
              application: this.matchForm.purpose || 'other',
              annualIncome: this.matchForm.income,
              otherNeeds: this.matchForm.requirements,
              realName: this.matchForm.applicantName,
              phone: this.matchForm.applicantPhone
            };

            // 2. 查询是否存在意向，决定是插入还是更新
            const res = await selectIntention();
            if (res && res.success && res.data) {
              // 更新
              intentionDTO.id = res.data.id;
              await updateIntention(intentionDTO);
            } else {
              // 插入
              await insertIntention(intentionDTO);
            }

            // 3. 获取推荐（智能匹配）
            const recommendRes = await selectRecommned();
            if (recommendRes && recommendRes.success) {
               this.processMatchResults(recommendRes.data);
               this.showResults = true;
            } else {
               this.matchResults = [];
               this.$message.warning('未找到匹配的推荐结果');
            }
          } catch (error) {
            console.error('Matching failed:', error);
            this.$message.error('匹配失败，请稍后重试');
          } finally {
            this.matching = false;
          }
        }
      });
    },
    
    processMatchResults(results) {
      // 将返回的匹配结果列表映射为视图模型
      if (!results || !Array.isArray(results)) {
        this.matchResults = [];
        return;
      }
      
      this.matchResults = results.map((result) => {
        const intention = result.intention || {};
        const score = result.score || 60;
        
        return {
          id: intention.userId, // 或者是 intention.id?
          productName: intention.realName || intention.userName || `推荐用户 ${intention.userId}`, // 显示真实姓名或用户名
          bankName: '优质合作伙伴', // 因为Intention没有角色字段，暂时固定显示，或者如果DTO里有role字段更好
          matchRate: score,
          // 下面的字段由于User实体没有，我们做展示处理或隐藏
          interestRate: '面议', 
          loanTime: '实时',
          amount: this.matchForm.amount, // 使用用户申请的金额作为参考
          term: this.matchForm.period === '6' ? 6 : (parseInt(this.matchForm.period) || 12)
        };
      });
    },
    formatAmount(amount) {
      if (!amount) return '-';
      return amount.toLocaleString('zh-CN') + ' 元';
    },
    goToApply(result) {
      // 跳转到申请页面
      localStorage.setItem('loanProduct', JSON.stringify(result));
      const productId = result.id || result.name;
      
      // 传递用户信息
      this.$router.push({
        path: `/home/loanApply/${productId}`,
        query: {
          applicantName: this.matchForm.applicantName,
          applicantPhone: this.matchForm.applicantPhone,
          amount: this.matchForm.amount,
          term: this.matchForm.period === '6' ? 6 : (parseInt(this.matchForm.period) || 12),
          purpose: this.matchForm.purpose
        }
      });
    },
    goToDetail(result) {
      // 跳转到产品详情页
      localStorage.setItem('loanProduct', JSON.stringify(result));
      const productId = result.id || result.name;
      this.$router.push(`/home/loanProductDetail/${productId}`);
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
  margin-bottom: 40px;
  padding: 30px 0;
  
  .page-title {
    font-size: 36px;
    color: #303133;
    margin: 0 0 12px 0;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    
    i {
      color: #67C23A;
      font-size: 40px;
    }
  }
  
  .page-subtitle {
    font-size: 16px;
    color: #909399;
    margin: 0;
  }
}

.match-form-section {
  margin-bottom: 30px;
  
  .form-card {
    border-radius: 12px;
    
    .card-header {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      display: flex;
      align-items: center;
      gap: 8px;
      
      i {
        color: #409EFF;
        font-size: 20px;
      }
    }
    
    .form-tip {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
    
    .form-actions {
      text-align: center;
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid #EBEEF5;
      
      .el-button {
        margin: 0 10px;
        padding: 12px 40px;
        font-size: 16px;
      }
    }
  }
}

.matching-section {
  margin-bottom: 30px;
  
  .matching-card {
    border-radius: 12px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    
    .matching-content {
      text-align: center;
      padding: 40px 20px;
      color: white;
      
      .matching-icon {
        font-size: 64px;
        margin-bottom: 20px;
        animation: rotate 2s linear infinite;
      }
      
      .matching-text {
        font-size: 18px;
        font-weight: 500;
        margin: 0 0 8px 0;
      }
      
      .matching-subtext {
        font-size: 14px;
        opacity: 0.9;
        margin: 0;
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.match-results-section {
  .results-card {
    border-radius: 12px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 8px;
        
        i {
          color: #67C23A;
          font-size: 20px;
        }
      }
      
      .close-btn {
        padding: 0;
        font-size: 18px;
        color: #909399;
        
        &:hover {
          color: #303133;
        }
      }
    }
    
    .results-content {
      .no-results {
        text-align: center;
        padding: 60px 20px;
        color: #909399;
        
        i {
          font-size: 64px;
          margin-bottom: 16px;
          display: block;
          color: #C0C4CC;
        }
        
        p {
          font-size: 16px;
          margin: 8px 0;
        }
        
        .no-results-tip {
          font-size: 14px;
          color: #C0C4CC;
        }
      }
      
      .results-list {
        .result-item {
          border: 2px solid #EBEEF5;
          border-radius: 12px;
          padding: 24px;
          margin-bottom: 20px;
          background-color: #fff;
          position: relative;
          transition: all 0.3s;
          
          &:hover {
            border-color: #409EFF;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
            transform: translateY(-2px);
          }
          
          &.top-match {
            border-color: #67C23A;
            background: linear-gradient(135deg, #f5fdf5 0%, #ffffff 100%);
            
            .result-badge {
              position: absolute;
              top: -12px;
              right: 24px;
              background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
              color: white;
              padding: 6px 16px;
              border-radius: 20px;
              font-size: 12px;
              font-weight: 600;
              display: flex;
              align-items: center;
              gap: 4px;
              box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
              
              i {
                font-size: 14px;
              }
            }
          }
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .result-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 20px;
            padding-bottom: 16px;
            border-bottom: 1px solid #EBEEF5;
            
            .header-info {
              flex: 1;
              
              h3 {
                margin: 0 0 8px 0;
                font-size: 20px;
                color: #303133;
                font-weight: 600;
              }
              
              .bank-name {
                color: #606266;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 4px;
                
                i {
                  color: #909399;
                }
              }
            }
            
            .match-rate {
              text-align: center;
              padding: 8px 16px;
              background: #F5F7FA;
              border-radius: 8px;
              
              .rate-value {
                font-size: 24px;
                font-weight: 700;
                color: #67C23A;
                line-height: 1;
              }
              
              .rate-label {
                font-size: 12px;
                color: #909399;
                margin-top: 4px;
              }
            }
          }
          
          .result-details {
            margin-bottom: 20px;
            
            .detail-row {
              display: grid;
              grid-template-columns: repeat(4, 1fr);
              gap: 16px;
              margin-bottom: 16px;
              
              .detail-item {
                display: flex;
                flex-direction: column;
                gap: 8px;
                
                .label {
                  color: #909399;
                  font-size: 13px;
                  display: flex;
                  align-items: center;
                  gap: 4px;
                  
                  i {
                    font-size: 14px;
                  }
                }
                
                .value {
                  color: #303133;
                  font-weight: 600;
                  font-size: 15px;
                  
                  &.highlight {
                    color: #E6A23C;
                    font-size: 16px;
                  }
                }
              }
            }
            
            .match-progress {
              display: flex;
              align-items: center;
              gap: 12px;
              padding: 12px;
              background: #F5F7FA;
              border-radius: 8px;
              
              .progress-label {
                font-size: 13px;
                color: #606266;
                min-width: 50px;
              }
              
              .el-progress {
                flex: 1;
              }
              
              .progress-value {
                font-size: 14px;
                font-weight: 600;
                color: #303133;
                min-width: 40px;
                text-align: right;
              }
            }
          }
          
          .result-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            padding-top: 16px;
            border-top: 1px solid #EBEEF5;
            
            .el-button {
              padding: 10px 24px;
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
    padding: 20px 0;
    
    .page-title {
      font-size: 28px;
      
      i {
        font-size: 32px;
      }
    }
  }
  
  .match-form-section {
    .form-card {
      .el-form {
        .el-row {
          .el-col {
            margin-bottom: 16px;
          }
        }
      }
      
      .form-actions {
        .el-button {
          width: 100%;
          margin: 5px 0;
        }
      }
    }
  }
  
  .match-results-section {
    .results-card {
      .results-content {
        .results-list {
          .result-item {
            padding: 16px;
            
            .result-header {
              flex-direction: column;
              align-items: flex-start;
              gap: 12px;
              
              .match-rate {
                align-self: flex-end;
              }
            }
            
            .result-details {
              .detail-row {
                grid-template-columns: repeat(2, 1fr);
                gap: 12px;
              }
            }
            
            .result-actions {
              flex-direction: column;
              
              .el-button {
                width: 100%;
              }
            }
          }
        }
      }
    }
  }
}
</style>
