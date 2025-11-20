<template>
  <div class="publish-supply-container">
    <div class="publish-header">
      <h2><i class="el-icon-plus"></i> 发布货源</h2>
      <p class="subtitle">填写商品信息，让更多买家看到您的商品</p>
    </div>

    <el-form 
      ref="form" 
      :model="form" 
      :rules="rules" 
      label-width="120px" 
      class="publish-form"
    >
      <el-form-item label="商品名称" prop="title">
        <el-input 
          v-model="form.title" 
          placeholder="请输入商品名称"
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
          <el-option label="水果类" value="fruit"></el-option>
          <el-option label="蔬菜类" value="vegetable"></el-option>
          <el-option label="粮食类" value="grain"></el-option>
          <el-option label="畜牧类" value="livestock"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="商品图片" prop="picture">
        <el-upload
          class="upload-demo"
          :action="upurl"
          :headers="upheaders"
          :limit="3"
          list-type="picture-card"
          :on-change="handleChange"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :on-success="handleSuccess"
          :on-error="handleError"
          :file-list="fileList"
          :class="{ disUoloadSty: noneBtnImg }"
          ref="upload"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
        <div class="upload-tip">
          <p>支持 JPG、PNG 格式，最多上传 3 张图片</p>
        </div>
      </el-form-item>

      <el-form-item label="商品描述" prop="content">
        <el-input 
          type="textarea" 
          v-model="form.content" 
          placeholder="请详细描述商品的特点、产地、品质等信息"
          :rows="6"
          style="width: 600px;"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="商品价格" prop="price">
        <el-input 
          v-model="form.price" 
          placeholder="请输入商品价格（元/单位）"
          style="width: 300px;"
        >
          <template slot="prepend">?</template>
        </el-input>
        <span class="price-tip">如：10.00 或 面议</span>
      </el-form-item>

      <el-form-item label="商品单位">
        <el-select 
          v-model="form.unit" 
          placeholder="请选择单位"
          style="width: 200px;"
        >
          <el-option label="斤" value="斤"></el-option>
          <el-option label="公斤" value="公斤"></el-option>
          <el-option label="吨" value="吨"></el-option>
          <el-option label="箱" value="箱"></el-option>
          <el-option label="件" value="件"></el-option>
          <el-option label="袋" value="袋"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="库存数量">
        <el-input-number 
          v-model="form.stock" 
          :min="0" 
          :max="999999"
          placeholder="请输入库存数量"
          style="width: 300px;"
        />
      </el-form-item>

      <el-form-item label="产地">
        <el-input 
          v-model="form.origin" 
          placeholder="请输入商品产地"
          style="width: 300px;"
        />
      </el-form-item>

      <el-form-item label="联系方式">
        <el-input 
          v-model="form.contact" 
          placeholder="请输入联系电话或微信"
          style="width: 300px;"
        />
      </el-form-item>

      <el-form-item>
        <el-button 
          type="primary" 
          size="large" 
          :loading="submitting"
          @click="submitForm"
        >
          <i class="el-icon-check"></i> 发布商品
        </el-button>
        <el-button 
          size="large" 
          @click="resetForm"
        >
          <i class="el-icon-refresh-left"></i> 重置
        </el-button>
        <el-button 
          size="large" 
          @click="goBack"
        >
          <i class="el-icon-back"></i> 返回
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="dialogVisible" width="50%">
      <img width="100%" :src="dialogImageUrl" alt="" />
    </el-dialog>
  </div>
</template>

<script>
import { addOrder } from "../api/order";

export default {
  name: "PublishSupply",
  data() {
    return {
      submitting: false,
      dialogImageUrl: "",
      dialogVisible: false,
      noneBtnImg: false,
      limitCountImg: 3,
      upurl: this.$store.state.fileUploadRoad + "/file/upload/order",
      upheaders: {
        Authorization: window.localStorage.token,
      },
      fileList: [],
      form: {
        title: "",
        category: "",
        content: "",
        price: "",
        picture: "",
        unit: "斤",
        stock: 0,
        origin: "",
        contact: ""
      },
      rules: {
        title: [
          { required: true, message: "请输入商品名称", trigger: "blur" },
          { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" }
        ],
        category: [
          { required: true, message: "请选择商品分类", trigger: "change" }
        ],
        content: [
          { required: true, message: "请输入商品描述", trigger: "blur" },
          { min: 10, max: 500, message: "长度在 10 到 500 个字符", trigger: "blur" }
        ],
        price: [
          { required: true, message: "请输入商品价格", trigger: "blur" }
        ],
        picture: [
          { required: true, message: "请上传商品图片", trigger: "change" }
        ]
      }
    };
  },
  methods: {
    handleError(err, file, fileList) {
      this.$message.error("上传失败，请重试");
      console.log(err);
    },
    handleSuccess(response, file, fileList) {
      if (response.flag == true) {
        this.fileList = fileList;
        this.form.picture = response.data;
        if (fileList.length >= 3) {
          this.noneBtnImg = true;
        }
        this.$message.success(response.message || "图片上传成功");
      } else {
        this.$message.error(response.data || "上传失败");
      }
    },
    handleChange(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
    },
    handleRemove(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      this.fileList = fileList;
      if (fileList.length === 0) {
        this.form.picture = "";
      }
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (!this.form.picture) {
            this.$message.warning("请上传商品图片");
            return;
          }
          
          this.submitting = true;
          addOrder({
            title: this.form.title,
            content: this.form.content,
            price: this.form.price,
            type: "goods",
            picture: this.form.picture,
            category: this.form.category,
            unit: this.form.unit,
            stock: this.form.stock,
            origin: this.form.origin,
            contact: this.form.contact
          })
            .then((res) => {
              this.submitting = false;
              if (res.flag == true) {
                this.$message.success(res.message || "发布成功");
                setTimeout(() => {
                  this.$router.push("/home/goods").catch((err) => err);
                }, 1500);
              } else {
                this.$message.error(res.data || "发布失败");
              }
            })
            .catch((err) => {
              this.submitting = false;
              this.$message.error("发布失败，请重试");
              console.log("添加失败", err);
            });
        } else {
          this.$message.warning("请完善表单信息");
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.form.resetFields();
      this.fileList = [];
      this.form.picture = "";
      this.noneBtnImg = false;
      this.$message.info("表单已重置");
    },
    goBack() {
      this.$router.go(-1);
    }
  },
  mounted() {
    this.$store.commit("updateActiveIndex", "2");
  }
};
</script>

<style lang="less" scoped>
.publish-supply-container {
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

    .price-tip {
      margin-left: 10px;
      color: #999;
      font-size: 12px;
    }

    .upload-tip {
      margin-top: 10px;
      color: #999;
      font-size: 12px;

      p {
        margin: 0;
      }
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

.disUoloadSty /deep/ .el-upload--picture-card {
  display: none;
}

/deep/ .el-upload--picture-card {
  width: 148px;
  height: 148px;
  line-height: 148px;
}

/deep/ .el-upload-list--picture-card .el-upload-list__item {
  width: 148px;
  height: 148px;
}
</style>

