<template>
  <div class="goods-box">
    <el-backtop target=".home-content"></el-backtop>

    <!-- 顶部操作�? -->
    <div class="top-bar">
      <div class="search-section">
        <el-input
          v-model="searchValue"
          maxlength="100"
          clearable
          placeholder="搜索商品名称、产�?..."
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
        发布商品
      </el-button>
    </div>

    <!-- �۸�Ԥ�� -->
    <section class="forecast-section" ref="forecastSection">
      <div class="forecast-header">
        <div>
          <div class="tag">�۸�Ԥ��</div>
          <h2>δ��7������</h2>
          <p class="sub">����XGBoost ʱ��ع飬����ͺ��뼾������</p>
        </div>
        <div class="forecast-actions">
          <el-select
            v-model="forecastCommodity"
            placeholder="ѡ��Ʒ��"
            size="small"
            style="width: 150px;"
            @change="fetchForecast"
          >
            <el-option
              v-for="item in forecastCommodities"
              :key="item"
              :value="item"
              :label="item"
            />
          </el-select>
          <el-button size="small" type="primary" plain @click="refreshForecast" :loading="forecastLoading">
            ˢ��Ԥ��
          </el-button>
        </div>
      </div>

      <el-card shadow="hover" class="forecast-card">
        <div class="forecast-meta">
          <div class="meta-item">
            <span class="meta-label">Ԥ�����</span>
            <span class="meta-value">{{ forecastSummary.avg }} Ԫ/��</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">������Χ</span>
            <span class="meta-value">{{ forecastSummary.range }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">ģ��</span>
            <span class="meta-value">XGBoost �ع�</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">����ʱ��</span>
            <span class="meta-value">{{ forecastSummary.updatedAt }}</span>
          </div>
        </div>

        <div class="forecast-body">
          <div class="forecast-chart">
            <div class="chart-y-label">�۸�</div>
            <div class="chart-bars" v-if="forecastSeries.length">
              <div
                v-for="(point, idx) in forecastSeries"
                :key="idx"
                class="bar"
                :style="{ height: point.barHeight + '%'}"
              >
                <div class="bar-value">{{ point.pred }} </div>
                <div class="bar-date">{{ point.date }}</div>
              </div>
            </div>
            <div v-else class="chart-empty">
              <el-skeleton animated :rows="2" style="width: 100%;"></el-skeleton>
            </div>
          </div>

          <div class="forecast-table">
            <el-table
              v-loading="forecastLoading"
              size="small"
              :data="forecastTable"
              border
              empty-text="����Ԥ������"
              height="260"
            >
              <el-table-column prop="date" label="����" width="90"></el-table-column>
              <el-table-column prop="pred" label="Ԥ���(Ԫ/��)"></el-table-column>
              <el-table-column prop="lower" label="�½�"></el-table-column>
              <el-table-column prop="upper" label="�Ͻ�"></el-table-column>
            </el-table>
          </div>
        </div>

        <div v-if="forecastError" class="forecast-error">
          <i class="el-icon-warning-outline"></i>
          {{ forecastError }}
        </div>
      </el-card>
    </section>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧分类�? -->
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
          <span>水果�?</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'vegetable' }"
          @click="selectCategory('vegetable')"
        >
          <i class="el-icon-food"></i>
          <span>蔬菜�?</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'grain' }"
          @click="selectCategory('grain')"
        >
          <i class="el-icon-coffee-cup"></i>
          <span>粮食�?</span>
        </div>
        <div
          class="category-item"
          :class="{ active: selectedCategory === 'livestock' }"
          @click="selectCategory('livestock')"
        >
          <i class="el-icon-cherry"></i>
          <span>畜牧�?</span>
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

      <!-- 右侧商品展示�? -->
      <div class="goods-display">
        <div v-if="filteredGoods.length === 0" class="empty-state">
          <i class="el-icon-box"></i>
          <p>暂无商品</p>
        </div>
        <div
          v-for="(item, index) in filteredGoods"
          :key="index"
          class="goods-card"
          @click="showGoodsDetail(item)"
        >
          <el-card shadow="hover" :body-style="{ padding: '0' }">
            <div class="goods-image-wrapper">
              <img
                v-if="item.picture && item.picture !== ''"
                :src="getImageUrl(item.picture)"
                class="goods-img"
                alt="商品图片"
                @error="handleImageError"
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
              <p class="goods-content">{{ item.content || item.name || '商品名称' }}</p>
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

    <!-- 商品详情弹窗 -->
    <el-dialog
      :title="detailDialogTitle"
      :visible.sync="detailDialogVisible"
      width="80%"
      :before-close="handleCloseDialog"
      class="goods-detail-dialog"
    >
      <div class="detail-content">
        <div class="detail-goods-list">
          <div
            v-for="(item, index) in similarGoods"
            :key="index"
            class="detail-goods-item"
            @click="goToGoodsDetailPage(item)"
          >
            <div class="detail-goods-image">
              <img
                v-if="item.picture && item.picture !== ''"
                :src="getImageUrl(item.picture)"
                alt="商品图片"
                @error="handleImageError"
              />
              <img
                v-else
                :src="`/order/wutu.gif`"
                alt="暂无图片"
              />
            </div>
            <div class="detail-goods-info">
              <div class="detail-goods-name">{{ item.content || item.name || '商品名称' }}</div>
              <div class="detail-goods-meta">
                <span class="detail-goods-origin">
                  <i class="el-icon-location-outline"></i>
                  {{ item.origin || '产地未知' }}
                </span>
                <span class="detail-goods-seller">
                  <i class="el-icon-user"></i>
                  {{ item.ownName || '未知卖家' }}
                </span>
              </div>
              <div class="detail-goods-price">
                <i class="el-icon-coin"></i>
                ¥{{ item.price || '面议' }}
              </div>
              <div class="detail-goods-stock" v-if="item.stock !== undefined">
                库存：{{ item.stock }}
              </div>
            </div>
            <div class="detail-goods-actions">
              <el-button type="primary" size="small" @click.stop="handleBuyNow(item)">立即购买</el-button>
              <el-button type="success" size="small" icon="el-icon-shopping-cart-2" @click.stop="handleAddToCart(item)">加入购物�?</el-button>
            </div>
          </div>
          <div v-if="similarGoods.length === 0" class="empty-detail-state">
            <i class="el-icon-box"></i>
            <p>暂无同类商品</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPriceForecast } from "@/api/price";

