<template>
  <div style="height:100%">
    <category-grid :categories="categories" :columns="4"></category-grid>
  </div>
</template>

<script>
import CategoryGrid from "../components/CategoryGrid.vue";
export default {
  data() {
    return {
      categories: [
        { title: '蔬菜', sub: ['叶菜类','瓜类','豆类','根菜类'], icon: require('../assets/img/food.png') },
        { title: '水果', sub: ['仁果类','核果类','浆果类','柑橘类'], icon: require('../assets/img/fruit.png') },
        { title: '园林花卉', sub: ['乔木类','花卉类','宿根花卉'], icon: require('../assets/img/farm.jpeg') },
        { title: '农药肥料', sub: ['肥料','农药'], icon: require('../assets/img/eco.jpg') }
      ]
    };
  },
  components: { CategoryGrid },
  created() {
    this.$store.commit("updatePublishActiveIndex", "2-2");
  },
};
</script>

<style>
</style>