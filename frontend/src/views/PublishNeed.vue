<template>
  <div class="publish-need-container">
    <div class="publish-header">
      <h2><i class="el-icon-plus"></i> {{ isEditMode ? '编辑求购需求' : '发布求购需求' }}</h2>
      <p class="subtitle">{{ isEditMode ? '修改您的求购信息' : '填写您的求购信息，让更多供应商看到您的需求' }}</p>
    </div>

    <el-form 
      ref="form" 
      :model="form" 
      :rules="rules" 
      label-width="120px" 
      class="publish-form"
    >
      <el-form-item label="求购标题" prop="title">
        <el-input 
          v-model="form.title" 
          placeholder="请输入求购标题，如：求购香菜"
          style="width: 600px;"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="商品分类" prop="category">
        <el-select 
          v-model="form.category" 
          placeholder="请选择商品分类"
          style="width: 600px;"
        >
          <el-option label="蔬菜类" value="vegetable"></el-option>
          <el-option label="瓜类" value="melon"></el-option>
          <el-option label="豆类" value="bean"></el-option>
          <el-option label="茄果类" value="solanaceous"></el-option>
          <el-option label="薯芋类" value="tuber"></el-option>
          <el-option label="多年生" value="perennial"></el-option>
          <el-option label="水果类" value="fruit"></el-option>
          <el-option label="粮食类" value="grain"></el-option>
          <el-option label="畜牧类" value="livestock"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="求购描述" prop="content">
        <el-input 
          type="textarea" 
          v-model="form.content" 
          placeholder="请详细描述您的求购需求，包括商品名称、规格、数量、品质要求等信息"
          :rows="6"
          style="width: 600px;"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="期望价格">
        <el-input 
          v-model="form.price" 
          placeholder="请输入期望价格（元/单位），可不填"
          style="width: 300px;"
        >
          <template slot="prepend">?</template>
        </el-input>
        <span class="price-tip">如：10.00 或 面议</span>
      </el-form-item>

      <el-form-item label="求购数量">
        <el-input-number 
          v-model="form.quantity" 
          :min="0" 
          :max="999999"
          placeholder="请输入求购数量"
          style="width: 300px;"
        />
        <el-select 
          v-model="form.unit" 
          placeholder="单位"
          style="width: 150px; margin-left: 10px;"
        >
          <el-option label="斤" value="斤"></el-option>
          <el-option label="公斤" value="公斤"></el-option>
          <el-option label="吨" value="吨"></el-option>
          <el-option label="箱" value="箱"></el-option>
          <el-option label="件" value="件"></el-option>
          <el-option label="袋" value="袋"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="所在地区">
        <el-select 
          v-model="form.location" 
          placeholder="请选择所在地区"
          style="width: 300px;"
        >
          <el-option label="北京" value="beijing"></el-option>
          <el-option label="上海" value="shanghai"></el-option>
          <el-option label="广州" value="guangzhou"></el-option>
          <el-option label="深圳" value="shenzhen"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="联系方式" prop="contact">
        <el-input 
          v-model="form.contact" 
          placeholder="请输入联系电话或微信"
          style="width: 300px;"
        />
        <span class="contact-tip">用于供应商联系您</span>
      </el-form-item>

      <el-form-item label="联系人">
        <el-input 
          v-model="form.contactName" 
          placeholder="请输入联系人姓名"
          style="width: 300px;"
        />
      </el-form-item>

      <el-form-item label="QQ号码">
        <el-input 
          v-model="form.qq" 
          placeholder="请输入QQ号码（可选）"
          style="width: 300px;"
        />
      </el-form-item>

      <el-form-item>
        <el-button 
          size="large" 
          class="secondary-btn"
          @click="goBackTrade"
        >
          <i class="el-icon-back"></i> 返回
        </el-button>
        <el-button 
          type="primary" 
          size="large" 
          :loading="submitting"
          @click="submitForm"
        >
          <i class="el-icon-check"></i> {{ isEditMode ? '保存修改' : '发布求购' }}
        </el-button>
        <el-button 
          size="large" 
          @click="resetForm"
        >
          <i class="el-icon-refresh-left"></i> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { createDemand, getDemandDetail, updateDemand } from "../api/trade";

