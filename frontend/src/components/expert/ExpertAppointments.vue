<!-- encoding: utf-8 -->
<template>
  <el-card>
    <div class="appointments-head">
      <h4>预约管理</h4>
      <el-button size="mini" type="primary" @click="refresh">刷新</el-button>
    </div>
    <el-table :data="appointments" style="width:100%">
      <el-table-column prop="date" label="时间" width="140"/>
      <el-table-column prop="userName" label="用户" width="140"/>
      <el-table-column prop="topic" label="咨询主题"/>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button size="mini" @click="accept(scope.row)">接受</el-button>
          <el-button size="mini" type="danger" @click="reject(scope.row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script>
export default {
  name: 'ExpertAppointments',
  data(){
    return { appointments: [] }
  },
  methods: {
    refresh(){
      this.$message.info('刷新预约（接口: GET /api/expert/appointments）')
    },
    accept(row){ this.$message.success('已接受（接口: POST /api/expert/appointments/{id}/accept）') },
    reject(row){ this.$message.error('已拒绝（接口: POST /api/expert/appointments/{id}/reject）') }
  }
}
</script>

<style scoped>
.appointments-head{display:flex;justify-content:space-between;align-items:center;margin-bottom:10px}
</style>
