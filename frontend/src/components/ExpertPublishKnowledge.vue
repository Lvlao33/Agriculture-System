<template>
  <div class="publish-message">
    <el-form label-width="100px" :model="form" style="margin-top:50px;">
      <el-form-item label="封面图片">
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
          ref="upload">
          <span class="orders-img_el_upload_btn" @click.stop="submitUpload">添加图片</span>
        </el-upload>
      </el-form-item>
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="请输入知识标�?"></el-input>
      </el-form-item>
      <el-form-item label="内容">
        <el-input type="textarea" v-model="form.content" placeholder="请输入知识正文内�?"></el-input>
      </el-form-item>
      <el-form-item label="相关链接">
        <el-input v-model="form.url" placeholder="可选：填写相关政策或技术文件链�?"></el-input>
      </el-form-item>
    </el-form>
    <div style="display:flex;justify-content:center;">
      <el-button type="success" :disabled="isDisabled" @click="publishClick">发布知识</el-button>
    </div>
  </div>
</template>

<script>
import { addKnowledge } from "../api/knowledge";
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
      // picPath: "",
      imgurl: "",
      // title: "",
      // content: "",
      form:{
        title:'',
        content:'',
        picPath:'',
        url:''
      }
    };
  },
  props: {
    ctype: {
      type: String,
    },
  },
  computed: {
    isDisabled() {
      return this.form.title == "" || this.form.content == ""
    },
    isActive() {},
  },
  methods: {
    handleError(err, file, fileList) {
      this.$message.error("图片上传失败，请稍后重试");
      console.log(err);
    },
    handleSuccess(response, file, fileList) {
      if (file.response.flag == true) {
        this.fileList = fileList;
        this.form.picPath = file.response.data;
        if (fileList.length >= 3) {
          this.uploadDisabled = true;
        }
        this.$message.success(file.response.message || '图片上传成功');
      } else {
        alert(file.response.data);
      }
    },
    handleChange(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
    },
    handleRemove(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      this.fileList.pop();
      this.uploadDisabled = false;
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
    publishClick() {
      addKnowledge({
        title: this.form.title,
        content: this.form.content,
        picPath: this.form.picPath,
        url: this.form.url
      })
        .then((res) => {
          if (res.flag == true) {
            this.$message.success(res.message || '知识已发�?');
            // 发布成功后跳转到专家指导的农业知识页�?
            this.$router.push("/home/knowledge");
          } else {
            this.$message.error(res.message || '发布失败');
          }
        })
        .catch((err) => {
          console.log("添加知识失败", err);
          this.$message.error('发布失败，请稍后重试');
        });
    },
  },
};
</script>

<style lang="less" scoped>
.disUoloadSty .el-upload--picture-card {
  display: none; /* 涓婁紶鎸夐挳闅愯�? */
}
.publish-message {
  width: 1100px;
  margin: 20px auto;
  padding: 10px 20px;
  background: #fff;
  // min-height: 100%;
  // height: auto;
  .orders-img_el_upload {
    width: 1000px;
    
    height: 148px;
    .el-upload {
      //   width: 50px;
      //   height: 20px;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      &:hover {
        border-color: #409eff;
      }
      .el-upload__input {
        position: absolute;
        left: -1000px;
      }
    }
  }
}
</style>