export default {
  name: "PublishNeed",
  data() {
    return {
      submitting: false,
      isEditMode: false,
      demandId: null,
      form: {
        title: "",
        category: "",
        content: "",
        price: "",
        quantity: 0,
        unit: "斤",
        location: "",
        contact: "",
        contactName: "",
        qq: ""
      },
      rules: {
        title: [
          { required: true, message: "请输入求购标题", trigger: "blur" },
          { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" }
        ],
        category: [
          { required: true, message: "请选择商品分类", trigger: "change" }
        ],
        content: [
          { required: true, message: "请输入求购描述", trigger: "blur" },
          { min: 10, max: 500, message: "长度在 10 到 500 个字符", trigger: "blur" }
        ],
        contact: [
          { required: true, message: "请输入联系方式", trigger: "blur" }
        ]
      }
    };
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "3");
    
    // 检查是否是编辑模式
    const id = this.$route.query.id || this.$route.params.id;
    if (id) {
      this.isEditMode = true;
      this.demandId = id;
      this.loadDemandDetail(id);
    }
  },
  methods: {
    loadDemandDetail(id) {
      getDemandDetail(id)
        .then((res) => {
          if (res.flag === true && res.data) {
            const demand = res.data;
            this.form.title = demand.title || "";
            this.form.category = demand.category || "";
            
            // 解析描述内容，提取各部分信息
            const description = demand.description || "";
            this.form.content = description.split('\n')[0] || description; // 第一行是主要内容
            
            // 解析其他信息（数量、地区、联系人、QQ、联系方式）
            if (description.includes('求购数量：')) {
              const qtyMatch = description.match(/求购数量：(\d+)([^\\n]+)/);
              if (qtyMatch) {
                this.form.quantity = parseInt(qtyMatch[1]) || 0;
                this.form.unit = qtyMatch[2].trim() || "斤";
              }
            }
            if (description.includes('所在地区：')) {
              const locMatch = description.match(/所在地区：([^\\n]+)/);
              if (locMatch) {
                this.form.location = locMatch[1].trim();
              }
            }
            if (description.includes('联系人：')) {
              const nameMatch = description.match(/联系人：([^，\\n]+)/);
              if (nameMatch) {
                this.form.contactName = nameMatch[1].trim();
              }
            }
            if (description.includes('QQ：')) {
              const qqMatch = description.match(/QQ：([^，\\n]+)/);
              if (qqMatch) {
                this.form.qq = qqMatch[1].trim();
              }
            }
            
            // 解析联系方式
            if (demand.contactInfo) {
              const contactInfo = demand.contactInfo;
              // 尝试从联系信息中提取电话号码
              const phoneMatch = contactInfo.match(/(1[3-9]\d{9})/);
              if (phoneMatch) {
                this.form.contact = phoneMatch[1];
              } else {
                // 如果没有匹配到电话号码，使用完整的联系信息
                this.form.contact = contactInfo.replace(/联系人：[^，]+，?/, "").replace(/，QQ：[^，]+/, "");
              }
            }
            
            // 如果需求中有 quantity 和 unit 字段，优先使用
            if (demand.quantity) {
              this.form.quantity = demand.quantity;
            }
            if (demand.unit) {
              this.form.unit = demand.unit;
            }
          } else {
            this.$message.error("获取需求详情失败");
            this.$router.push("/home/user/publishedneeds").catch(() => {});
          }
        })
        .catch((err) => {
          console.error("加载需求详情失败", err);
          this.$message.error("加载需求详情失败");
          this.$router.push("/home/user/publishedneeds").catch(() => {});
        });
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 检查用户是否登录
          const userId = this.$store.state.loginUserId;
          if (!userId) {
            this.$message.warning("请先登录");
            this.$router.push("/login").catch((err) => err);
            return;
          }

          this.submitting = true;
          
          // 构建完整的描述信息
          let fullContent = this.form.content;
          if (this.form.quantity > 0) {
            fullContent += `\n求购数量：${this.form.quantity}${this.form.unit}`;
          }
          if (this.form.location) {
            fullContent += `\n所在地区：${this.form.location}`;
          }
          if (this.form.contactName) {
            fullContent += `\n联系人：${this.form.contactName}`;
          }
          if (this.form.qq) {
            fullContent += `\nQQ：${this.form.qq}`;
          }
          fullContent += `\n联系方式：${this.form.contact}`;

          // 构建联系信息（包含多个联系方式）
          let contactInfo = this.form.contact;
          if (this.form.contactName) {
            contactInfo = `联系人：${this.form.contactName}，${contactInfo}`;
          }
          if (this.form.qq) {
            contactInfo += `，QQ：${this.form.qq}`;
          }

          const demandData = {
            title: this.form.title,
            description: fullContent,
            category: this.form.category,
            quantity: this.form.quantity > 0 ? this.form.quantity : null,
            unit: this.form.unit,
            contactInfo: contactInfo,
            userId: userId,
            status: "ACTIVE"
          };

          // 根据是否是编辑模式调用不同的API
          const apiCall = this.isEditMode
            ? updateDemand(this.demandId, demandData)
            : createDemand(demandData);

          apiCall
            .then((res) => {
              this.submitting = false;
              if (res.flag === true) {
                const successMsg = this.isEditMode ? "更新成功" : "发布成功";
                this.$message.success(res.message || successMsg);
                setTimeout(() => {
                  if (this.isEditMode) {
                    this.$router.push("/home/user/publishedneeds").catch((err) => err);
                  } else {
                    this.$router.push("/home/purchase").catch((err) => err);
                  }
                }, 1500);
              } else {
                const errorMsg = this.isEditMode ? "更新失败" : "发布失败";
                this.$message.error(res.message || errorMsg);
              }
            })
            .catch((err) => {
              this.submitting = false;
              const errorMsg = this.isEditMode ? "更新失败，请重试" : "发布失败，请重试";
              this.$message.error(errorMsg);
              console.error(this.isEditMode ? "更新需求失败" : "发布需求失败", err);
            });
        } else {
          this.$message.warning("请完善表单信息");
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.form.resetFields();
      this.form.quantity = 0;
      this.form.unit = "斤";
      this.$message.info("表单已重置");
    },
    goBack() {
      this.$router.go(-1);
    }
    ,
    goBackTrade() {
      this.$router.push('/home/trade').catch(() => {});
    }
  },
};
</script>

<style lang="less" scoped>
.publish-need-container {
  width: 1100px;
  margin: 0 auto;
  background: #fff;
  padding: 30px;
  min-height: 100vh;

  .publish-header {
    text-align: center;
    margin-bottom: 40px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;

    h2 {
      font-size: 28px;
      color: #333;
      margin: 0 0 10px 0;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;

      i {
        color: #67c23a;
      }
    }

    .subtitle {
      color: #999;
      font-size: 14px;
      margin: 0;
    }
  }

  .publish-form {
    max-width: 800px;
    margin: 0 auto;

    .el-form-item {
      margin-bottom: 30px;
    }

    .price-tip,
    .contact-tip {
      margin-left: 10px;
      color: #999;
      font-size: 12px;
    }

    .el-button {
      margin-right: 15px;
      padding: 12px 30px;
      font-size: 16px;

      i {
        margin-right: 5px;
      }
    }
  }
}
</style>

