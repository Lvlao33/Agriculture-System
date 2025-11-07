<template>
  <div class="loan-product-page">
    <!-- Banner Section -->
    <div class="banner-section">
      <div class="banner-image">
        <!-- 图片位置 - 请在此处添加您的banner图片 -->
        <img src="/img/2.png" alt="Banner Image" class="banner-img" />
      </div>
    </div>

    <!-- Smart Match Button -->
    <div class="smart-match-section">
      <el-button type="success" class="smart-match-btn" @click="goToSmartMatch">
        <i class="el-icon-lightbulb"></i>
        智能匹配
      </el-button>
    </div>

    <!-- Loan Products List -->
    <div class="products-container">
      <div class="product-card" v-for="(product, index) in paginatedProducts" :key="index">
        <div class="product-header">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="bank-name">{{ product.bank }}</p>
        </div>
        
        <div class="product-tags">
          <span class="tag">材料简单</span>
          <span class="tag">审核便捷</span>
          <span class="tag">放款快</span>
        </div>

        <div class="product-details">
          <div class="detail-item">
            <span class="detail-label">最快放款</span>
            <span class="detail-value">{{ product.fastestDisbursement }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">申请额度(元)</span>
            <span class="detail-value">{{ product.amount.toLocaleString() }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">贷款期限(月)</span>
            <span class="detail-value">{{ product.term }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">参考利率</span>
            <span class="detail-value">年利率 {{ product.rate }}%</span>
          </div>
        </div>

        <div class="product-action">
          <el-button type="success" class="apply-btn">我要贷款</el-button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-section">
      <div class="pagination">
        <span class="page-btn" @click="prevPage" :class="{ disabled: currentPage === 1 }">&lt;</span>
        <span
          v-for="page in totalPages"
          :key="page"
          class="page-number"
          :class="{ active: currentPage === page }"
          @click="changePage(page)"
        >{{ page }}</span>
        <span class="page-btn" @click="nextPage" :class="{ disabled: currentPage === totalPages }">&gt;</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoanProductList',
  data() {
    return {
      loanProducts: [
        {
          name: '青易贷',
          bank: '青岛银行',
          amount: 100000,
          term: 24,
          rate: 1.1,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '农业贷',
          bank: '中国银行',
          amount: 100000,
          term: 36,
          rate: 1.2,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '助农贷',
          bank: '中国工商银行',
          amount: 150000,
          term: 30,
          rate: 1.1,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '阳光贷',
          bank: '日照银行',
          amount: 100000,
          term: 24,
          rate: 2.0,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '华夏贷',
          bank: '华夏银行',
          amount: 160000,
          term: 36,
          rate: 1.8,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '诚贷通',
          bank: '中国建设银行',
          amount: 200000,
          term: 36,
          rate: 3.6,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '点贷',
          bank: '浦发银行',
          amount: 150000,
          term: 24,
          rate: 1.1,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '数保贷',
          bank: '中国平安银行',
          amount: 160000,
          term: 36,
          rate: 1.2,
          fastestDisbursement: '3个工作日'
        },
        {
          name: '农贷通',
          bank: '中国民生银行',
          amount: 200000,
          term: 24,
          rate: 1.6,
          fastestDisbursement: '3个工作日'
        }
      ],
      currentPage: 1,
      pageSize: 4
    }
  },
  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.loanProducts.length / this.pageSize))
    },
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.loanProducts.slice(start, end)
    }
  },
  methods: {
    applyLoan(product) {
      // 处理贷款申请逻辑
      console.log('申请贷款:', product);
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage -= 1
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage += 1
      }
    },
    goToSmartMatch() {
      // 跳转到智能匹配页面
      this.$router.push('/home/smartMatchPage');
    }
  }
}
</script>

<style lang="less" scoped>
.loan-product-page {
  width: 100%;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.banner-section {
  width: 100%;
  height: 400px;
  position: relative;
  overflow: hidden;
  
  .banner-image {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    
    .banner-img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
}

.smart-match-section {
  width: 1100px;
  margin: 10px auto 0 auto;
  text-align: left;
  
  .smart-match-btn {
    background-color: #4CAF50;
    border-color: #4CAF50;
    padding: 10px 20px;
    font-size: 16px;
    border-radius: 999px;
    box-shadow: 0 6px 18px rgba(76, 175, 80, 0.25);
    
    i {
      margin-right: 5px;
    }
  }
}

.products-container {
  width: 1100px;
  margin: 12px auto 0 auto;
  padding: 0 0 20px 0;
}

.product-card {
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  }
  
  .product-header {
    flex: 1;
    min-width: 200px;
    
    .product-name {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin: 0 0 5px 0;
    }
    
    .bank-name {
      font-size: 14px;
      color: #666;
      margin: 0;
    }
  }
  
  .product-tags {
    flex: 1;
    display: flex;
    gap: 8px;
    margin: 0 20px;
    
    .tag {
      background-color: #4CAF50;
      color: white;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
    }
  }
  
  .product-details {
    flex: 2;
    display: flex;
    justify-content: space-between;
    margin: 0 20px;
    
    .detail-item {
      text-align: center;
      
      .detail-label {
        display: block;
        font-size: 12px;
        color: #666;
        margin-bottom: 4px;
      }
      
      .detail-value {
        display: block;
        font-size: 14px;
        font-weight: bold;
        color: #333;
      }
    }
  }
  
  .product-action {
    flex: 0 0 auto;
    
    .apply-btn {
      background-color: #4CAF50;
      border-color: #4CAF50;
      padding: 10px 20px;
      font-size: 14px;
      font-weight: bold;
      
      &:hover {
        background-color: #45a049;
        border-color: #45a049;
      }
    }
  }
}

.pagination-section {
  width: 1100px;
  margin: 30px auto;
  text-align: center;
  
  .pagination {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    
    .page-btn, .page-number {
      padding: 8px 12px;
      border: 1px solid #ddd;
      background-color: white;
      cursor: pointer;
      border-radius: 4px;
      font-size: 14px;
      
      &:hover {
        background-color: #f5f5f5;
      }
    }
    
    .page-number.active {
      background-color: #4CAF50;
      color: white;
      border-color: #4CAF50;
    }

    .page-btn.disabled {
      opacity: 0.5;
      cursor: not-allowed;
      pointer-events: none;
    }
  }
}

@media (max-width: 1200px) {
  .banner-section,
  .smart-match-section,
  .products-container,
  .pagination-section {
    width: 95%;
  }
  
  .product-card {
    flex-direction: column;
    align-items: flex-start;
    
    .product-tags {
      margin: 15px 0;
    }
    
    .product-details {
      margin: 15px 0;
      width: 100%;
    }
    
    .product-action {
      width: 100%;
      text-align: right;
    }
  }
}
</style>
