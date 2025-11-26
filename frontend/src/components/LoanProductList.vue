<template>
  <div class="loan-product-page">
    <!-- Banner Section -->
    <div class="banner-section">
      <div class="carousel-container">
        <div class="carousel-wrapper">
          <div
            v-for="(img, index) in bannerImages"
            :key="index"
            class="carousel-item"
            :class="{
              'active': index === currentBannerIndex,
              'prev': index === getPrevIndex(),
              'next': index === getNextIndex()
            }"
            :style="getItemStyle(index)"
            @click="goToBanner(index)"
          >
            <div class="banner-image">
              <img :src="img.src" :alt="img.alt" class="banner-img" />
            </div>
          </div>
        </div>
        <div class="carousel-controls">
          <button class="carousel-btn prev-btn" @click="prevBanner">
            <i class="el-icon-arrow-left"></i>
          </button>
          <button class="carousel-btn next-btn" @click="nextBanner">
            <i class="el-icon-arrow-right"></i>
          </button>
        </div>
        <div class="carousel-indicators">
          <span
            v-for="(img, index) in bannerImages"
            :key="index"
            class="indicator"
            :class="{ active: index === currentBannerIndex }"
            @click="goToBanner(index)"
          ></span>
        </div>
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
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading"></i>
        <span>加载中...</span>
      </div>
      <div v-else-if="loanProducts.length === 0" class="empty-container">
        <p>暂无产品数据</p>
      </div>
      <div v-else class="product-card" v-for="(product, index) in paginatedProducts" :key="index" @click="goToProductDetail(product)">
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

        <div class="product-action" @click.stop>
          <el-button type="success" class="apply-btn" @click="goToApply(product)">我要贷款</el-button>
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
import { getLoanProducts } from '../api/finance'

