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
          placeholder="搜索商品名称、产地..."
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
 
    <!-- 价格预测 -->
    <section class="forecast-section" ref="forecastSection">
      <div class="forecast-header">
        <div>
          <div class="tag">价格预测</div>
          <h2>未来7天价格走势</h2>
          <p class="sub">基于XGBoost 时间序列回归，提供均值和置信区间</p>
        </div>
        <div class="forecast-actions">
          <el-select
            v-model="forecastCommodity"
            placeholder="选择品类"
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
            刷新预测
          </el-button>
        </div>
      </div>

      <el-card shadow="hover" class="forecast-card">
        <div class="forecast-meta">
          <div class="meta-item highlight">
            <span class="meta-label">商品名称</span>
            <span class="meta-value commodity-name">{{ forecastCommodity }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">预测均价</span>
            <span class="meta-value">{{ forecastSummary.avg }} 元/斤</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">波动范围</span>
            <span class="meta-value">{{ forecastSummary.range }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">模型</span>
            <span class="meta-value">XGBoost 回归</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">更新时间</span>
            <span class="meta-value">{{ forecastSummary.updatedAt }}</span>
          </div>
        </div>

        <div class="forecast-body">
          <!-- ECharts 图表容器 -->
          <div class="forecast-chart-container">
            <div class="chart-title">
              <span class="commodity-name-inline">{{ forecastCommodity }}</span> 未来7天价格预测趋势
            </div>
            <div 
              ref="forecastChart" 
              class="forecast-chart-echarts"
              v-loading="forecastLoading"
            ></div>
          </div>

          <div class="forecast-table">
            <div class="table-title">
              <span class="commodity-name-inline">{{ forecastCommodity }}</span> 详细预测数据
            </div>
            <el-table
              v-loading="forecastLoading"
              size="small"
              :data="forecastTable"
              border
              empty-text="暂无预测数据"
              height="260"
            >
              <el-table-column prop="date" label="日期" width="100"></el-table-column>
              <el-table-column label="商品" width="80">
                <template slot-scope="scope">
                  <span class="table-commodity-name">{{ forecastCommodity }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="pred" label="预测价(元/斤)" width="120">
                <template slot-scope="scope">
                  <span class="pred-value">{{ scope.row.pred }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="lower" label="下界" width="100"></el-table-column>
              <el-table-column prop="upper" label="上界" width="100"></el-table-column>
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
      <!-- 左侧分类 -->
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
              <el-button type="success" size="small" icon="el-icon-shopping-cart-2" @click.stop="handleAddToCart(item)">加入购物车</el-button>
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
import { addOrderToCart } from "@/api/cart";
import * as echarts from 'echarts';

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
      // 示例商品数据（当API返回空数据时使用）
      defaultGoods: [
        // 水果类
        {
          name: '新鲜苹果',
          content: '新鲜苹果 红富士 脆甜多汁',
          price: 12.00,
          picture: 'pro2.jpg',
          origin: '山东烟台',
          ownName: '张果农',
          category: 'fruit',
          stock: 500,
          orderId: 'fruit1',
          keyword: '苹果'
        },
        {
          name: '优质苹果',
          content: '优质苹果 有机种植 无农药残留',
          price: 15.00,
          picture: 'pro2.jpg',
          origin: '陕西',
          ownName: '李农户',
          category: 'fruit',
          stock: 300,
          orderId: 'fruit2',
          keyword: '苹果'
        },
        {
          name: '精品苹果',
          content: '精品苹果 个大饱满 甜度高',
          price: 18.00,
          picture: 'pro2.jpg',
          origin: '新疆',
          ownName: '王果农',
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
          ownName: '陈果农',
          category: 'fruit',
          stock: 400,
          orderId: 'fruit4',
          keyword: '橙子'
        },
        {
          name: '优质葡萄',
          content: '优质葡萄 无籽 甜度高',
          price: 20.00,
          picture: 'pro2.jpg',
          origin: '新疆',
          ownName: '赵果农',
          category: 'fruit',
          stock: 250,
          orderId: 'fruit5',
          keyword: '葡萄'
        },
        // 蔬菜类
        {
          name: '新鲜白菜',
          content: '新鲜白菜 有机种植 口感脆嫩',
          price: 5.00,
          picture: 'pro1.jpg',
          origin: '山东',
          ownName: '刘菜农',
          category: 'vegetable',
          stock: 800,
          orderId: 'veg1',
          keyword: '白菜'
        },
        {
          name: '有机白菜',
          content: '有机白菜 无农药 绿色健康',
          price: 8.00,
          picture: 'pro1.jpg',
          origin: '河北',
          ownName: '周农户',
          category: 'vegetable',
          stock: 600,
          orderId: 'veg2',
          keyword: '白菜'
        },
        {
          name: '新鲜萝卜',
          content: '新鲜萝卜 白萝卜 清脆爽口',
          price: 4.00,
          picture: 'pro1.jpg',
          origin: '河南',
          ownName: '吴菜农',
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
          origin: '内蒙古',
          ownName: '郑农户',
          category: 'vegetable',
          stock: 900,
          orderId: 'veg4',
          keyword: '土豆'
        },
        // 粮食类
        {
          name: '优质大米',
          content: '优质大米 东北大米 香糯可口',
          price: 45.00,
          picture: 'rice.png',
          origin: '黑龙江',
          ownName: '孙粮农',
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
          ownName: '钱农户',
          category: 'grain',
          stock: 500,
          orderId: 'grain2',
          keyword: '大米'
        },
        {
          name: '优质小麦',
          content: '优质小麦 高筋小麦 适合做面条',
          price: 35.00,
          picture: 'rice.png',
          origin: '河南',
          ownName: '周粮农',
          category: 'grain',
          stock: 800,
          orderId: 'grain3',
          keyword: '小麦'
        },
        // 畜牧类
        {
          name: '新鲜土鸡蛋',
          content: '新鲜土鸡蛋 散养 营养丰富',
          price: 35.00,
          picture: 'pro3.jpg',
          origin: '河北',
          ownName: '李养殖户',
          category: 'livestock',
          stock: 200,
          orderId: 'live1',
          keyword: '鸡蛋'
        },
        {
          name: '有机土鸡蛋',
          content: '有机土鸡蛋 无激素 品质保证',
          price: 42.00,
          picture: 'pro3.jpg',
          origin: '山东',
          ownName: '王养殖户',
          category: 'livestock',
          stock: 150,
          orderId: 'live2',
          keyword: '鸡蛋'
        },
        {
          name: '新鲜牛奶',
          content: '新鲜牛奶 当日配送 营养健康',
          price: 25.00,
          picture: 'pro3.jpg',
          origin: '内蒙古',
          ownName: '赵牧农',
          category: 'livestock',
          stock: 300,
          orderId: 'live3',
          keyword: '牛奶'
        },
        // 其他
        {
          name: '有机茶叶',
          content: '有机茶叶 原产地直供 品质优良',
          price: 128.00,
          picture: 'chayangji.jpg',
          origin: '福建',
          ownName: '陈茶农',
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
      forecastCommodities: ['苹果', '土豆', '小麦', '玉米', '大米'],
      forecastCommodity: '苹果',
      forecastSeries: [],
      forecastTable: [],
      forecastSummary: {
        avg: '--',
        range: '--',
        updatedAt: '--'
      },
      forecastLoading: false,
      forecastError: '',
      chartInstance: null
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
    this.$nextTick(() => {
      this.initChart();
    });
  },
  beforeDestroy() {
    if (this.chartInstance) {
      this.chartInstance.dispose();
      this.chartInstance = null;
    }
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
        // 处理响应数据：后端返回格式为 { flag: true, data: { series, model, mape, updatedAt } }
        let payload = {};
        if (res.flag && res.data) {
          payload = res.data;
        } else if (res.series) {
          payload = res;
        } else if (res.data && res.data.series) {
          payload = res.data;
        } else {
          payload = res;
        }
        
        const series = payload.series || [];
        if (!Array.isArray(series) || series.length === 0) {
          throw new Error('预测数据为空');
        }
        
        this.applySeries(series);
        this.forecastSummary.updatedAt = payload.updatedAt || new Date().toLocaleString();
        this.forecastError = ''; // 清除错误信息
      } catch (err) {
        this.forecastError = '预测数据获取失败，已显示示例数据';
        this.applySeries(this.getSampleForecast());
        this.forecastSummary.updatedAt = new Date().toLocaleString();
      } finally {
        this.forecastLoading = false;
      }
    },
    refreshForecast() {
      this.fetchForecast();
    },
    initChart() {
      if (!this.$refs.forecastChart) {
        return;
      }
      this.chartInstance = echarts.init(this.$refs.forecastChart);
      window.addEventListener('resize', this.handleResize);
    },
    handleResize() {
      if (this.chartInstance) {
        this.chartInstance.resize();
      }
    },
    updateChart() {
      if (!this.chartInstance || !this.forecastSeries.length) {
        return;
      }
      
      const dates = this.forecastSeries.map(item => item.date);
      const preds = this.forecastSeries.map(item => Number(item.pred));
      const lowers = this.forecastSeries.map(item => item.lower !== '--' ? Number(item.lower) : null);
      const uppers = this.forecastSeries.map(item => item.upper !== '--' ? Number(item.upper) : null);
      
      const option = {
        title: {
          text: `${this.forecastCommodity} 价格预测趋势`,
          left: 'center',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold',
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            let result = `<div style="margin-bottom: 4px;"><strong>${params[0].axisValue}</strong></div>`;
            params.forEach(param => {
              result += `<div style="margin: 2px 0;">
                <span style="display:inline-block;width:10px;height:10px;background:${param.color};border-radius:50%;margin-right:5px;"></span>
                ${param.seriesName}: <strong>${param.value} 元/斤</strong>
              </div>`;
            });
            return result;
          },
          backgroundColor: 'rgba(50, 50, 50, 0.9)',
          borderColor: '#409EFF',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          data: ['预测价格', '置信区间'],
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: {
            rotate: 45,
            fontSize: 11
          }
        },
        yAxis: {
          type: 'value',
          name: '价格 (元/斤)',
          nameLocation: 'middle',
          nameGap: 40,
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: [
          {
            name: '预测价格',
            type: 'line',
            data: preds,
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 3,
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                ]
              }
            },
            markPoint: {
              data: [
                { type: 'max', name: '最高价' },
                { type: 'min', name: '最低价' }
              ]
            },
            markLine: {
              data: [
                { type: 'average', name: '平均�?' }
              ]
            }
          },
          {
            name: '置信区间',
            type: 'line',
            data: uppers,
            smooth: true,
            symbol: 'none',
            lineStyle: {
              width: 1,
              type: 'dashed',
              color: '#67C23A'
            },
            areaStyle: {
              color: 'rgba(103, 194, 58, 0.1)'
            }
          },
          {
            name: '置信区间',
            type: 'line',
            data: lowers,
            smooth: true,
            symbol: 'none',
            lineStyle: {
              width: 1,
              type: 'dashed',
              color: '#67C23A'
            },
            areaStyle: {
              color: 'rgba(103, 194, 58, 0.1)'
            },
            stack: 'confidence'
          }
        ]
      };
      
      this.chartInstance.setOption(option);
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
      this.forecastSummary.range = `${min.toFixed(2)} - ${max.toFixed(2)} �?/�?`;
      
      // 更新图表
      this.$nextTick(() => {
        this.updateChart();
      });
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
      // 显示商品详情弹窗，展示所有同类商�?
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
      // 跳转到商品详情页
      if (item.orderId) {
        this.$store.commit("updateOrderId", item.orderId);
        this.$router.push(`/home/details?orderId=${item.orderId}`).catch((err) => err);
      }
    },
    handleBuyNow(item) {
      // 立即购买
      this.goToGoodsDetailPage(item);
    },
    async handleAddToCart(item) {
      // 加入购物�?
      // 检查是否登录
      const token = localStorage.getItem('token');
      if (!token) {
        this.$message.warning('请先登录');
        this.$router.push('/login').catch(err => err);
        return;
      }

      // 获取商品ID，优先使用 id，其次使用 orderId
      const productId = item.id || item.orderId;
      if (!productId) {
        this.$message.error('商品信息不完整，无法添加到购物车');
        return;
      }

      try {
        // 确保productId是数字类型
        const numProductId = Number(productId);
        if (isNaN(numProductId) || numProductId <= 0) {
          this.$message.error('商品ID格式错误');
          console.error('商品ID格式错误:', productId);
          return;
        }

        console.log('添加商品到购物车:', { productId: numProductId, item });
        
        const res = await addOrderToCart({
          order_id: numProductId,
          productId: numProductId,
          quantity: 1
        });
        
        console.log('加入购物车响应:', res);
        
        if (res && res.flag === true) {
          this.$message.success(res.message || '已加入购物车');
          // 触发事件，让父组件知道已添加
          this.$emit('addToCart', item);
        } else {
          const errorMsg = res?.message || res?.data || '加入购物车失败';
          this.$message.error(errorMsg);
          console.error('加入购物车失败，响应:', res);
        }
      } catch (error) {
        console.error('加入购物车异常:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response,
          data: error.data,
          status: error.status,
          flag: error.flag,
          isNetworkError: error.isNetworkError,
          errorObject: error
        });
        
        // request.js 的拦截器会返回 err.response.data，所以 error 可能是后端返回的数据对象
        // 检查是否是后端返回的数据格式（包含 flag 和 message）
        if (error && typeof error === 'object' && 'flag' in error) {
          // 后端返回的错误数据格式
          if (error.flag === false) {
            const errorMsg = error.message || '加入购物车失败';
            const status = error.status;
            
            // 根据状态码处理不同错误
            if (status === 401 || errorMsg.includes('登录') || errorMsg.includes('请先登录')) {
              this.$message.warning('请先登录');
              this.$router.push('/login').catch(err => err);
            } else if (status === 404 || errorMsg.includes('不存在')) {
              this.$message.error('商品不存在');
            } else {
              this.$message.error(errorMsg);
            }
            return;
          }
        }
        
        // 处理网络错误
        if (error.isNetworkError) {
          this.$message.error(error.message || '加入购物车失败，请检查网络连接');
          return;
        }
        
        // 处理 HTTP 错误响应（原始 axios 错误格式，兼容性处理）
        if (error.response) {
          const status = error.response.status || error.status;
          const errorData = error.response.data || error.data || {};
          
          if (status === 401) {
            this.$message.warning('请先登录');
            this.$router.push('/login').catch(err => err);
          } else if (status === 404) {
            this.$message.error('商品不存在');
          } else if (status === 400) {
            const msg = errorData.message || (typeof errorData === 'string' ? errorData : '请求参数错误');
            this.$message.error(msg);
          } else {
            const msg = errorData.message || (typeof errorData === 'string' ? errorData : `加入购物车失败 (${status})`);
            this.$message.error(msg);
          }
        } else if (error.message) {
          // 其他错误（有错误消息）
          this.$message.error(error.message);
        } else {
          // 未知错误
          const errorMsg = error.message || (typeof error === 'string' ? error : '加入购物车失败，请检查网络连接');
          this.$message.error(errorMsg);
        }
      }
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
        transition: all 0.3s;

        &:hover {
          background: #ecf5ff;
          transform: translateY(-2px);
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
        }

        &.highlight {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;

          .meta-label {
            color: rgba(255, 255, 255, 0.9);
          }

          .meta-value {
            color: #fff;
          }

          .commodity-name {
            font-size: 18px;
            font-weight: 700;
            text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
          }
        }

        .meta-label {
          font-size: 12px;
          color: #909399;
        }

        .meta-value {
          font-size: 16px;
          color: #303133;
          font-weight: 600;
        }

        .commodity-name {
          font-size: 18px;
          font-weight: 700;
          color: #409EFF;
        }
      }
    }

    .forecast-body {
      display: grid;
      grid-template-columns: 1.2fr 1fr;
      gap: 20px;

      @media (max-width: 960px) {
        grid-template-columns: 1fr;
      }
    }

    .forecast-chart-container {
      background: linear-gradient(180deg, #f9fbff 0%, #ffffff 100%);
      border: 1px solid #ebeef5;
      border-radius: 10px;
      padding: 16px;
      position: relative;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

      .chart-title {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 12px;
        text-align: center;
        padding-bottom: 12px;
        border-bottom: 1px solid #ebeef5;

        .commodity-name-inline {
          color: #409EFF;
          font-size: 16px;
          font-weight: 700;
          margin-right: 4px;
        }
      }

      .forecast-chart-echarts {
        width: 100%;
        height: 350px;
        min-height: 350px;
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
      border: 1px solid #ebeef5;
      border-radius: 10px;
      overflow: hidden;
      background: #fff;

      .table-title {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
        padding: 12px 16px;
        background: #f7f9fc;
        border-bottom: 1px solid #ebeef5;

        .commodity-name-inline {
          color: #409EFF;
          font-size: 16px;
          font-weight: 700;
          margin-right: 4px;
        }
      }

      .el-table {
        border-radius: 10px;
      }

      .table-commodity-name {
        color: #409EFF;
        font-weight: 600;
        font-size: 13px;
      }

      .pred-value {
        color: #409EFF;
        font-weight: 700;
        font-size: 14px;
      }
    }

    .forecast-error {
      margin-top: 12px;
      padding: 12px 16px;
      background: #fef0f0;
      color: #f56c6c;
      border-radius: 6px;
      font-size: 13px;
      border-left: 4px solid #f56c6c;
      display: flex;
      align-items: center;
      gap: 8px;

      i {
        font-size: 16px;
      }
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
