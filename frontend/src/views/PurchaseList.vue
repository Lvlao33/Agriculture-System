<template>
  <div class="purchase-list-page">
    <el-backtop target=".home-content"></el-backtop>
    
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">求购信息列表</h2>
        <span class="category-name" v-if="categoryName">{{ categoryName }}</span>
      </div>
      <el-button 
        type="success" 
        class="publish-btn"
        @click="handlePublish"
      >
        <i class="el-icon-plus"></i>
        我要发布求购信息
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="filters.location" placeholder="所在地区" style="width: 150px; margin-right: 10px;">
        <el-option label="所有城市" value="all"></el-option>
        <el-option label="北京" value="beijing"></el-option>
        <el-option label="上海" value="shanghai"></el-option>
        <el-option label="广州" value="guangzhou"></el-option>
        <el-option label="深圳" value="shenzhen"></el-option>
      </el-select>
      
      <el-select v-model="filters.member" placeholder="所有会员" style="width: 150px; margin-right: 10px;">
        <el-option label="全部会员" value="all"></el-option>
        <el-option label="个人" value="individual"></el-option>
        <el-option label="企业" value="enterprise"></el-option>
      </el-select>
      
      <el-select v-model="filters.updateTime" placeholder="所有更新时间" style="width: 150px; margin-right: 10px;">
        <el-option label="全部时间" value="all"></el-option>
        <el-option label="今天" value="today"></el-option>
        <el-option label="本周" value="week"></el-option>
        <el-option label="本月" value="month"></el-option>
      </el-select>

      <el-checkbox v-model="filters.mergeSupplier" style="margin-right: 20px;">
        合并相同供应商
      </el-checkbox>

      <el-input 
        v-model="searchValue" 
        placeholder="搜索求购信息..."
        style="width: 250px;"
        clearable
        @keyup.enter.native="handleSearch"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
      </el-input>
    </div>

    <!-- 求购信息列表 -->
    <div class="needs-list">
      <div 
        v-for="(item, index) in filteredNeeds" 
        :key="index"
        class="needs-item"
        @click="goToDetail(item.orderId)"
      >
        <div class="item-image">
          <img 
            v-if="item.picture && item.picture !== ''" 
            :src="`/order/${item.picture}`" 
            alt="商品图片"
          />
          <div v-else class="no-image">
            <i class="el-icon-picture"></i>
            <span>暂无图片</span>
          </div>
        </div>
        <div class="item-content">
          <div class="item-header">
            <h3 class="item-title">{{ item.title || item.content || '求购信息' }}</h3>
            <span class="item-date">[{{ formatDate(item.createTime) }}]</span>
          </div>
          <p class="item-description">{{ item.content || '暂无描述' }}</p>
          <div class="item-footer">
            <span class="item-poster">{{ item.ownName ? item.ownName : '个人' }}</span>
            <div class="item-actions">
              <span class="action-item" @click.stop="handleMessage(item)">
                <i class="el-icon-edit"></i>
                留言咨询
              </span>
              <span class="action-item" @click.stop="handleContact(item)">
                <i class="el-icon-phone"></i>
                联系方式
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="filteredNeeds.length === 0" class="empty-state">
        <i class="el-icon-box"></i>
        <p>暂无求购信息</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        background
      />
    </div>
  </div>
</template>

<script>
import { selectNeedsPage } from "../api/order";

