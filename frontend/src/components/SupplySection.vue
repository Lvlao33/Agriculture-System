<template>
  <section class="supply-section card" :style="{borderTopColor: themeColor}">
    <div class="supply-inner">
      <div class="left-panel" :style="{background: leftBg}">
        <div class="title" :style="{color: themeColor}">{{ title }}</div>
        <div class="subtitle">{{ subtitle }}</div>
        <img v-if="leftImage" :src="leftImage" alt="banner" />
      </div>

      <div class="categories">
        <div class="cat-col" v-for="(cat, cidx) in categories" :key="cidx">
          <div class="cat-title">{{ cat.title }}</div>
          <div class="items">
            <a v-for="(name, nidx) in cat.items" :key="nidx" class="item">{{ name }}</a>
          </div>
        </div>
      </div>

      <div class="featured">
        <div class="feat-title">
          <span class="bar" :style="{background: themeColor}"></span>
          好货推荐
        </div>
        <div class="goods-list">
          <div class="goods" v-for="(g, gidx) in featuredGoods" :key="gidx">
            <img :src="g.img" alt="goods" />
            <div class="price">
              <span class="num">{{ g.price }}</span>
              <span class="unit">{{ g.unit }}</span>
            </div>
            <div class="g-title">{{ g.title }}</div>
            <div class="tags">
              <span class="tag" v-for="(t, tidx) in g.tags || []" :key="tidx">{{ t }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="suppliers">
        <div class="sup-title">
          <span class="bar" :style="{background: themeColor}"></span>
          推荐供应商
        </div>
        <ul>
          <li v-for="(s, sidx) in suppliers" :key="sidx">{{ s }}</li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'SupplySection',
  props: {
    title: { type: String, required: true },
    subtitle: { type: String, default: '' },
    themeColor: { type: String, default: '#39b54a' },
    leftImage: { type: String, default: '' },
    leftBg: { type: String, default: 'linear-gradient(90deg,#eef9f1,#ffffff)' },
    categories: { type: Array, default: () => [] },
    featuredGoods: { type: Array, default: () => [] },
    suppliers: { type: Array, default: () => [] }
  }
}
</script>

<style lang="less" scoped>
.supply-section {
  border-top: 3px solid #39b54a;
}
.supply-inner {
  display: grid;
  grid-template-columns: 300px 1fr 400px 300px;
  gap: 24px;
  max-width: 100%;
}
.left-panel {
  padding: 16px;
  border-radius: 6px;
  position: relative;
  min-height: 220px;
  .title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 6px;
  }
  .subtitle {
    font-size: 13px;
    color: #6b6b6b;
    margin-bottom: 12px;
  }
  img {
    position: absolute;
    left: 12px;
    bottom: 10px;
    width: 180px;
    height: auto;
    object-fit: contain;
  }
}
.categories {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.cat-col .cat-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 12px;
}
.item {
  color: #333;
  cursor: pointer;
}
.item:hover { color: #035D1C; text-decoration: underline; }

.featured .feat-title,
.suppliers .sup-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.feat-title .bar,
.sup-title .bar { display: inline-block; width: 6px; height: 16px; margin-right: 6px; border-radius: 3px; }

.goods-list { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.goods {
  background: #fafafa;
  border-radius: 6px;
  padding: 8px;
}
.goods img { width: 100%; height: 140px; object-fit: cover; border-radius: 4px; }
.price { color: #E84C3D; margin-top: 6px; }
.price .num { font-size: 18px; font-weight: 700; }
.price .unit { font-size: 12px; margin-left: 2px; color: #E84C3D; }
.g-title { font-size: 13px; margin: 6px 0; color: #333; }
.tag { font-size: 12px; color: #666; background: #eee; padding: 2px 6px; border-radius: 10px; margin-right: 6px; }

.suppliers ul { list-style: none; padding: 0; margin: 0; }
.suppliers li { line-height: 24px; font-size: 13px; color: #333; cursor: pointer; }
.suppliers li:hover { color: #035D1C; }

@media (max-width: 1200px) {
  .supply-inner { grid-template-columns: 220px 1fr; }
  .featured, .suppliers { grid-column: span 2; }
}
</style>


