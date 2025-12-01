<!-- encoding: utf-8 -->
<template>
  <el-card>
    <div class="profile">
      <div class="avatar">
        <el-avatar :size="80" :icon="user.avatar ? '' : 'el-icon-user'" />
      </div>
      <div class="meta">
        <h3>{{ user.name || '专家昵称' }}</h3>
        <p class="tag">{{ user.title || '农业专家' }}</p>
        <el-button size="mini" @click="edit">编辑资料</el-button>
      </div>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'ExpertProfile',
  data() {
    return { user: {} }
  },
  methods: {
    edit() {
      this.$message.info('编辑资料功能待实现（接口: PUT /api/expert/profile）')
    }
  },
  mounted() {
    // placeholder: load profile from API when available
    // this.$api.expert.getProfile().then(res => this.user = res.data)
  }
}
</script>

<style scoped>
.profile{display:flex;align-items:center;gap:12px}
.avatar{width:80px}
.meta h3{margin:0}
.tag{color:#666;font-size:12px}
</style>