export default {
  name: "PurchaseList",
  data() {
    return {
      needs: [],
      filteredNeeds: [],
      searchValue: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      categoryName: '',
      filters: {
        location: 'all',
        member: 'all',
        updateTime: 'all',
        mergeSupplier: false
      }
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
    this.categoryName = this.$route.query.categoryName || '';
    this.loadNeeds();
  },
  watch: {
    '$route'(to, from) {
      this.categoryName = to.query.categoryName || '';
      this.loadNeeds();
    },
    filters: {
      deep: true,
      handler() {
        this.filterNeeds();
      }
    },
    searchValue() {
      this.filterNeeds();
    }
  },
  methods: {
    loadNeeds() {
      selectNeedsPage({
        pageNum: this.currentPage,
        keys: this.searchValue || ''
      }).then((res) => {
        if (res.flag == true) {
          this.needs = res.data.list || [];
          this.total = res.data.total || 0;
          this.filterNeeds();
        }
      }).catch((err) => {
        console.error('加载求购信息失败', err);
      });
    },
    filterNeeds() {
      let filtered = [...this.needs];

      // 按分类筛选（如果后端支持，可以在这里添加）
      // if (this.$route.query.category) {
      //   filtered = filtered.filter(item => item.category === this.$route.query.category);
      // }

      // 按会员类型筛选
      if (this.filters.member !== 'all') {
        // 这里可以根据实际数据结构调整
      }

      // 按更新时间筛选
      if (this.filters.updateTime !== 'all') {
        const now = new Date();
        filtered = filtered.filter(item => {
          const updateTime = new Date(item.updateTime || item.createTime);
          const diff = now - updateTime;
          const days = diff / (1000 * 60 * 60 * 24);
          
          switch (this.filters.updateTime) {
            case 'today':
              return days < 1;
            case 'week':
              return days < 7;
            case 'month':
              return days < 30;
            default:
              return true;
          }
        });
      }

      this.filteredNeeds = filtered;
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadNeeds();
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.loadNeeds();
    },
    handlePublish() {
      this.$router.push('/home/publishNeed').catch((err) => err);
    },
    goToDetail(orderId) {
      this.$store.commit("updateOrderId", orderId);
      this.$router.push(`/home/purchaseDetails?orderId=${orderId}`).catch((err) => err);
    },
    handleMessage(item) {
      // 留言咨询功能
      this.$message.info('留言功能开发中');
    },
    handleContact(item) {
      // 显示联系方式
      if (item.contact) {
        this.$alert(`联系方式：${item.contact}`, '联系方式', {
          confirmButtonText: '确定'
        });
      } else {
        this.$message.warning('该用户未提供联系方式');
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}/${month}/${day}`;
    }
  }
};
</script>

<style lang="less" scoped>
.purchase-list-page {
  width: 1100px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f0f0f0;

    .header-left {
      display: flex;
      align-items: center;
      gap: 15px;

      .page-title {
        font-size: 24px;
        color: #333;
        margin: 0;
      }

      .category-name {
        color: #67c23a;
        font-size: 16px;
        padding: 4px 12px;
        background: #f0f9ff;
        border-radius: 4px;
      }
    }

    .publish-btn {
      background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      border: none;
      color: #fff;
      padding: 10px 20px;
      font-size: 14px;
      border-radius: 6px;

      i {
        margin-right: 5px;
      }
    }
  }

  .filter-bar {
    display: flex;
    align-items: center;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 6px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 10px;
  }

  .needs-list {
    .needs-item {
      display: flex;
      padding: 20px;
      margin-bottom: 15px;
      background: #f9f9f9;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #f0f9ff;
        transform: translateX(5px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .item-image {
        width: 120px;
        height: 120px;
        flex-shrink: 0;
        margin-right: 20px;
        border-radius: 6px;
        overflow: hidden;
        background: #e4e7ed;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .no-image {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: #909399;
          background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);

          i {
            font-size: 32px;
            margin-bottom: 5px;
          }

          span {
            font-size: 12px;
          }
        }
      }

      .item-content {
        flex: 1;
        display: flex;
        flex-direction: column;

        .item-header {
          display: flex;
          align-items: center;
          margin-bottom: 10px;

          .item-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin: 0 15px 0 0;
            flex: 1;
          }

          .item-date {
            color: #909399;
            font-size: 14px;
          }
        }

        .item-description {
          color: #606266;
          font-size: 14px;
          line-height: 1.6;
          margin: 0 0 15px 0;
          flex: 1;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .item-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .item-poster {
            color: #909399;
            font-size: 13px;
          }

          .item-actions {
            display: flex;
            gap: 20px;

            .action-item {
              color: #409eff;
              font-size: 13px;
              cursor: pointer;
              display: flex;
              align-items: center;
              transition: color 0.3s;

              i {
                margin-right: 5px;
              }

              &:hover {
                color: #67c23a;
              }
            }
          }
        }
      }
    }

    .empty-state {
      text-align: center;
      padding: 60px 20px;
      color: #999;

      i {
        font-size: 64px;
        margin-bottom: 20px;
        display: block;
      }

      p {
        font-size: 16px;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    padding: 20px 0;
  }
}
</style>

