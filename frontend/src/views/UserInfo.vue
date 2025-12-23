<!-- 我的信息总览 -->
<template>
  <div class="my-info-dashboard">
    <!-- 头像居中展示区 -->
    <section class="profile-section">
      <div class="avatar-wrapper">
        <user-avatar ref="avatar" :cUserAvatar="userinfo.avatar" />
      </div>
      <h2 class="user-name">{{ userinfo.nickName || "未设置昵称" }}</h2>
      <el-button type="primary" size="medium" icon="el-icon-edit" @click="profileDialogVisible = true">
        修改个人资料
      </el-button>
    </section>

    <!-- 个人信息列表展示 -->
    <section class="info-list-section">
      <h3 class="section-title">基本信息</h3>
      <div class="info-list">
        <div class="info-row">
          <span class="info-label">昵称</span>
          <span class="info-value">{{ userinfo.nickName || "未填写" }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">手机号</span>
          <span class="info-value">{{ userinfo.phone || "未填写" }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">电子邮箱</span>
          <span class="info-value">{{ userinfo.email || "未填写" }}</span>
        </div>
      </div>
    </section>

    <section class="panels-grid">
        <div class="panel-card">
          <div class="panel-content">
            <h3>地址管理</h3>
            <p class="subtitle">管理您的收货地址信息</p>
          </div>
          <div class="panel-actions">
            <el-button size="medium" plain @click="handleAddAddress">新增地址</el-button>
            <el-button size="medium" type="primary" @click="openAddressManager">管理地址</el-button>
          </div>
        </div>
        <div class="panel-card">
          <div class="panel-content">
            <h3>密码管理</h3>
            <p class="subtitle">建议定期更换，提高账号安全等级</p>
          </div>
          <div class="panel-actions">
            <el-button size="medium" type="primary" @click="passwordDialogVisible = true">
              修改密码
            </el-button>
          </div>
        </div>
    </section>

    <!-- 修改个人资料弹窗 -->
    <el-dialog
      title="修改个人资料"
      :visible.sync="profileDialogVisible"
      width="520px"
      @close="$refs.profileFormRef && $refs.profileFormRef.resetFields()"
    >
      <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px">
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="profileForm.nickName" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" maxlength="11" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="profileForm.email" placeholder="可用于接收提醒（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="profileSaving" @click="updateInfo">保 存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      title="我的收货地址"
      :visible.sync="addressManagerVisible"
      width="640px"
      @open="fetchAddresses"
    >
      <el-skeleton :loading="addressLoading" animated>
        <template #default>
          <el-empty v-if="!addressList.length" description="暂未添加收货地址" />
          <div v-else class="address-list compact">
            <div
              v-for="item in addressList"
              :key="item.id"
              class="address-item"
              :class="{ 'is-default': item.isDefault }"
            >
              <div>
                <p class="name">
                  {{ item.consignee }}
                  <el-tag v-if="item.isDefault" size="mini" type="success">默认</el-tag>
                </p>
                <p class="detail">{{ item.addressDetail }}</p>
                <p class="phone">{{ item.phone }}</p>
              </div>
              <div class="address-actions">
                <el-button type="text" @click="handleEditAddress(item)">编辑</el-button>
                <el-divider direction="vertical"></el-divider>
                <el-button type="text" @click="handleDeleteAddress(item)">删除</el-button>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
      <template #footer>
        <el-button plain @click="handleAddAddress">新增地址</el-button>
        <el-button type="primary" @click="addressManagerVisible = false">关 闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      title="修改密码"
      :visible.sync="passwordDialogVisible"
      width="480px"
      @close="$refs.passwordFormRef && $refs.passwordFormRef.resetFields()"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="110px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmNewPassword">
          <el-input v-model="passwordForm.confirmNewPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="changePassword">
          确 认
        </el-button>
      </template>
    </el-dialog>

    <el-dialog :title="addressDialogTitle" :visible.sync="addressDialogVisible" width="520px">
      <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="100px">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="addressForm.consignee" />
      </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" maxlength="11" />
      </el-form-item>
        <el-form-item label="详细地址" prop="addressDetail">
          <el-input type="textarea" :rows="2" v-model="addressForm.addressDetail" />
      </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
      </el-form-item>
    </el-form>
      <template #footer>
        <el-button @click="closeAddressDialog">取 消</el-button>
        <el-button type="primary" :loading="addressSaving" @click="submitAddress">保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  loginUpdateByUsername,
  loginSelectByUsername,
  updateUserPassword,
} from "../api/user";
import {
  addAddress,
  selectAllAddressByUsername,
  defaultAddressInfoUpdate,
  deleteAddressById,
} from "../api/address";
import UserAvatar from "../components/UserAvatar.vue";

export default {
  name: "UserInfoPage",
  components: { UserAvatar },
  data() {
    return {
      userinfo: {
        avatar: "",
        nickName: "",
        phone: "",
        email: "",
      },
      profileDialogVisible: false,
      profileSaving: false,
      profileForm: {
        nickName: "",
        phone: "",
        email: "",
      },
      profileRules: {
        nickName: [{ required: true, message: "请输入昵称", trigger: "blur" }],
      },
      addressManagerVisible: false,
      addressLoading: false,
      addressSaving: false,
      addressList: [],
      addressDialogVisible: false,
      addressDialogTitle: "新增地址",
      addressForm: {
        id: null,
        consignee: "",
        phone: "",
        addressDetail: "",
        isDefault: false,
      },
      addressRules: {
        consignee: [{ required: true, message: "请填写收货人", trigger: "blur" }],
        phone: [{ required: true, message: "请填写联系电话", trigger: "blur" }],
        addressDetail: [{ required: true, message: "请填写详细地址", trigger: "blur" }],
      },
      passwordDialogVisible: false,
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmNewPassword: "",
      },
      passwordRules: {
        oldPassword: [{ required: true, message: "请输入原密码", trigger: "blur" }],
        newPassword: [{ required: true, message: "请输入新密码", trigger: "blur" }],
        confirmNewPassword: [
          { required: true, message: "请再次输入新密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
      passwordLoading: false,
    };
  },
  computed: {
    profileCompletion() {
      const fields = ["nickName", "phone", "email"];
      const filled = fields.filter((key) => this.userinfo[key]);
      return Math.round((filled.length / fields.length) * 100);
    },
  },
  methods: {
    triggerUpload() {
      if (this.$refs.avatar && this.$refs.avatar.submitUpload) {
        this.$refs.avatar.submitUpload();
      }
    },
    maskIdCard(idCard) {
      if (!idCard || idCard.length < 8) return idCard;
      return idCard.slice(0, 4) + "****" + idCard.slice(-4);
    },
    fetchUserInfo() {
      loginSelectByUsername({})
        .then((res) => {
          const data = res && res.data ? res.data : {};
          // 兼容后端字段命名：后端常用 `nickname`（小写），前端使用 `nickName`（驼峰）
          const normalized = {
            nickName: data.nickName || data.nickname || "",
            phone: data.phone || data.phoneNumber || "",
            email: data.email || "",
            avatar: data.avatar || this.userinfo.avatar || "",
            username: data.username || data.userName || this.userinfo.username,
            id: data.id || data.userId || this.userinfo.id,
          };

          this.userinfo = { ...this.userinfo, ...normalized };
          // 同步到编辑表单（使用驼峰字段）
          this.profileForm = {
            nickName: this.userinfo.nickName || "",
            phone: this.userinfo.phone || "",
            email: this.userinfo.email || "",
          };
        })
        .catch(() => {
          this.$message.error("获取用户信息失败");
        });
    },
    updateInfo() {
      this.$refs.profileFormRef.validate((valid) => {
        if (!valid) return;
        this.profileSaving = true;
        const avatar = this.$refs.avatar?.cUserAvatar || this.userinfo.avatar;
        // 需要传入 username，以便后端识别当前用户（Authorization header 不可用时）
        loginUpdateByUsername({
          username: this.userinfo.username || this.userinfo.userName || undefined,
          nickName: this.profileForm.nickName,
          avatar: avatar,
        })
          .then((res) => {
            if (res.flag) {
              // 更新本地数据（不要覆盖 token）
              this.userinfo = { ...this.userinfo, ...this.profileForm, avatar };
              this.$store.commit("updateLoginUserNickname", this.profileForm.nickName);
              this.$store.commit("updateLoginUserAvatar", avatar);
              this.$message.success(res.message || "资料保存成功");
              this.profileDialogVisible = false;
            } else {
              this.$message.error(res.data || "资料保存失败");
            }
          })
          .catch(() => {
            this.$message.error("资料保存失败，请稍后再试");
          })
          .finally(() => {
            this.profileSaving = false;
          });
      });
    },
    openAddressManager() {
      this.addressManagerVisible = true;
    },
    fetchAddresses() {
      this.addressLoading = true;
      selectAllAddressByUsername()
        .then((res) => {
          this.addressList = res.data || [];
        })
        .catch(() => {
          this.$message.error("获取地址失败");
        })
        .finally(() => {
          this.addressLoading = false;
        });
    },
    handleAddAddress() {
      this.addressDialogTitle = "新增地址";
      this.addressForm = {
        id: null,
        consignee: "",
        phone: "",
        addressDetail: "",
        isDefault: false,
      };
      this.addressDialogVisible = true;
    },
    handleEditAddress(item) {
      this.addressDialogTitle = "编辑地址";
      this.addressForm = { ...item };
      this.addressDialogVisible = true;
    },
    handleDeleteAddress(item) {
      this.$confirm(`确定要删除【${item.consignee}】的地址吗？`, "提示", {
        type: "warning",
      })
        .then(() => {
          return deleteAddressById({ addressId: item.id });
        })
        .then((res) => {
          if (res.flag) {
            this.$message.success("删除成功");
            this.fetchAddresses();
          } else {
            this.$message.error(res.message || "删除失败");
          }
        })
        .catch(() => {});
    },
    submitAddress() {
      this.$refs.addressFormRef.validate((valid) => {
        if (!valid) return;
        this.addressSaving = true;
        const payload = {
          consignee: this.addressForm.consignee,
          phone: this.addressForm.phone,
          addressDetail: this.addressForm.addressDetail,
          isDefault: this.addressForm.isDefault,
          id: this.addressForm.id,
        };
        const requestPromise = payload.id
          ? defaultAddressInfoUpdate(payload)
          : addAddress(payload);
        requestPromise
          .then((res) => {
            if (res.flag) {
              this.$message.success(res.message || "保存成功");
              this.closeAddressDialog();
              this.fetchAddresses();
            } else {
              this.$message.error(res.message || "保存失败");
            }
          })
          .catch(() => {
            this.$message.error("保存失败，请稍后再试");
          })
          .finally(() => {
            this.addressSaving = false;
          });
      });
    },
    closeAddressDialog() {
      this.addressDialogVisible = false;
      this.$nextTick(() => {
        if (this.$refs.addressFormRef) {
          this.$refs.addressFormRef.resetFields();
        }
      });
    },
    changePassword() {
      this.$refs.passwordFormRef.validate((valid) => {
        if (!valid) return;
        this.passwordLoading = true;
        updateUserPassword({
          oldPassword: this.passwordForm.oldPassword,
          newPassword: this.passwordForm.newPassword,
        })
          .then((res) => {
            const ok = res && (res.success === true || res.flag === true);
            if (ok) {
              this.$message.success(res.message || "密码修改成功，请重新登录");
              this.passwordForm = {
                oldPassword: "",
                newPassword: "",
                confirmNewPassword: "",
              };
              this.$store.commit("updateLoginUserNickname", "");
              this.$store.commit("removeStorage");
              this.$router.push("/login");
              this.passwordDialogVisible = false;
            } else {
              this.$message.error(res && (res.message || res.data) ? (res.message || res.data) : "密码修改失败");
            }
          })
          .catch((err) => {
            const msg = err && (err.message || err.msg || err.data || JSON.stringify(err));
            this.$message.error(msg || "密码修改失败，请稍后再试");
          })
          .finally(() => {
            this.passwordLoading = false;
          });
        });
    },
  },
  created() {
    this.$store.commit("updateUserActiveIndex", "1-1");
    this.fetchUserInfo();
  },
};
</script>

<style lang="less" scoped>
.my-info-dashboard {
  width: 100%;
  background: #fff;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 14px 32px rgba(24, 45, 89, 0.08);
  border: 1px solid rgba(91, 126, 158, 0.12);
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 头像居中区域 */
.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 20px;
  background: linear-gradient(180deg, #f0f4ff 0%, #fff 100%);
  border-radius: 16px;
  border: 1px solid rgba(91, 126, 158, 0.1);
}

.avatar-wrapper {
  margin-bottom: 16px;
}

::v-deep .user-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

::v-deep .user-avatar .avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid rgba(92, 124, 250, 0.25);
  box-shadow: 0 8px 24px rgba(92, 124, 250, 0.2);
}

.user-name {
  margin: 8px 0 4px;
  font-size: 24px;
  font-weight: 600;
  color: #1f2c3b;
  text-align: center;
}

.user-desc {
  margin: 0 0 16px;
  font-size: 14px;
  color: #7a8b9a;
  text-align: center;
}

.status-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
}

.status-tags span {
  padding: 6px 16px;
  border-radius: 999px;
  background: rgba(92, 124, 250, 0.12);
  color: #5c7cfa;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-tags span.filled {
  background: rgba(83, 197, 118, 0.15);
  color: #2a9259;
}

.profile-section .el-button {
  padding: 12px 32px;
  font-size: 15px;
  border-radius: 10px;
  background: linear-gradient(135deg, #5c7cfa 0%, #4263eb 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(92, 124, 250, 0.35);
  
  &:hover {
    background: linear-gradient(135deg, #4263eb 0%, #3b5bdb 100%);
  }
}

/* 信息列表展示区 */
.info-list-section {
  padding: 0 12px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2c3b;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(92, 124, 250, 0.2);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid rgba(91, 126, 158, 0.1);
  
  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  width: 120px;
  flex-shrink: 0;
  font-size: 14px;
  color: #7a8b9a;
  font-weight: 500;
}

.info-value {
  flex: 1;
  font-size: 15px;
  color: #1f2c3b;
}

.panels-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.panel-card {
  border: 1px solid rgba(91, 126, 158, 0.16);
  border-radius: 16px;
  padding: 24px 28px;
  background: linear-gradient(145deg, #fff 0%, #f9faff 100%);
  box-shadow: 0 10px 18px rgba(24, 45, 89, 0.04);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 160px;

  h3 {
    margin: 0 0 8px;
    font-size: 18px;
    color: #1f2c3b;
  }

  .subtitle {
    margin: 0;
    color: #7a8b9a;
    font-size: 14px;
  }
}

.panel-content {
  flex: 1;
}

.panel-actions {
  margin-top: 20px;
  display: flex;
  gap: 14px;

  .el-button {
    padding: 12px 28px;
    font-size: 15px;
    border-radius: 10px;
  }

  .el-button--primary {
    background: linear-gradient(135deg, #5c7cfa 0%, #4263eb 100%);
    border: none;
    box-shadow: 0 4px 12px rgba(92, 124, 250, 0.3);
    
    &:hover {
      background: linear-gradient(135deg, #4263eb 0%, #3b5bdb 100%);
    }
  }

  .el-button--default {
    border-color: #5c7cfa;
    color: #5c7cfa;
    
    &:hover {
      background: rgba(92, 124, 250, 0.08);
    }
  }
}

.card-label {
  font-size: 12px;
  letter-spacing: 0.08em;
  color: #5c7cfa;
  margin: 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.address-list.compact {
  max-height: 360px;
  overflow-y: auto;
}

.address-item {
  border: 1px solid rgba(91, 126, 158, 0.16);
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.address-item.is-default {
  border-color: rgba(83, 197, 118, 0.5);
  background: rgba(83, 197, 118, 0.06);
}

.address-meta .name {
  margin: 0;
  font-weight: 600;
  color: #1f2c3b;
  font-size: 16px;
}

.address-meta .detail,
.address-meta .phone {
  margin: 4px 0;
  color: #7a8b9a;
}

.address-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #5c7cfa;
}

@media (max-width: 1200px) {
  .info-wrap {
    grid-template-columns: 1fr;
  }

  .panels-grid {
    grid-template-columns: 1fr;
  }
}
</style>