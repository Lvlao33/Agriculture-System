<!-- encoding: utf-8 -->
<template>
  <el-card>
    <div class="head">
      <h4>知识库管理</h4>
      <el-button size="mini" type="primary" @click="create">发布知识</el-button>
    </div>
    <el-table :data="knowledges" style="width:100%">
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="category" label="分类" width="120"/>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button size="mini" @click="edit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="del(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script>
export default {
  name: 'ExpertKnowledgeManage',
  data(){ return { knowledges: [] } },
  methods: {
    create(){ this.$message.info('创建知识（接口: POST /api/expert/knowledges）') },
    edit(r){ this.$message.info('编辑知识（接口: PUT /api/expert/knowledges/{id}）') },
    del(r){ this.$message.confirm('确认删除？').then(()=>this.$message.success('已删除')) }
  }
}
</script>

<style scoped>
.head{display:flex;justify-content:space-between;align-items:center;margin-bottom:10px}
</style>
