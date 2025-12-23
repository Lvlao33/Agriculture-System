<template>
  <div class="publish-message">
    <el-form ref="form" :model="form" label-width="90px" class="publish-form">
      <el-form-item label="上架图片">
        <el-upload
          class="orders-img_el_upload"
          :action="upurl"
          :headers="upheaders"
          :limit="3"
          :auto-upload="true"
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
    handleError(err, file, fileList) {
      console.error('图片上传失败:', err);
      let errorMessage = "图片上传失败，请重试";
      
      if (err.response) {
        // 服务器返回了错误响应
        if (err.response.data && err.response.data.message) {
          errorMessage = err.response.data.message;
        } else if (err.response.status === 413) {
          errorMessage = "图片文件过大，请选择小于10MB的图片";
        } else if (err.response.status === 404) {
          errorMessage = "上传接口不存在，请检查后端服务是否启动";
        } else if (err.response.status === 500) {
          errorMessage = "服务器内部错误，请稍后重试";
        }
      } else if (err.message) {
        // 网络错误或其他错误
        if (err.message.includes('Network Error') || err.message.includes('timeout')) {
          errorMessage = "网络连接失败，请检查后端服务地址是否正确";
        } else {
          errorMessage = err.message;
        }
      }
      
      this.$message.error(errorMessage);
      
      // 从文件列表中移除失败的文件
      const index = this.fileList.findIndex(f => f.uid === file.uid);
      if (index > -1) {
        this.fileList.splice(index, 1);
      }
    },
    handleSuccess(response, file, fileList) {
      console.log('上传成功响应:', response, '文件:', file, '文件列表:', fileList);
      if (response && response.flag === true) {
        this.fileList = fileList;
        // 确保 response.data 是字符串
        const filename = typeof response.data === 'string' ? response.data : String(response.data || '');
        
        // 如果这是第一张图片，或者 form.picture 为空，则设置为主图
        if (filename && (!this.form.picture || fileList.length === 1)) {
          this.form.picture = filename;
        }
        
        // 确保 form.picture 始终有值（使用第一张成功上传的图片）
        if (!this.form.picture && fileList.length > 0) {
          // 从文件列表中查找第一张有响应数据的图片
          for (let i = 0; i < fileList.length; i++) {
            const f = fileList[i];
            if (f.response && f.response.flag === true && f.response.data) {
              this.form.picture = typeof f.response.data === 'string' 
                ? f.response.data 
                : String(f.response.data);
              break;
            }
          }
        }
        
        console.log('设置后的 form.picture:', this.form.picture);
        
        if (fileList.length >= 3) {
          this.uploadDisabled = true;
        }
        this.$message.success("图片上传成功");
      } else {
        this.$message.error(response?.message || "上传失败");
        // 上传失败时移除该文件
        const index = this.fileList.findIndex(f => f.uid === file.uid);
        if (index > -1) {
          this.fileList.splice(index, 1);
        }
      }
    },
    handleChange(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      // 更新 fileList
      this.fileList = fileList;
      // 确保 form.picture 有值（使用第一张图片）
      if (fileList.length > 0 && !this.form.picture) {
        const firstFile = fileList[0];
        if (firstFile.response && firstFile.response.data) {
          this.form.picture = typeof firstFile.response.data === 'string' 
            ? firstFile.response.data 
            : String(firstFile.response.data);
        }
      }
    },
    handleRemove(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      this.fileList = fileList;
      this.uploadDisabled = false;
      if (fileList.length === 0) {
        this.form.picture = "";
      } else {
        // 如果还有文件，使用第一张图片的路径
        const firstFile = fileList[0];
        if (firstFile.response && firstFile.response.data) {
          this.form.picture = typeof firstFile.response.data === 'string' 
            ? firstFile.response.data 
            : String(firstFile.response.data);
        } else if (firstFile.name) {
          this.form.picture = firstFile.name;
        }
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
      // 发布前再次确保 form.picture 有值
      if (this.fileList.length > 0 && !this.form.picture) {
        // 从文件列表中查找第一张有响应数据的图片
        for (let i = 0; i < this.fileList.length; i++) {
          const f = this.fileList[i];
          if (f.response && f.response.flag === true && f.response.data) {
            this.form.picture = typeof f.response.data === 'string' 
              ? f.response.data 
              : String(f.response.data);
            break;
          }
        }
      }
      
      // 检查是否有上传的图片（检查 fileList 或 form.picture）
      if (this.ctype !== "needs" && (this.fileList.length === 0 || !this.form.picture || this.form.picture === "")) {
        this.$message.warning("请至少上传一张商品图片");
        return;
      }
      
      console.log('发布商品，图片路径:', this.form.picture);
      
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