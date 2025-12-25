<template>
  <div class="delete-message">
    <div class="delete-text" @click="dialogVisible = true">下架</div>

    <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
      <span v-if="ctype === 'needs'">确认删除该需求？</span>
      <span v-else>确认下架该商品？</span>
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
        const id = this.$store.state.changedOrderId;
        if (!id) {
          this.$message.error(this.ctype === "needs" ? "缺少ID，无法删除" : "缺少ID，无法下架");
          return;
        }
        
        let res;
        if (this.ctype === "needs") {
          // 删除需求
          res = await deleteDemand(id);
          if (res && res.flag) {
            this.$message.success(res.message || "删除成功");
          } else {
            this.$message.error(res?.message || "删除失败");
            return;
          }
        } else {
          // 下架商品
          res = await deleteProduct(id);
          if (res && res.flag) {
            this.$message.success(res.message || "下架成功");
          } else {
            this.$message.error(res?.message || "下架失败");
            return;
          }
        }
        
        this.dialogVisible = false;
        // 触发父组件刷新列表
        this.$emit('deleted');
        // 如果父组件有reload方法，也调用它
        if (this.reload) {
          this.reload();
        }
      } catch (err) {
        console.error(this.ctype === "needs" ? '删除需求失败:' : '下架商品失败:', err);
        let errorMsg = this.ctype === "needs" ? "删除失败" : "下架失败";
        
        // 处理不同的错误格式
        if (err && err.message) {
          errorMsg = err.message;
        } else if (err && err.response && err.response.data && err.response.data.message) {
          errorMsg = err.response.data.message;
        } else if (typeof err === 'string') {
          errorMsg = err;
        }
        
        this.$message.error(errorMsg);
      }
    },
  },
};
</script>

<style lang="less" scoped>
.delete-message {
  .delete-text,
  .delete-text.action-link {
    display: inline-block;
    min-width: 96px;
    padding: 9px 16px;
    font-size: 16px;
    color: #2e8b57;
    background: #fff;
    border: 1px solid rgba(46,139,87,0.18);
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 4px 10px rgba(46,139,87,0.03);
    cursor: pointer;
    transition: all .12s ease;
    &:hover {
      background: rgba(46,139,87,0.04);
      transform: translateY(-1px);
    }
  }
}
</style>