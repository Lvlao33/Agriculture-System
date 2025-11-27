<template>
  <div class="publish-message">
    <el-form ref="form" :model="form" label-width="90px" class="publish-form">
      <el-form-item label="上架图片">
        <el-upload
          class="orders-img_el_upload"
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
          <span class="orders-img_el_upload_btn" @click.stop="submitUpload">添加图片</span>
        </el-upload>
      </el-form-item>

      <el-form-item label="商品标题">
        <el-input
          v-model="form.title"
          style="width:800px;"
          placeholder="请输入商品标题"
        ></el-input>
      </el-form-item>

      <el-form-item label="商品描述">
        <el-input
          type="textarea"
          style="width:800px;"
          v-model="form.content"
          placeholder="请填写详细的商品描述"
          :rows="4"
        ></el-input>
      </el-form-item>

      <el-form-item label="商品分类">
        <el-cascader
          v-model="form.categoryPath"
          :options="categoryOptions"
          :props="{ expandTrigger: 'hover' }"
          clearable
          style="width:400px;"
          placeholder="请选择商品所属分类"
        ></el-cascader>
      </el-form-item>

      <el-form-item label="商品价格">
        <el-input
          v-model="form.price"
          style="width:150px;"
          placeholder="请输入价格"
        >
          <template slot="append">元</template>
        </el-input>
      </el-form-item>
    </el-form>

    <div class="submit-row">
      <el-button type="success" :disabled="isDisabled" @click="publishClick">
        发布信息
      </el-button>
    </div>
  </div>
</template>

<script>
import { addOrder } from "../api/order";

export default {
  name: "PublishMessage",
  data() {
    return {
      dialogImageUrl: "",
      dialogVisible: false,
      showBtnImg: true,
      noneBtnImg: false,
      limitCountImg: 3,
      upurl: this.$store.state.fileUploadRoad + "/file/upload/order",
      upheaders: {
        Authorization: window.localStorage.token,
      },
      fileList: [],
      imgurl: "",
      uploadDisabled: false,
      form: {
        title: "",
        content: "",
        price: "",
        picture: "",
        categoryPath: [],
      },
      categoryOptions: [
        {
          value: "vegetable",
          label: "蔬菜类",
          children: [
            { value: "leafy", label: "叶菜类（菠菜、生菜等）" },
            { value: "root", label: "根茎类（萝卜、土豆等）" },
            { value: "gourd", label: "瓜果类（黄瓜、番茄等）" },
          ],
        },
        {
          value: "fruit",
          label: "水果类",
          children: [
            { value: "berry", label: "浆果类（草莓、蓝莓等）" },
            { value: "stone", label: "核果类（桃子、李子等）" },
            { value: "citrus", label: "柑橘类（橙子、柚子等）" },
          ],
        },
        {
          value: "grain",
          label: "粮食作物",
          children: [
            { value: "cereals", label: "谷物类（水稻、小麦等）" },
            { value: "beans", label: "豆类（大豆、绿豆等）" },
          ],
        },
        {
          value: "livestock",
          label: "畜禽产品",
          children: [
            { value: "meat", label: "肉类（猪肉、牛肉等）" },
            { value: "eggMilk", label: "蛋奶类（鸡蛋、牛奶等）" },
          ],
        },
        {
          value: "specialty",
          label: "特色农产品",
          children: [
            { value: "organic", label: "有机食品" },
            { value: "geo", label: "地理标志产品" },
            { value: "handcraft", label: "手工艺品" },
          ],
        },
      ],
    };
  },
  props: {
    ctype: {
      type: String,
    },
  },
  computed: {
    isDisabled() {
      if (this.ctype === "needs") {
        return (
          this.form.title === "" ||
          this.form.content === ""
        );
      }
      return (
        this.form.title === "" ||
        this.form.content === "" ||
        this.form.price === "" ||
        !Array.isArray(this.form.categoryPath) ||
        this.form.categoryPath.length === 0
      );
    },
  },
  methods: {
    handleError(err) {
      this.$message.error("图片上传失败，请重试");
      console.log(err);
    },
    handleSuccess(response, file, fileList) {
      if (response.flag === true) {
        this.fileList = fileList;
        this.form.picture = response.data;
        if (fileList.length >= 3) {
          this.uploadDisabled = true;
        }
        this.$message.success("图片上传成功");
      } else {
        this.$message.error(response.message || "上传失败");
      }
    },
    handleChange(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
    },
    handleRemove(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      this.fileList = fileList;
      this.uploadDisabled = false;
      if (fileList.length === 0) {
        this.form.picture = "";
      }
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
    getCategoryLabel(path) {
      if (!Array.isArray(path) || path.length === 0) {
        return "";
      }
      const labels = [];
      let currentLevel = this.categoryOptions;
      path.forEach((value) => {
        const node = currentLevel.find((item) => item.value === value);
        if (node) {
          labels.push(node.label);
          currentLevel = node.children || [];
        }
      });
      return labels.join(" / ");
    },
    publishClick() {
      if (this.ctype !== "needs" && this.form.picture === "") {
        this.$message.warning("请至少上传一张商品图片");
        return;
      }
      const categoryLabel = this.getCategoryLabel(this.form.categoryPath);
      addOrder({
        title: this.form.title,
        content: this.form.content,
        price: this.form.price,
        type: this.ctype,
        picture: this.form.picture,
        category: categoryLabel,
      })
        .then((res) => {
          if (res.flag === true) {
            this.$message.success(res.message || "发布成功");
            this.$router.push("/home/user/published" + this.ctype);
          } else {
            this.$message.error(res.message || "发布失败");
          }
        })
        .catch((err) => {
          console.log("发布失败", err);
          this.$message.error("发布失败，请稍后再试");
        });
    },
  },
};
</script>

<style lang="less" scoped>
.disUoloadSty .el-upload--picture-card {
  display: none;
}

.publish-message {
  width: 1100px;
  margin: 20px auto;
  padding: 10px 20px 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .publish-form {
    margin-top: 20px;
  }

  .orders-img_el_upload {
    width: 100%;
    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      &:hover {
        border-color: #409eff;
      }
    }
  }

  .submit-row {
    text-align: center;
    margin-top: 20px;

    .el-button {
      width: 160px;
      height: 44px;
      font-size: 16px;
    }
  }
}
</style>