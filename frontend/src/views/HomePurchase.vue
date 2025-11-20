<template>
  <div class="purchase-catalog">
    <el-backtop target=".home-content"></el-backtop>
    
    <!-- 顶部标题栏 -->
    <div class="catalog-header">
      <div class="header-title">产品采购目录</div>
      <el-button 
        type="success" 
        class="publish-btn"
        @click="handlePublish"
      >
        <i class="el-icon-plus"></i>
        我要发布求购信息
      </el-button>
    </div>

    <!-- 分类内容区域 -->
    <div class="catalog-content">
      <!-- 左列 -->
      <div class="catalog-column">
        <div class="category-section">
          <div class="category-title" @click="goToCategoryList({ key: 'vegetable', name: '蔬菜类' })">
            <span class="category-bullet">■</span>
            蔬菜类
          </div>
          <div class="subcategory-list">
            <div class="subcategory-item">
              <span class="subcategory-bullet">◆</span>
              <span class="subcategory-title">叶菜类</span>
              <span class="subcategory-items">：油菜、香菜、苋菜、莴苣、蕹菜、白菜、青梗菜、芥菜、芹菜、菠菜、菜薹、落葵、茼蒿、茴香、乌塌菜</span>
            </div>
            <div class="subcategory-item">
              <span class="subcategory-bullet">◆</span>
              <span class="subcategory-title">甘蓝类</span>
              <span class="subcategory-items">：甘蓝、花椰菜、芥蓝、青花菜</span>
            </div>
            <div class="subcategory-item">
              <span class="subcategory-bullet">◆</span>
              <span class="subcategory-title">根菜类</span>
              <span class="subcategory-items">：芜菁、胡萝卜、萝卜</span>
            </div>
            <div class="subcategory-item">
              <span class="subcategory-bullet">◆</span>
              <span class="subcategory-title">葱蒜类</span>
              <span class="subcategory-items">：洋葱、葱、大蒜、韭菜</span>
            </div>
            <div class="subcategory-item">
              <span class="subcategory-bullet">◆</span>
              <span class="subcategory-title">水生类</span>
              <span class="subcategory-items">：茭白、荸荠</span>
            </div>
            <div class="subcategory-item">
              <span class="subcategory-bullet">◆</span>
              <span class="subcategory-title">其他</span>
              <span class="subcategory-items">：紫苏、砧木、野生蔬菜、芽类蔬菜、香椿、草石蚕、朝鲜蓟、姜、牛蒡、食用菌</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右列 -->
      <div class="catalog-column">
        <div class="category-section">
          <div class="category-title" @click="goToCategoryList({ key: 'melon', name: '瓜类' })">
            <span class="category-bullet">■</span>
            瓜类
          </div>
          <div class="subcategory-list">
            <div class="subcategory-item">
              <span class="subcategory-items">西葫芦、西瓜、冬瓜、佛手瓜、葫芦、黄瓜、节瓜、苦瓜、木瓜、南瓜、丝瓜、甜瓜、蛇瓜</span>
            </div>
          </div>
        </div>

        <div class="category-section">
          <div class="category-title" @click="goToCategoryList({ key: 'bean', name: '豆类' })">
            <span class="category-bullet">■</span>
            豆类
          </div>
          <div class="subcategory-list">
            <div class="subcategory-item">
              <span class="subcategory-items">豌豆、菜豆、蚕豆、刀豆、豇豆、四棱豆</span>
            </div>
          </div>
        </div>

        <div class="category-section">
          <div class="category-title" @click="goToCategoryList({ key: 'solanaceous', name: '茄果类' })">
            <span class="category-bullet">■</span>
            茄果类
          </div>
          <div class="subcategory-list">
            <div class="subcategory-item">
              <span class="subcategory-items">番茄、辣椒、茄子、甜椒</span>
            </div>
          </div>
        </div>

        <div class="category-section">
          <div class="category-title" @click="goToCategoryList({ key: 'tuber', name: '薯芋类' })">
            <span class="category-bullet">■</span>
            薯芋类
          </div>
          <div class="subcategory-list">
            <div class="subcategory-item">
              <span class="subcategory-items">芋、甘薯、马铃薯、山药</span>
            </div>
          </div>
        </div>

        <div class="category-section">
          <div class="category-title" @click="goToCategoryList({ key: 'perennial', name: '多年生' })">
            <span class="category-bullet">■</span>
            多年生
          </div>
          <div class="subcategory-list">
            <div class="subcategory-item">
              <span class="subcategory-items">食用百合、菜用玉米、黄秋葵、芦笋、草莓、黄花菜、竹笋</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类点击区域 -->
    <div class="category-links">
      <div 
        v-for="category in categories" 
        :key="category.key"
        class="category-link-item"
        @click="goToCategoryList(category)"
      >
        <i :class="category.icon"></i>
        <span>{{ category.name }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomePurchase",
  data() {
    return {
      categories: [
        { key: 'vegetable', name: '蔬菜类', icon: 'el-icon-food' },
        { key: 'melon', name: '瓜类', icon: 'el-icon-grape' },
        { key: 'bean', name: '豆类', icon: 'el-icon-coffee-cup' },
        { key: 'solanaceous', name: '茄果类', icon: 'el-icon-cherry' },
        { key: 'tuber', name: '薯芋类', icon: 'el-icon-box' },
        { key: 'perennial', name: '多年生', icon: 'el-icon-menu' },
        { key: 'fruit', name: '水果类', icon: 'el-icon-grape' },
        { key: 'grain', name: '粮食类', icon: 'el-icon-coffee-cup' },
        { key: 'livestock', name: '畜牧类', icon: 'el-icon-cherry' },
        { key: 'other', name: '其他', icon: 'el-icon-box' }
      ]
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
  },
  methods: {
    handlePublish() {
      this.$router.push('/home/publishNeed').catch((err) => err);
    },
    goToCategoryList(category) {
      this.$router.push({
        path: '/home/purchaseList',
        query: { category: category.key, categoryName: category.name }
      }).catch((err) => err);
    }
  }
};
</script>

<style lang="less" scoped>
.purchase-catalog {
  width: 1100px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
  padding: 20px;

  .catalog-header {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    border-radius: 8px 8px 0 0;
    padding: 15px 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0;

    .header-title {
      color: #fff;
      font-size: 20px;
      font-weight: bold;
    }

    .publish-btn {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.5);
      color: #fff;
      padding: 8px 20px;
      font-size: 14px;
      border-radius: 4px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
        border-color: #fff;
      }

      i {
        margin-right: 5px;
      }
    }
  }

  .catalog-content {
    display: flex;
    gap: 30px;
    padding: 25px;
    background: #fff;
    border: 1px solid #e4e7ed;
    border-top: none;
    border-radius: 0 0 8px 8px;

    .catalog-column {
      flex: 1;

      .category-section {
        margin-bottom: 25px;

          .category-title {
            font-size: 16px;
            font-weight: bold;
            color: #333;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            cursor: pointer;
            transition: color 0.3s;
            
            &:hover {
              color: #67c23a;
            }

          .category-bullet {
            color: #67c23a;
            margin-right: 8px;
            font-size: 14px;
          }
        }

        .subcategory-list {
          .subcategory-item {
            margin-bottom: 10px;
            line-height: 1.8;
            color: #666;
            font-size: 14px;
            display: flex;
            align-items: flex-start;

            .subcategory-bullet {
              color: #67c23a;
              margin-right: 8px;
              flex-shrink: 0;
            }

            .subcategory-title {
              color: #333;
              font-weight: 500;
              margin-right: 5px;
              flex-shrink: 0;
            }

            .subcategory-items {
              flex: 1;
            }
          }
        }
      }
    }
  }

  .category-links {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
    margin-top: 30px;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;

    .category-link-item {
      display: flex;
      align-items: center;
      padding: 10px 20px;
      background: #fff;
      border: 1px solid #e4e7ed;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s;
      color: #606266;
      font-size: 14px;

      i {
        margin-right: 8px;
        color: #67c23a;
        font-size: 16px;
      }

      &:hover {
        border-color: #67c23a;
        color: #67c23a;
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(103, 194, 58, 0.2);
      }
    }
  }
}
</style>
