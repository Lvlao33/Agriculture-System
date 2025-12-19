<template>
  <div class="delete-message">
    <div @click="dialogVisible = true" class="delete">删除</div>
    <el-dialog title="提示" v-model:visible="dialogVisible" width="30%">
      <span>确认删除这条知识吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="success" @click="deleteMessageClick">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { deleteKnowledgeById } from "../api/knowledge";
export default {
  props: {
    knowledgeId: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      dialogVisible: false,
    };
  },
  methods: {
    deleteMessageClick() {
      this.dialogVisible = false;
      deleteKnowledgeById({
        knowledgeId: this.knowledgeId,
      })
        .then((res) => {
          this.$message.success(res.message || "删除成功");
          this.$emit("deleted");
        })
        .catch((err) => {
          this.$message.error("删除失败");
        });
    },
  },
};
</script>

<style lang="less" scoped>
.delete-message {
  .delete {
    height: 25px;
    cursor: pointer;
    margin-right: 10px;
    color: #67C23A;
    &:hover{
      color: #035D1C;
    }
  }
}
</style>