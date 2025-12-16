<template>
  <div class="delete-message">
    <div class="delete-text" @click="dialogVisible = true">删除</div>

    <el-dialog title="提示" v-model:visible="dialogVisible" width="30%">
      <span>确认删除该商品？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="success" @click="deleteMessageClick">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { deleteProduct, deleteDemand } from "../api/trade";
export default {
  inject: ["reload"],
  props: {
    ctype: {
      type: String,
      default: "goods", // goods 或 needs
    },
  },
  data() {
    return {
      dialogVisible: false,
    };
  },
  methods: {
    async deleteMessageClick() {
      try {
        if (!this.$store.state.changedOrderId) {
          this.$message.error("缺少ID，无法删除");
          return;
        }
        if (this.ctype === "needs") {
          await deleteDemand(this.$store.state.changedOrderId);
        } else {
          await deleteProduct(this.$store.state.changedOrderId);
        }
        this.dialogVisible = false;
        this.$message.success("删除成功");
        if (this.reload) this.reload();
      } catch (err) {
        console.error(err);
        this.$message.error("删除失败");
      }
    },
  },
};
</script>

<style lang="less" scoped>
.delete-message {
  .delete-text {
    height: 25px;
    cursor: pointer;
    color: #67C23A;
    &:hover{
      color: #035D1C;
    }
  }
}
</style>