<template>
  <div class="goods-box">
    <el-backtop target=".home-content"></el-backtop>

    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="search-section">
        <el-input
          v-model="searchValue"
          maxlength="100"
          clearable
          placeholder="搜索商品..."
          style="width: 300px;"
          @keyup.enter.native="handleSearch"
        />
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
      </div>
      <el-button
        type="success"
        icon="el-icon-plus"
        class="publish-btn"
        @click="handlePublish"
      >
        发布货源
      </el-button>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧分类栏 -->
      <div class="category-sidebar">
        <div class="category-title">商品分类</div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'all' }"
          @click="selectCategory('all')"
        >
          <i class="el-icon-menu"></i>
          <span>全部商品</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'fruit' }"
          @click="selectCategory('fruit')"
        >
          <i class="el-icon-grape"></i>
          <span>水果类</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'vegetable' }"
          @click="selectCategory('vegetable')"
        >
          <i class="el-icon-food"></i>
          <span>蔬菜类</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'grain' }"
          @click="selectCategory('grain')"
        >
          <i class="el-icon-coffee-cup"></i>
          <span>粮食类</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'livestock' }"
          @click="selectCategory('livestock')"
        >
          <i class="el-icon-cherry"></i>
          <span>畜牧类</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'other' }"
          @click="selectCategory('other')"
        >
          <i class="el-icon-box"></i>
          <span>其他</span>
        </div>
      </div>

      <!-- 右侧商品展示区 -->
      <div class="goods-display">
        <div v-if="filteredGoods.length === 0" class="empty-state">
          <i class="el-icon-box"></i>
          <p>暂无商品</p>
        </div>
        <div
          v-for="(item, index) in filteredGoods"
          :key="index"
          class="goods-card"
          @click="detailsClick(item.orderId)"
        >
          <el-card shadow="hover" :body-style="{ padding: '0' }">
            <div class="goods-image-wrapper">
              <img
                v-if="item.picture && item.picture !== ''"
                :src="`/order/${item.picture}`"
                class="goods-img"
                alt="商品图片"
              />
              <img
                v-else
                :src="`/order/wutu.gif`"
                class="goods-img"
                alt="暂无图片"
              />
            </div>
            <div class="goods-info">
              <div class="goods-owner">
                <i class="el-icon-user"></i>
                <span>{{ item.ownName || '未知' }}</span>
              </div>
              <p class="goods-content">{{ item.content || '暂无描述' }}</p>
              <div class="goods-footer">
                <span class="goods-price" v-if="item.price">
                  <i class="el-icon-coin"></i>
                  ¥{{ item.price }}
                </span>
                <span class="goods-price" v-else>价格面议</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 分类详细内容 -->
    <div class="category-showcase">
      <div class="showcase-header">
        <h3>分类精选</h3>
        <p>为每个品类提供产地、规格、供货节奏等详细信息，帮助您快速匹配高质量货源。</p>
      </div>
      <div class="showcase-grid">
        <div
          class="showcase-card"
          v-for="category in categoryShowcase"
          :key="category.key"
        >
          <div class="showcase-top">
            <div class="showcase-info">
              <h4>{{ category.title }}</h4>
              <p>{{ category.description }}</p>
            </div>
            <img :src="category.image" :alt="category.title" />
          </div>
          <ul class="feature-list">
            <li v-for="(feature, index) in category.features" :key="index">
              <i class="el-icon-check"></i>
              <span>{{ feature }}</span>
            </li>
          </ul>
          <div class="sample-goods">
            <span
              v-for="sample in category.sampleGoods"
              :key="sample.name"
              @click="handleCategoryShowcase(sample.keyword, category.key)"
            >
              {{ sample.name }}
            </span>
          </div>
          <el-button
            type="primary"
            size="mini"
            @click="handleCategoryShowcase(category.searchKeyword, category.key)"
          >
            查看{{ category.title }}货源
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "GoodsSource",
  data() {
    return {
      searchValue: '',
      selectedCategory: 'all',
      categoryMap: {
        'all': '全部商品',
        'fruit': '水果类',
        'vegetable': '蔬菜类',
        'grain': '粮食类',
        'livestock': '畜牧类',
        'other': '其他'
      },
      categoryShowcase: [
        {
          key: 'vegetable',
          title: '蔬菜类',
          description: '基地直供叶菜、根茎类，每日凌晨采摘，4小时内完成预冷打包。',
          image: '/kn/pro1.jpg',
          features: ['日采日配 冷链配送', '可提供检测报告', '支持分级分拣'],
          sampleGoods: [
            { name: '有机生菜', keyword: '生菜' },
            { name: '西兰花', keyword: '西兰花' },
            { name: '洪湖莲藕', keyword: '莲藕' }
          ],
          searchKeyword: '蔬菜'
        },
        {
          key: 'fruit',
          title: '水果类',
          description: '从华南、川渝、云南等核心产区直发，甜度、大小均可定制。',
          image: '/kn/pro2.jpg',
          features: ['原产地控货', '破损包赔', '提供空运/冷链双模式'],
          sampleGoods: [
            { name: '赣南脐橙', keyword: '脐橙' },
            { name: '阳山水蜜桃', keyword: '水蜜桃' },
            { name: '妃子笑荔枝', keyword: '荔枝' }
          ],
          searchKeyword: '水果'
        },
        {
          key: 'grain',
          title: '粮食类',
          description: '东北优质寒地稻、优选主体合作社，支持代精加工和包装。',
          image: '/kn/rice.png',
          features: ['产地可追溯', '仓配一体化', '支持金融结算'],
          sampleGoods: [
            { name: '五常稻花香', keyword: '稻花香' },
            { name: '高筋小麦', keyword: '小麦' },
            { name: '有机黄小米', keyword: '小米' }
          ],
          searchKeyword: '粮食'
        },
        {
          key: 'livestock',
          title: '畜牧类',
          description: '覆盖肉牛、肉羊、家禽禽蛋等，屠宰加工和冷链运输一条龙服务。',
          image: '/kn/pro3.jpg',
          features: ['定点屠宰', '全程溯源', '可出具检疫证明'],
          sampleGoods: [
            { name: '草原西门塔尔牛', keyword: '西门塔尔' },
            { name: '散养山地鸡', keyword: '土鸡' },
            { name: '有机鲜蛋', keyword: '鲜蛋' }
          ],
          searchKeyword: '畜牧'
        },
        {
          key: 'other',
          title: '特色与深加工',
          description: '蜂蜜、茶叶、中药材、预制菜等特色货源，支持OEM/ODM。',
          image: '/kn/chayangji.jpg',
          features: ['源头工厂', '资质齐全', '配方可定制'],
          sampleGoods: [
            { name: '古树红茶', keyword: '红茶' },
            { name: '预制酸菜鱼', keyword: '预制菜' },
            { name: '道地黄芪', keyword: '黄芪' }
          ],
          searchKeyword: '特色'
        }
      ]
    };
  },
  props: {
    cgoods: {
      type: Array,
      default: () => []
    },
  },
  computed: {
    filteredGoods() {
      let goods = this.cgoods || [];
      
      // 鎸夊垎绫荤瓫閫夛紙杩欓噷鍙互鏍规嵁瀹為檯鏁版嵁缁撴瀯璋冩暣锛�
      if (this.selectedCategory !== 'all') {
        // 濡傛灉鍟嗗搧鏁版嵁涓湁鍒嗙被瀛楁锛屽彲浠ュ湪杩欓噷杩涜绛涢€�
        // goods = goods.filter(item => item.category === this.selectedCategory);
      }
      
      // 鎸夋悳绱㈠叧閿瘝绛涢€�
      if (this.searchValue) {
        const keyword = this.searchValue.toLowerCase();
        goods = goods.filter(item => {
          return (item.content && item.content.toLowerCase().includes(keyword)) ||
                 (item.ownName && item.ownName.toLowerCase().includes(keyword));
        });
      }
      
      return goods;
    }
  },
  methods: {
    selectCategory(category) {
      this.selectedCategory = category;
    },
    handleSearch() {
      this.$emit('handleSearch', this.searchValue);
    },
    handlePublish() {
      this.$router.push('/home/publishSupply').catch((err) => err);
    },
    detailsClick(orderId) {
      this.$store.commit("updateOrderId", orderId);
      this.$router.push(`/home/details?orderId=${orderId}`).catch((err) => err);
    },
    handleCategoryShowcase(keyword, categoryKey) {
      if (categoryKey) {
        this.selectedCategory = categoryKey;
      }
      if (keyword) {
        this.searchValue = keyword;
        // 触发父组件搜索，确保商品列表切换到对应货源
        this.handleSearch();
      }
    }
  }
}
</script>