export default {
  name: 'LoanProductList',
  data() {
    return {
      loanProducts: [],
      currentPage: 1,
      pageSize: 4,
      loading: false,
      bannerImages: [
        { src: '/img/2.png', alt: '贷款服务Banner 1' },
        { src: '/img/2.png', alt: '贷款服务Banner 2' },
        { src: '/img/2.png', alt: '贷款服务Banner 3' }
      ],
      currentBannerIndex: 0,
      bannerTimer: null
    }
  },
  mounted() {
    this.loadProducts()
    this.startBannerAutoPlay()
  },
  beforeDestroy() {
    this.stopBannerAutoPlay()
  },
  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.loanProducts.length / this.pageSize))
    },
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.loanProducts.slice(start, end)
    },
  },
  methods: {
    async loadProducts() {
      this.loading = true
      try {
        const response = await getLoanProducts()
        // 处理响应数据，确保数据结构正确
        let products = []
        if (response && Array.isArray(response)) {
          this.loanProducts = response
        } else if (response && response.data && Array.isArray(response.data)) {
          this.loanProducts = response.data
        } else if (response && response.list && Array.isArray(response.list)) {
          this.loanProducts = response.list
        } else {
          console.warn('产品列表数据格式不正确:', response)
          products = []
        }
      } catch (error) {
        console.error('加载产品列表失败:', error)
        this.$message.error('加载产品列表失败，请稍后重试')
        this.loanProducts = []
      } finally {
        this.loading = false
      }
    },
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
    },
    goToProductDetail(product) {
      // 跳转到产品详情页
      localStorage.setItem('loanProduct', JSON.stringify(product));
      const productId = product.id || product.name;
      this.$router.push(`/home/loanProductDetail/${productId}`);
    },
    goToApply(product) {
      // 跳转到申请页面
      localStorage.setItem('loanProduct', JSON.stringify(product));
      const productId = product.id || product.name;
      this.$router.push(`/home/loanApply/${productId}`);
    },
    getPrevIndex() {
      const prev = this.currentBannerIndex - 1
      return prev < 0 ? this.bannerImages.length - 1 : prev
    },
    getNextIndex() {
      const next = this.currentBannerIndex + 1
      return next >= this.bannerImages.length ? 0 : next
    },
    prevBanner() {
      this.stopBannerAutoPlay()
      this.currentBannerIndex = this.getPrevIndex()
      this.startBannerAutoPlay()
    },
    nextBanner() {
      this.stopBannerAutoPlay()
      this.currentBannerIndex = this.getNextIndex()
      this.startBannerAutoPlay()
    },
    goToBanner(index) {
      this.stopBannerAutoPlay()
      this.currentBannerIndex = index
      this.startBannerAutoPlay()
    },
    getItemStyle(index) {
      const itemWidth = 1100
      const gap = 20
      const activeIndex = this.currentBannerIndex
      
      // 计算相对于当前激活项的偏移
      let offset = index - activeIndex
      
      // 处理循环：确保只显示当前、前一张、后一张
      if (offset > 1) {
        offset = offset - this.bannerImages.length
      }
      if (offset < -1) {
        offset = offset + this.bannerImages.length
      }
      
      // 只显示当前、前一张、后一张（offset: -1, 0, 1）
      // 计算X位置：中间为0，左侧为负，右侧为正
      const x = offset * (itemWidth + gap)
      
      return {
        transform: `translateX(${x}px)`,
        zIndex: index === activeIndex ? 3 : (Math.abs(offset) === 1 ? 2 : 1)
      }
    },
    startBannerAutoPlay() {
      this.bannerTimer = setInterval(() => {
        this.currentBannerIndex = this.getNextIndex()
      }, 4000)
    },
    stopBannerAutoPlay() {
      if (this.bannerTimer) {
        clearInterval(this.bannerTimer)
        this.bannerTimer = null
      }
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
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0;
  
  .carousel-container {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: visible;
    
    .carousel-wrapper {
      width: 100%;
      height: 100%;
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .carousel-item {
      width: 1100px;
      height: 400px;
      position: absolute;
      left: 50%;
      top: 50%;
      margin-left: -550px; // 宽度的一半，用于居中
      margin-top: -200px; // 高度的一半，用于居中
      cursor: pointer;
      transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
      
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
          transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
        }
      }
      
      // 当前激活的图片 - 正常大小，居中
      &.active {
        opacity: 1;
        
        .banner-img {
          transform: scale(1);
        }
      }
      
      // 前一张和后一张图片 - 缩小并偏移
      &.prev,
      &.next {
        opacity: 0.75;
        
        .banner-img {
          transform: scale(0.75);
        }
      }
      
      // 其他图片 - 隐藏
      &:not(.active):not(.prev):not(.next) {
        opacity: 0;
        pointer-events: none;
      }
    }
    
    .carousel-controls {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      width: 100%;
      display: flex;
      justify-content: space-between;
      padding: 0 20px;
      z-index: 10;
      pointer-events: none;
      
      .carousel-btn {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.9);
        border: 1px solid #e0e0e0;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s;
        pointer-events: all;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        
        i {
          font-size: 20px;
          color: #333;
        }
        
        &:hover {
          background-color: #fff;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
          transform: scale(1.1);
        }
        
        &.prev-btn {
          left: 20px;
        }
        
        &.next-btn {
          right: 20px;
        }
      }
    }
    
    .carousel-indicators {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 10px;
      z-index: 10;
      
      .indicator {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.5);
        cursor: pointer;
        transition: all 0.3s;
        
        &.active {
          background-color: #4CAF50;
          width: 24px;
          border-radius: 5px;
        }
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }
}

.smart-match-section {
  width: 1100px;
  margin: 0 auto 0 auto;
  text-align: right;
  padding-bottom: 0;
  
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
  margin: -10px auto 0 auto;
  padding: 0 0 20px 0;
  
  .loading-container {
    text-align: center;
    padding: 40px;
    color: #666;
    
    i {
      font-size: 24px;
      margin-right: 10px;
    }
  }
  
  .empty-container {
    text-align: center;
    padding: 40px;
    color: #999;
    
    p {
      margin: 0;
      font-size: 16px;
    }
  }
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
  cursor: pointer;
  
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
