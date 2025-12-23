<template>
  <div class="user-password">
    <el-form
      :model="ruleForm"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="原密码：" prop="name">
        <el-input
          v-model="ruleForm.oldPassword"
          style="width: 300px" show-password
        ></el-input>
      </el-form-item>
      <el-form-item label="新密码：" prop="name">
        <el-input
          v-model="ruleForm.newPassword"
          style="width: 300px" show-password
        ></el-input>
      </el-form-item>
      <el-form-item label="确认新密码：" prop="name">
        <el-input
          v-model="ruleForm.confirmNewPassword"
          style="width: 300px" show-password
        ></el-input>
      </el-form-item>
    </el-form>
    <el-button
      style="margin-left: 300px"
      type="success"
      @click="changePasswordClick"
      >修改密码</el-button
    >
  </div>
</template>

<script>
import { updateUserPassword } from "../api/user";
export default {
  data() {
    return {
      ruleForm: {
        oldPassword: "",
        newPassword: "",
        confirmNewPassword: "",
      },
    };
  },
  methods: {
    changePasswordClick() {
      if (this.ruleForm.newPassword === this.ruleForm.confirmNewPassword) {
        if (this.ruleForm.newPassword === "") {
          alert("新密码不能为空");
        }
        updateUserPassword({
          oldPassword: this.ruleForm.oldPassword,
          newPassword: this.ruleForm.newPassword,
        })
          .then((res) => {
            const ok = res && (res.success === true || res.flag === true);
            if (ok) {
              // 成功提示使用绿色样式
              this.$message.success(res.message || "密码修改成功，请重新登录");
              // 修改密码后退出登录
              this.$store.commit("updateLoginUserNickname", "");
              this.$store.commit("removeStorage");
              this.$router.push("/login").catch((err) => err);
              if (sessionStorage.getItem("/order/needs/pageCode")) {
                sessionStorage.removeItem("/order/needs/pageCode");
              }
              if (sessionStorage.getItem("/order/goods/pageCode")) {
                sessionStorage.removeItem("/order/goods/pageCode");
              }
              if (sessionStorage.getItem("/user/search/pageCode")) {
                sessionStorage.removeItem("/user/search/pageCode");
              }
            } else {
              this.$message.error(res && (res.message || res.data) ? (res.message || res.data) : "密码修改失败");
            }
          })
          .catch((err) => {
            const msg = err && (err.message || err.msg || err.data || JSON.stringify(err));
            this.$message.error(msg || "密码修改失败，请稍后再试");
          });
      } else {
        alert("新密码与确认密码不一致");
      }
    },
  },
  created() {
    this.$store.commit("updateUserActiveIndex", "1-3");
  },
};
</script>

<style lang="less" scoped>
.user-password {
  width: 900px;
  float: left;
  padding: 20px;
  background: #fff;
  height: 100%;
}
</style>