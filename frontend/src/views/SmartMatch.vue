<template>
  <div class="details-box2">
    <loan-product-list></loan-product-list>
  </div>
</template>

<script>
import LoanProductList from '../components/LoanProductList.vue';

export default {
  components: { LoanProductList },
  data() {
    return {};
  },
  created() {
    // 页面初始化逻辑
  }
};
</script>

<style lang="less" scoped>
.details-box2 {
  width: 100%;
  margin: 0;
  background: transparent;
  min-height: 100vh;
}
</style>