export default {
  name: "GoodsSource",
  data() {
    return {
      searchValue: '',
      selectedCategory: 'all',
      detailDialogVisible: false,
      detailDialogTitle: '商品详情',
      currentGoodsItem: null,
      similarGoods: [],
      // 示例商品数据（当API返回空数据时使用�?
      defaultGoods: [
        // 水果�?
        {
          name: '新鲜苹果',
          content: '新鲜苹果 红富�? 脆甜多汁',
          price: 12.00,
          picture: 'pro2.jpg',
          origin: '山东烟台',
          ownName: '张果�?',
          category: 'fruit',
          stock: 500,
          orderId: 'fruit1',
          keyword: '苹果'
        },
        {
          name: '优质苹果',
          content: '优质苹果 有机种植 无农药残�?',
          price: 15.00,
          picture: 'pro2.jpg',
          origin: '陕西',
          ownName: '李农�?',
          category: 'fruit',
          stock: 300,
          orderId: 'fruit2',
          keyword: '苹果'
        },
        {
          name: '精品苹果',
          content: '精品苹果 个大饱满 甜度�?',
          price: 18.00,
          picture: 'pro2.jpg',
          origin: '新疆',
          ownName: '王果�?',
          category: 'fruit',
          stock: 200,
          orderId: 'fruit3',
          keyword: '苹果'
        },
        {
          name: '新鲜橙子',
          content: '新鲜橙子 汁多味甜 维C丰富',
          price: 10.00,
          picture: 'pro2.jpg',
          origin: '江西',
          ownName: '陈果�?',
          category: 'fruit',
          stock: 400,
          orderId: 'fruit4',
          keyword: '橙子'
        },
        {
          name: '优质葡萄',
          content: '优质葡萄 无籽 甜度�?',
          price: 20.00,
          picture: 'pro2.jpg',
          origin: '新疆',
          ownName: '赵果�?',
          category: 'fruit',
          stock: 250,
          orderId: 'fruit5',
          keyword: '葡萄'
        },
        // 蔬菜�?
        {
          name: '新鲜白菜',
          content: '新鲜白菜 有机种植 口感脆嫩',
          price: 5.00,
          picture: 'pro1.jpg',
          origin: '山东',
          ownName: '刘菜�?',
          category: 'vegetable',
          stock: 800,
          orderId: 'veg1',
          keyword: '白菜'
        },
        {
          name: '有机白菜',
          content: '有机白菜 无农�? 绿色健康',
          price: 8.00,
          picture: 'pro1.jpg',
          origin: '河北',
          ownName: '周农�?',
          category: 'vegetable',
          stock: 600,
          orderId: 'veg2',
          keyword: '白菜'
        },
        {
          name: '新鲜萝卜',
          content: '新鲜萝卜 白萝�? 清脆爽口',
          price: 4.00,
          picture: 'pro1.jpg',
          origin: '河南',
          ownName: '吴菜�?',
          category: 'vegetable',
          stock: 700,
          orderId: 'veg3',
          keyword: '萝卜'
        },
        {
          name: '新鲜土豆',
          content: '新鲜土豆 黄心土豆 品质优良',
          price: 6.00,
          picture: 'pro1.jpg',
          origin: '内蒙�?',
          ownName: '郑农�?',
          category: 'vegetable',
          stock: 900,
          orderId: 'veg4',
          keyword: '土豆'
        },
        // 粮食�?
        {
          name: '优质大米',
          content: '优质大米 东北大米 香糯可口',
          price: 45.00,
          picture: 'rice.png',
          origin: '黑龙�?',
          ownName: '孙粮�?',
          category: 'grain',
          stock: 1000,
          orderId: 'grain1',
          keyword: '大米'
        },
        {
          name: '有机大米',
          content: '有机大米 绿色认证 营养丰富',
          price: 58.00,
          picture: 'rice.png',
          origin: '吉林',
          ownName: '钱农�?',
          category: 'grain',
          stock: 500,
          orderId: 'grain2',
          keyword: '大米'
        },
        {
          name: '优质小麦',
          content: '优质小麦 高筋小麦 适合做面�?',
          price: 35.00,
          picture: 'rice.png',
          origin: '河南',
          ownName: '周粮�?',
          category: 'grain',
          stock: 800,
          orderId: 'grain3',
          keyword: '小麦'
        },
        // 畜牧�?
        {
          name: '新鲜土鸡�?',
          content: '新鲜土鸡�? 散养 营养丰富',
          price: 35.00,
          picture: 'pro3.jpg',
          origin: '河北',
          ownName: '李养�?',
          category: 'livestock',
          stock: 200,
          orderId: 'live1',
          keyword: '鸡蛋'
        },
        {
          name: '有机土鸡�?',
          content: '有机土鸡�? 无激�? 品质保证',
          price: 42.00,
          picture: 'pro3.jpg',
          origin: '山东',
          ownName: '王养�?',
          category: 'livestock',
          stock: 150,
          orderId: 'live2',
          keyword: '鸡蛋'
        },
        {
          name: '新鲜牛奶',
          content: '新鲜牛奶 当日配�? 营养健康',
          price: 25.00,
          picture: 'pro3.jpg',
          origin: '内蒙�?',
          ownName: '赵牧�?',
          category: 'livestock',
          stock: 300,
          orderId: 'live3',
          keyword: '牛奶'
        },
        // 其他
        {
          name: '有机茶叶',
          content: '有机茶叶 原产地直�? 品质优良',
          price: 128.00,
          picture: 'chayangji.jpg',
          origin: '福建',
          ownName: '陈茶�?',
          category: 'other',
          stock: 100,
          orderId: 'other1',
          keyword: '茶叶'
        },
        {
          name: '新鲜玉米',
          content: '新鲜玉米 甜玉�? 口感香甜',
          price: 15.00,
          picture: 'farm.jpeg',
          origin: '河南',
          ownName: '黄农�?',
          category: 'other',
          stock: 400,
          orderId: 'other2',
          keyword: '玉米'
        }
      ],
      forecastCommodities: ['ƻ��', '����', 'С��', '����', '����'],
      forecastCommodity: 'ƻ��',
      forecastSeries: [],
      forecastTable: [],
      forecastSummary: {
        avg: '--',
        range: '--',
        updatedAt: '--'
      },
      forecastLoading: false,
      forecastError: ''
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
      // 优先使用传入的商品数据，如果为空则使用示例数�?
      let goods = (this.cgoods && this.cgoods.length > 0) ? [...this.cgoods] : [...this.defaultGoods];
      
      // 分类筛�?
      if (this.selectedCategory !== 'all') {
        goods = goods.filter(item => item.category === this.selectedCategory);
      }
      
      // 搜索筛�?
      if (this.searchValue) {
        const keyword = this.searchValue.toLowerCase();
        goods = goods.filter(item => {
          return (item.content && item.content.toLowerCase().includes(keyword)) ||
                 (item.name && item.name.toLowerCase().includes(keyword)) ||
                 (item.keyword && item.keyword.toLowerCase().includes(keyword)) ||
                 (item.origin && item.origin.toLowerCase().includes(keyword)) ||
                 (item.ownName && item.ownName.toLowerCase().includes(keyword));
        });
      }
      
      return goods;
    }
  },
  mounted() {
    this.initForecastFromRoute();
    this.fetchForecast();
  },
  methods: {
    initForecastFromRoute() {
      const query = this.$route && this.$route.query ? this.$route.query : {};
      if (query.commodity) {
        this.forecastCommodity = query.commodity;
      }
      if (query.section === 'forecast') {
        this.$nextTick(() => {
          this.scrollToForecast();
        });
      }
    },
    scrollToForecast() {
      if (this.$refs.forecastSection) {
        this.$refs.forecastSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    },
    async fetchForecast() {
      this.forecastLoading = true;
      this.forecastError = '';
      try {
        const res = await getPriceForecast({ commodity: this.forecastCommodity, horizon: 7 });
        const payload = res.data || res || {};
        const series = payload.series || payload.data || [];
        if (!Array.isArray(series) || series.length === 0) {
          throw new Error('empty');
        }
        this.applySeries(series);
        this.forecastSummary.updatedAt = payload.updatedAt || new Date().toLocaleString();
      } catch (err) {
        this.forecastError = 'Ԥ�����ݻ�ȡʧ�ܣ�����ʾʾ������';
        this.applySeries(this.getSampleForecast());
        this.forecastSummary.updatedAt = new Date().toLocaleString();
      } finally {
        this.forecastLoading = false;
      }
    },
    refreshForecast() {
      this.fetchForecast();
    },
    applySeries(series) {
      const normalized = series.map(item => ({
        date: item.date || item.ds || item.time || '',
        pred: Number(item.pred || item.yhat || item.value || item.forecast || item.price || 0).toFixed(2),
        lower: (item.lower || item.yhat_lower || item.lower_bound || item.lwr || item.pred_min || null),
        upper: (item.upper || item.yhat_upper || item.upper_bound || item.upr || item.pred_max || null),
      }));
      const preds = normalized.map(i => Number(i.pred));
      const max = Math.max(...preds);
      const min = Math.min(...preds);
      const heightSpan = Math.max(max - min, 0.01);
      this.forecastSeries = normalized.map(item => {
        const h = ((Number(item.pred) - min) / heightSpan) * 70 + 20;
        return {
          ...item,
          barHeight: Math.min(Math.max(h, 10), 100),
          lower: item.lower !== null ? Number(item.lower).toFixed(2) : '--',
          upper: item.upper !== null ? Number(item.upper).toFixed(2) : '--',
        };
      });
      const avg = (preds.reduce((a, b) => a + b, 0) / preds.length).toFixed(2);
      this.forecastTable = this.forecastSeries;
      this.forecastSummary.avg = avg;
      this.forecastSummary.range = `${min.toFixed(2)} - ${max.toFixed(2)} Ԫ/��`;
    },
    getSampleForecast() {
      const today = new Date();
      return Array.from({ length: 7 }).map((_, idx) => {
        const d = new Date(today);
        d.setDate(d.getDate() + idx + 1);
        const price = 3.2 + Math.sin(idx / 2) * 0.2 + idx * 0.05;
        return {
          date: `${d.getMonth() + 1}-${String(d.getDate()).padStart(2, '0')}`,
          pred: price.toFixed(2),
          lower: (price - 0.15).toFixed(2),
          upper: (price + 0.18).toFixed(2)
        };
      });
    },
    selectCategory(category) {
      this.selectedCategory = category;
    },
    handleSearch() {
      this.$emit('handleSearch', this.searchValue);
    },
    handlePublish() {
      this.$router.push('/home/addmessage/publishgoods').catch((err) => err);
    },
    showGoodsDetail(item) {
      // 显示商品详情弹窗，显示所有同类商�?
      this.currentGoodsItem = item;
      this.detailDialogTitle = `${item.content || item.name || '商品'} - 同类商品`;
      
      // 根据商品关键词查找同类商�?
      const keyword = item.keyword || this.extractKeyword(item.content || item.name);
      this.similarGoods = this.filteredGoods.filter(goods => {
        const goodsKeyword = goods.keyword || this.extractKeyword(goods.content || goods.name);
        return goodsKeyword === keyword && goods.orderId !== item.orderId;
      });
      
      // 将当前商品也加入列表（放在第一位）
      this.similarGoods.unshift(item);
      
      this.detailDialogVisible = true;
    },
    extractKeyword(text) {
      // 从商品名称中提取关键词（简单实现）
      if (!text) return '';
      // 提取常见商品关键�?
      const keywords = ['苹果', '橙子', '葡萄', '白菜', '萝卜', '土豆', '大米', '小麦', '鸡蛋', '牛奶', '茶叶', '玉米'];
      for (let kw of keywords) {
        if (text.includes(kw)) {
          return kw;
        }
      }
      return text.substring(0, 2); // 默认取前两个�?
    },
    handleCloseDialog() {
      this.detailDialogVisible = false;
      this.currentGoodsItem = null;
      this.similarGoods = [];
    },
    goToGoodsDetailPage(item) {
      // 跳转到商品详情页�?
      if (item.orderId) {
        this.$store.commit("updateOrderId", item.orderId);
        this.$router.push(`/home/details?orderId=${item.orderId}`).catch((err) => err);
      }
    },
    handleBuyNow(item) {
      // 立即购买
      this.goToGoodsDetailPage(item);
    },
    handleAddToCart(item) {
      // 加入购物�?
      this.$emit('addToCart', item);
      this.$message.success('已加入购物车');
    },
    getImageUrl(picture) {
      // 如果图片路径包含 /kn/ �? /order/，直接使�?
      if (picture.startsWith('/kn/') || picture.startsWith('/order/')) {
        return picture;
      }
      // 如果�? kn 目录下的图片
      if (['pro1.jpg', 'pro2.jpg', 'pro3.jpg', 'rice.png', 'chayangji.jpg', 'farm.jpeg'].includes(picture)) {
        return `/kn/${picture}`;
      }
      // 默认使用 order 目录
      return `/order/${picture}`;
    },
    handleImageError(event) {
      // 图片加载失败时，使用默认图片
      event.target.src = '/order/wutu.gif';
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

  .forecast-section {
    margin-bottom: 20px;

    .tag {
      display: inline-block;
      padding: 4px 8px;
      background: #ecf5ff;
      color: #409eff;
      border-radius: 4px;
      font-size: 12px;
      margin-bottom: 8px;
    }

    h2 {
      margin: 4px 0;
      font-size: 22px;
      color: #2c3e50;
    }

    .sub {
      margin: 0;
      color: #909399;
      font-size: 13px;
    }

    .forecast-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
    }

    .forecast-actions {
      display: flex;
      gap: 10px;
      align-items: center;
    }

    .forecast-card {
      border-radius: 10px;
    }

    .forecast-meta {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
      gap: 12px;
      margin-bottom: 16px;

      .meta-item {
        background: #f7f9fc;
        border-radius: 8px;
        padding: 10px 12px;
        display: flex;
        flex-direction: column;
        gap: 6px;

        .meta-label {
          font-size: 12px;
          color: #909399;
        }

        .meta-value {
          font-size: 16px;
          color: #303133;
          font-weight: 600;
        }
      }
    }

    .forecast-body {
      display: grid;
      grid-template-columns: 1.2fr 1fr;
      gap: 16px;

      @media (max-width: 960px) {
        grid-template-columns: 1fr;
      }
    }

    .forecast-chart {
      background: linear-gradient(180deg, #f9fbff 0%, #ffffff 100%);
      border: 1px solid #ebeef5;
      border-radius: 10px;
      padding: 14px;
      position: relative;

      .chart-y-label {
        position: absolute;
        left: 6px;
        top: 10px;
        font-size: 12px;
        color: #909399;
      }

      .chart-bars {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(60px, 1fr));
        gap: 10px;
        align-items: end;
        padding-top: 10px;
      }

      .bar {
        background: #ecf5ff;
        border-radius: 6px 6px 2px 2px;
        position: relative;
        display: flex;
        align-items: flex-end;
        justify-content: center;
        transition: all 0.2s;

        &:hover {
          background: #d9ecff;
          transform: translateY(-4px);
        }

        .bar-value {
          position: absolute;
          top: -22px;
          font-size: 12px;
          color: #303133;
        }

        .bar-date {
          position: absolute;
          bottom: -20px;
          font-size: 12px;
          color: #909399;
          white-space: nowrap;
        }
      }

      .chart-empty {
        padding: 20px;
      }
    }

    .forecast-table {
      .el-table {
        border-radius: 10px;
      }
    }

    .forecast-error {
      margin-top: 10px;
      color: #e6a23c;
      font-size: 13px;
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }

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
}

// 商品详情弹窗样式
/deep/ .goods-detail-dialog {
  .el-dialog__body {
    padding: 20px;
    max-height: 70vh;
    overflow-y: auto;
  }

  .detail-content {
    .detail-goods-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;

      .detail-goods-item {
        display: flex;
        flex-direction: column;
        background: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        padding: 15px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
          transform: translateY(-3px);
        }

        .detail-goods-image {
          width: 100%;
          height: 200px;
          border-radius: 6px;
          overflow: hidden;
          margin-bottom: 15px;
          background: #f5f5f5;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .detail-goods-info {
          flex: 1;

          .detail-goods-name {
            font-size: 16px;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .detail-goods-meta {
            display: flex;
            flex-direction: column;
            gap: 5px;
            margin-bottom: 10px;
            font-size: 12px;
            color: #666;

            .detail-goods-origin,
            .detail-goods-seller {
              display: flex;
              align-items: center;

              i {
                margin-right: 5px;
                color: #909399;
              }
            }
          }

          .detail-goods-price {
            color: #f56c6c;
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
            display: flex;
            align-items: center;

            i {
              margin-right: 5px;
              font-size: 16px;
            }
          }

          .detail-goods-stock {
            font-size: 12px;
            color: #909399;
            margin-bottom: 10px;
          }
        }

        .detail-goods-actions {
          display: flex;
          gap: 10px;
          margin-top: 10px;

          .el-button {
            flex: 1;
          }
        }
      }

      .empty-detail-state {
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
    }
  }
}

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
