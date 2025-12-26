<template>
  <div class="loan-product-detail">
  <div class="detail-container">
      <!-- 返回按钮 -->
      <div class="top-actions">
        <el-button class="back-button" type="text" icon="el-icon-arrow-left" @click="goBack">返回</el-button>
      </div>
      <!-- 产品基本信息 -->
      <div class="product-info-section">
        <div class="section-header">
          <h2>{{ product.name }}</h2>
          <p class="bank-name">{{ product.bank }}</p>
        </div>
        
        <div class="product-details-grid">
          <div class="detail-card">
            <div class="detail-label">申请额度</div>
            <div class="detail-value">{{ product.amount ? product.amount.toLocaleString() : '0' }} 元</div>
          </div>
          <div class="detail-card">
            <div class="detail-label">贷款期限</div>
            <div class="detail-value">{{ product.term }} 个月</div>
          </div>
          <div class="detail-card">
            <div class="detail-label">参考利率</div>
            <div class="detail-value">年利率 {{ product.rate }}%</div>
          </div>
          <div class="detail-card">
            <div class="detail-label">最快放款</div>
            <div class="detail-value">{{ product.fastestDisbursement || '3个工作日' }}</div>
          </div>
        </div>

        <div class="product-tags">
          <span class="tag">材料简单</span>
          <span class="tag">审核便捷</span>
          <span class="tag">放款快</span>
        </div>
      </div>

      <!-- 产品详细说明 -->
      <div class="product-description-section">
        <h3>产品介绍</h3>
        <div class="description-content">
          <p>{{ product.description || '该产品专为农户设计，提供便捷的融资服务，支持快速审批和放款。' }}</p>
        </div>
      </div>

      <!-- 申请材料说明 -->
      <div class="materials-section">
        <h3>申请材料说明</h3>
        <div class="materials-content">
          <div class="material-item" v-for="(material, index) in requiredMaterials" :key="index">
            <div class="material-header">
              <i class="el-icon-document"></i>
              <span class="material-name">{{ material.name }}</span>
            </div>
            <div class="material-description">{{ material.description }}</div>
            <div class="material-actions" v-if="material.templateUrl">
              <el-button type="text" @click="downloadTemplate(material.templateUrl)">
                <i class="el-icon-download"></i> 下载模板
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="contactManager">
          <i class="el-icon-service"></i> 联系负责人
        </el-button>
        <el-button type="success" size="large" @click="goToApply">
          <i class="el-icon-edit"></i> 我要贷款
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoanProductDetail',
  data() {
    return {
      product: {},
      requiredMaterials: [
        {
          name: '身份证扫描件',
          description: '请提供本人有效身份证正反面扫描件，要求清晰可见，文件格式为JPG或PDF，大小不超过5MB。',
          templateUrl: null
        },
        {
          name: '营业执照',
          description: '如为个体工商户或企业，需提供营业执照扫描件。',
          templateUrl: '/templates/business_license_template.pdf'
        },
        {
          name: '银行流水',
          description: '提供近6个月的银行流水记录，用于评估还款能力。',
          templateUrl: null
        },
        {
          name: '贷款申请表',
          description: '填写完整的贷款申请表，请下载模板后按要求填写。',
          templateUrl: '/templates/loan_application_form.docx'
        },
        {
          name: '收入证明',
          description: '提供收入证明文件，可以是工资单、收入证明或其他能证明收入来源的文件。',
          templateUrl: null
        }
      ]
    }
  },
  mounted() {
    this.loadProduct();
  },
  methods: {
    loadProduct() {
      // 从localStorage获取产品信息
      const productStr = localStorage.getItem('loanProduct');
      if (productStr) {
        this.product = JSON.parse(productStr);
      } else {
        // 如果没有，尝试从路由参数获取
        const productId = this.$route.params.id;
        // 这里可以调用API获取产品详情
        this.$message.warning('未找到产品信息');
        this.$router.push('/home/smartMatch');
      }
    },
    downloadTemplate(url) {
      // 下载模板文件
      if (url) {
        window.open(url, '_blank');
      } else {
        this.$message.info('模板文件暂未提供');
      }
    },
    contactManager() {
      // 跳转到聊天界面
      // 假设负责人ID为 product.managerId 或 product.handledBy
      const managerId = this.product.managerId || this.product.handledBy || 'default_manager';
      this.$router.push(`/home/chat/${managerId}?productId=${this.product.id || this.product.name}`);
    },
    goToApply() {
      // 跳转到申请页面
      const productId = this.product.id || this.product.name;
      this.$router.push(`/home/loanApply/${productId}`);
    }
    ,
    goBack() {
      // 优先使用历史后退，回退不可用时兜底到贷款列表页
      if (window.history && window.history.length > 1) {
        this.$router.back();
      } else {
        this.$router.push('/home/finance/loans');
      }
    }
  }
}
</script>

<style lang="less" scoped>
.loan-product-detail {
  width: 100%;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20px 0;
}

.detail-container {
  width: 1100px;
  margin: 0 auto;
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 返回按钮样式 */
.top-actions {
  margin-bottom: 8px;
}

.back-button {
  background-color: #4CAF50;
  border-color: #4CAF50;
  color: white;
  font-size: 14px;
  padding: 8px 16px;

  &:hover {
    background-color: #45a049;
    border-color: #45a049;
  }
}

.product-info-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e0e0e0;

  .section-header {
    margin-bottom: 20px;

    h2 {
      font-size: 28px;
      color: #333;
      margin: 0 0 10px 0;
    }

    .bank-name {
      font-size: 16px;
      color: #666;
      margin: 0;
    }
  }

  .product-details-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;

    .detail-card {
      background-color: #f9f9f9;
      border-radius: 8px;
      padding: 20px;
      text-align: center;

      .detail-label {
        font-size: 14px;
        color: #666;
        margin-bottom: 10px;
      }

      .detail-value {
        font-size: 20px;
        font-weight: bold;
        color: #4CAF50;
      }
    }
  }

  .product-tags {
    display: flex;
    gap: 10px;

    .tag {
      background-color: #4CAF50;
      color: white;
      padding: 6px 12px;
      border-radius: 16px;
      font-size: 14px;
    }
  }
}

.product-description-section,
.materials-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e0e0e0;

  h3 {
    font-size: 20px;
    color: #333;
    margin-bottom: 20px;
  }

  .description-content {
    font-size: 16px;
    line-height: 1.8;
    color: #666;
  }
}

.materials-content {
  .material-item {
    background-color: #f9f9f9;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 15px;

    .material-header {
      display: flex;
      align-items: center;
      margin-bottom: 10px;

      i {
        font-size: 20px;
        color: #4CAF50;
        margin-right: 10px;
      }

      .material-name {
        font-size: 18px;
        font-weight: bold;
        color: #333;
      }
    }

    .material-description {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      margin-bottom: 10px;
    }

    .material-actions {
      margin-top: 10px;
    }
  }
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;

  .el-button {
    padding: 12px 40px;
    font-size: 16px;

    i {
      margin-right: 5px;
    }
  }
}

@media (max-width: 1200px) {
  .detail-container {
    width: 95%;
  }

  .product-details-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .product-details-grid {
    grid-template-columns: 1fr !important;
  }

  .action-buttons {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }
}
</style>

