<!-- encoding: utf-8 -->
<template>
  <el-card>
    <h4>ÏûÏ¢</h4>
    <el-list style="max-height:240px;overflow:auto">
      <div v-for="(m,i) in messages" :key="i" class="msg-item">
        <div class="title">{{ m.title }}</div>
        <div class="time">{{ m.time }}</div>
      </div>
    </el-list>
  </el-card>
</template>

<script>
export default {
  name: 'ExpertMessages',
  data(){ return { messages: [] } },
  mounted(){ /* load messages via API when available */ }
}
</script>

<style scoped>
.msg-item{padding:8px;border-bottom:1px solid #f0f0f0}
.title{font-weight:600}
.time{color:#888;font-size:12px}
</style>
