<!-- encoding: utf-8 -->
<template>
  <div class="expert-dashboard">
    <div class="left-col">
      <expert-profile />
      <expert-messages />
    </div>
    <div class="main-col">
      <div class="top-row">
        <expert-appointments />
        <expert-earnings />
      </div>
      <div class="bottom-row">
        <expert-knowledge-manage />
        <expert-analytics />
      </div>
    </div>
  </div>
</template>

<script>
import ExpertProfile from '@/components/expert/ExpertProfile.vue'
import ExpertMessages from '@/components/expert/ExpertMessages.vue'
import ExpertAppointments from '@/components/expert/ExpertAppointments.vue'
import ExpertKnowledgeManage from '@/components/expert/ExpertKnowledgeManage.vue'
import ExpertEarnings from '@/components/expert/ExpertEarnings.vue'
import ExpertAnalytics from '@/components/expert/ExpertAnalytics.vue'

export default {
  name: 'ExpertDashboard',
  components: {
    ExpertProfile,
    ExpertMessages,
    ExpertAppointments,
    ExpertKnowledgeManage,
    ExpertEarnings,
    ExpertAnalytics
  }
}
</script>

<style scoped lang="less">
.expert-dashboard{display:flex;gap:16px;width:1100px;margin:0 auto;padding:20px}
.left-col{width:280px;display:flex;flex-direction:column;gap:12px}
.main-col{flex:1;display:flex;flex-direction:column;gap:12px}
.top-row{display:flex;gap:12px}
.bottom-row{display:flex;gap:12px}
</style>
