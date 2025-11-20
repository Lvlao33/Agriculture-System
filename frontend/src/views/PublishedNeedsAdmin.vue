<template>
  <div style="width:900px;height:100%">
    <category-grid :categories="categories" :columns="2"></category-grid>
  </div>
</template>

<script>
import CategoryGrid from "../components/CategoryGrid.vue";
export default {
  data() {
    return {
      categories: [
        { title: '蔬菜', sub: ['叶菜类','瓜类','豆类','根菜类'], icon: require('../assets/img/food.png') },
        { title: '水果', sub: ['仁果类','核果类','浆果类','柑橘类'], icon: require('../assets/img/fruit.png') }
      ]
    };
  },
  components: { CategoryGrid },
  created() {
    this.$store.commit("updatePublishActiveIndex", "4-2");
  },
};
</script>

<style>
</style>