<style lang="less" scoped>
.goods-box {
  background-color: #f5f5f5;
  width: 1100px;
  margin: 0 auto;
  padding: 20px;
  min-height: 600px;

  .top-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: white;
    padding: 15px 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .search-section {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .publish-btn {
      background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      border: none;
      padding: 10px 20px;
      font-size: 16px;
      font-weight: 500;
      border-radius: 6px;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(103, 194, 58, 0.3);
      }
    }
  }

  .main-content {
    display: flex;
    gap: 20px;

    .category-sidebar {
      width: 200px;
      background-color: white;
      border-radius: 8px;
      padding: 20px 0;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      height: fit-content;
      position: sticky;
      top: 20px;

      .category-title {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        padding: 0 20px 15px;
        border-bottom: 2px solid #f0f0f0;
        margin-bottom: 10px;
      }

      .category-item {
        display: flex;
        align-items: center;
        padding: 12px 20px;
        cursor: pointer;
        transition: all 0.3s;
        color: #666;
        font-size: 14px;

        i {
          margin-right: 10px;
          font-size: 16px;
        }

        &:hover {
          background-color: #f5f7fa;
          color: #409eff;
        }

        &.active {
          background-color: #ecf5ff;
          color: #409eff;
          border-left: 3px solid #409eff;
          font-weight: 500;
        }
      }
    }

    .goods-display {
      flex: 1;
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 20px;

      .empty-state {
        grid-column: 1 / -1;
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

      .goods-card {
        cursor: pointer;
        transition: transform 0.3s, box-shadow 0.3s;

        &:hover {
          transform: translateY(-5px);
        }

        .el-card {
          border-radius: 8px;
          overflow: hidden;
          height: 100%;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          }
        }

        .goods-image-wrapper {
          width: 100%;
          height: 200px;
          overflow: hidden;
          background-color: #f5f5f5;
          display: flex;
          align-items: center;
          justify-content: center;

          .goods-img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s;
          }
        }

        .goods-card:hover .goods-img {
          transform: scale(1.05);
        }

        .goods-info {
          padding: 15px;

          .goods-owner {
            display: flex;
            align-items: center;
            color: #666;
            font-size: 12px;
            margin-bottom: 10px;

            i {
              margin-right: 5px;
              color: #909399;
            }
          }

          .goods-content {
            font-size: 14px;
            color: #333;
            line-height: 1.5;
            margin: 0 0 10px 0;
            height: 42px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .goods-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 10px;
            border-top: 1px solid #f0f0f0;

            .goods-price {
              color: #f56c6c;
              font-size: 18px;
              font-weight: bold;
              display: flex;
              align-items: center;

              i {
                margin-right: 5px;
                font-size: 16px;
              }
            }
          }
        }
      }
    }
  }

  .category-showcase {
    margin-top: 30px;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);

    .showcase-header {
      border-left: 4px solid #67c23a;
      padding-left: 12px;
      margin-bottom: 20px;

      h3 {
        margin: 0;
        font-size: 20px;
        color: #303133;
      }

      p {
        margin: 6px 0 0 0;
        color: #909399;
        font-size: 13px;
      }
    }

    .showcase-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
      gap: 20px;

      .showcase-card {
        border: 1px solid #f0f0f0;
        border-radius: 8px;
        padding: 18px;
        display: flex;
        flex-direction: column;
        gap: 12px;
        transition: box-shadow 0.3s;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .showcase-top {
          display: flex;
          justify-content: space-between;
          align-items: center;
          gap: 10px;

          .showcase-info {
            flex: 1;

            h4 {
              margin: 0 0 6px 0;
              font-size: 18px;
              color: #303133;
            }

            p {
              margin: 0;
              color: #909399;
              font-size: 13px;
              line-height: 1.5;
            }
          }

          img {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 6px;
          }
        }

        .feature-list {
          list-style: none;
          padding: 0;
          margin: 0;

          li {
            display: flex;
            align-items: center;
            font-size: 13px;
            color: #606266;
            margin-bottom: 4px;

            i {
              color: #67c23a;
              margin-right: 6px;
            }
          }
        }

        .sample-goods {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;

          span {
            background: #ecf5ff;
            color: #409eff;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            cursor: pointer;
            transition: background 0.3s;

            &:hover {
              background: #d9ecff;
            }
          }
        }

        .el-button {
          align-self: flex-start;
        }
      }
    }
  }
}

// 鍝嶅簲寮忚璁�
@media (max-width: 1200px) {
  .goods-box {
    width: 100%;
    padding: 15px;

    .main-content {
      .goods-display {
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
        gap: 15px;
      }
    }
  }
}
</style>
