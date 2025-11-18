<template>
  <div class="category-grid">
    <div class="grid-row" v-for="(row, rIndex) in rows" :key="rIndex">
      <div class="grid-item" v-for="(col, cIndex) in row" :key="cIndex">
        <div class="icon" :style="{backgroundImage: `url(${col.icon})`}"></div>
        <div class="title">{{ col.title }}</div>
        <div class="sub">{{ col.sub.join(' | ') }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CategoryGrid',
  props: {
    categories: {
      type: Array,
      default: () => []
    },
    columns: {
      type: Number,
      default: 4
    }
  },
  computed: {
    rows() {
      const res = [];
      for (let i = 0; i < this.categories.length; i += this.columns) {
        res.push(this.categories.slice(i, i + this.columns));
      }
      return res;
    }
  }
}
</script>

<style scoped>
.category-grid{
  width: 1100px;
  margin: 20px auto;
  background: #fff;
  padding: 10px 20px;
}
.grid-row{display:flex; margin-bottom: 18px;}
.grid-item{flex:1; display:flex; align-items:flex-start; padding:6px 10px;}
.grid-item .icon{width:42px;height:42px;background-size:cover;border-radius:6px;margin-right:10px}
.grid-item .title{font-weight:600;margin-bottom:6px}
.grid-item .sub{color:#777;font-size:13px}
</style>
