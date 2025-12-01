<template>
  <div class="login-box">
    <div class="login">
      <div class="header">
        <!-- <a href="/">
        <img src="/public/img/logo3.png" alt="" />
      </a> -->
        <h1>用户登录</h1>
      </div>
      <div id="login_form">
        <div class="form-group">
          <label for="username">帐号</label>
          <input
              type="text"
              class="form-control"
              id="username"
              name="username"
              placeholder="请输入账�?"
              v-model="acount"
          />
        </div>
        <div class="form-group">
          <label for="">密码</label>

          <input
              type="password"
              class="form-control"
              id=""
              name="password"
              placeholder="Password"
              v-model="password"
          />
        </div>

        <div class="form-group" style="display:flex">
          <input  type="text"    class="form-control" v-model="verificationCode" placeholder="请输入验证码"  style="width: 200px"/>
          <div @click="refreshCode">
            <!--验证码组�?-->
            <s-identify :identifyCode="identifyCode"></s-identify>
          </div>
        </div>
        <span class="checkbox">
          <label> <input type="checkbox" />记住密码 </label>
          <span style="float: right" class="forgetbox"
          ><router-link to="/forget">忘记密码</router-link></span>

        </span>
        <button class="btn btn-success btn-block" @click="loginBtn">
          登录
        </button>
      </div>
      <div class="message">
        <p>没有账号? <router-link to="/register">立即注册</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import { userLogin } from "../api/user";
import SIdentify from '@/components/SIdentify';
export default {
  name: "Login",
  components: { SIdentify },
  data() {
    return {
      acount: "",
      password: "",
      verificationCode:'',
      identifyCode:'',
      identifyCodes:'abcdefghijklmnopqrstuvwxyz1234567890'
    };
  },
  methods: {
    refreshCode () {
      this.identifyCode = ''
      this.makeCode(this.identifyCodes,4);
    },
    makeCode (o,l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode += this.identifyCodes[this.randomNum(0, this.identifyCodes.length)]
      }
    },
    randomNum (min, max) {
      return Math.floor(Math.random() * (max - min) + min)
    },
    loginBtn() {

      if(!this.verificationCode){
        alert("验证码不能为�?");
        return;
      }

      if(this.verificationCode != this.identifyCode){
        alert("验证码不一�?");
        return;
      }

      userLogin({
        username: this.acount,
        password: this.password,
      })
          .then((res) => {
            if (this.acount == "") {
              alert("用户名不能为�?");
              return;
            } else if (this.password == "") {
              alert("密码不能为空");
              return;
            } else {
              if (res && res.success) {
                const token = res.data && res.data.token ? res.data.token : '';
                const user = res.data && res.data.user ? res.data.user : null;
                this.$store.commit("setToken", token);
                if (user) {
                  const nickname = user.nickname || user.username || '';
                  const avatar = user.avatar || '';
                  this.$store.commit("updateLoginUserNickname", nickname);
                  this.$store.commit("updateLoginUserAvatar", avatar);
                  if (user.id !== undefined && user.id !== null) {
                    this.$store.commit("updateLoginUserId", user.id);
                  }
                }
                const role = this.resolveUserRole(res, user);
                this.$store.commit("setUserRole", role);
                const target = this.getDefaultHome(role);
                this.$router.push(target).catch((err) => err);
              } else {
                alert(res && res.message ? res.message : '登录失败');
              }
            }
          })
          .catch((err) => {
            console.log(err);
          });
    },
    resolveUserRole(res, user) {
      const fallback = 'farmer';
      const data = res && res.data ? res.data : {};
      const directRole = data.roleKey || data.role || data.roleCode || data.userRole || data.identity;
      const roles = data.roles || data.roleList;
      let role = directRole;
      if (!role && Array.isArray(roles) && roles.length > 0) {
        role = roles[0];
      }
      if (!role && user) {
        role = user.role || user.identity || user.type;
      }
      if (typeof role === 'string') {
        const normalized = role.toLowerCase();
        if (['farmer', 'expert', 'bank'].includes(normalized)) {
          return normalized;
        }
        if (['staff', 'finance', 'banker'].includes(normalized)) {
          return 'bank';
        }
      }
      return fallback;
    },
    getDefaultHome(role) {
      // ��¼�����н�ɫͳһ����ԭ������ҳ
      return "/home/front";
    },
  },
  created() {},
  mounted () {
    // 初始化验证码
    this.identifyCode = ''
    this.makeCode(this.identifyCodes, 4)
  },
};
</script>

<style lang="less" scoped>
@import url("../../node_modules/bootstrap/dist/css/bootstrap.css");

.login-box {
  box-sizing: border-box;
  // width: 1897px;
  height: 100%;
  padding-top: 150px;
  background: url("../assets/img/rice.png");
  background-size: 1897px 920px;
  .login {
    width: 440px;
    margin: 0 auto;
    color: #333;
    .header {
      height: 40px;
      text-align: center;
      h1 {
        font-size: 26px;
        margin: 0;
        color: white;
      }
    }
    #login_form {
      padding: 20px;
      margin-bottom: 15px;
      border: 1px solid #d8dee2;
      border-radius: 5px;
      background-color: #fff;
    }
    .message {
      padding: 10px;
      padding-bottom: 0;
      color: white;
      border: 1px solid #d8dee2;
      border-radius: 5px;
      text-align: center;
    }
  }
}
</style>
