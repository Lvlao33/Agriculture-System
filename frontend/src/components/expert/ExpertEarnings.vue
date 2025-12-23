<!-- encoding: utf-8 -->
<template>
  <el-card>
    <h4>收益统计</h4>
    <div class="earnings">
      <div class="item">
        <div class="num">{{ earnings.month || 0 }}</div>
        <div class="label">本月收益</div>
      </div>
      <div class="item">
        <div class="num">{{ earnings.total || 0 }}</div>
        <div class="label">累计收益</div>
      </div>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'ExpertEarnings',
  data() { 
    return { earnings: {} } 
  },
  mounted() { 
    /* 从接口加载收益数据 GET /api/expert/earnings */ 
  }
}
</script>

<style scoped>
.earnings {
  display: flex;
  gap: 12px;
}
.item {
  background: #fff;
  padding: 12px;
  border-radius: 6px;
  flex: 1;
  text-align: center;
}
.num {
  font-size: 20px;
  font-weight: 700;
}
.label {
  color: #888;
}
</style>