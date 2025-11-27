<template>
  <div class="user-avatar">
    <div class="avatar-display" @click="submitUpload">
      <img
        v-if="cUserAvatar"
        :src="$store.state.imgShowRoad + '/file/' + cUserAvatar"
        alt="头像"
        class="avatar"
      />
      <div v-else class="avatar placeholder">
        <i class="el-icon-user"></i>
      </div>
      <div class="avatar-overlay">
        <i class="el-icon-camera"></i>
        <span>更换头像</span>
      </div>
    </div>
    <el-upload
      class="avatar-uploader"
      :action="upurl"
      :headers="upheaders"
      :limit="1"
      :show-file-list="false"
      :on-change="handleChange"
      :on-success="handleSuccess"
      :on-error="handleError"
      ref="upload">
    </el-upload>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dialogImageUrl: "",
      dialogVisible: false,
      showBtnImg: true,
      noneBtnImg: false,
      limitCountImg: 1,
      upurl: this.$store.state.fileUploadRoad + "/file/upload/avatar",
      upheaders: {
        Authorization: window.localStorage.token,
      },
      fileList: [],
      imgurl: "",
    };
  },
  props: {
    cUserAvatar: {
      type: String,
    },
  },
  methods: {
    handleError(err, file, fileList) {
      this.$message({
        message: "上传失败！",
        type: "success",
      });
      console.log(err);
    },
    handleSuccess(response, file, fileList) {
      if (file.response.flag == true) {
        this.fileList = fileList;
        this.cUserAvatar = file.response.data;
        console.log("头像：：：")
        console.log(file.response.data)
        console.log(fileList)
        if (fileList.length >= 1) {
          this.uploadDisabled = true;
        }
        alert(file.response.message);
      } else {
        alert(file.response.data);
      }
    },
    handleChange(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
    },
    handleRemove(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      this.fileList = [];
      // this.form.photo = "";
      this.uploadDisabled = false;
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
  },
};
</script>

<style lang="less" scoped>
.user-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .avatar-display {
    position: relative;
    cursor: pointer;
    border-radius: 50%;
    overflow: hidden;
    
    &:hover .avatar-overlay {
      opacity: 1;
    }
  }
  
  .avatar {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    display: block;
  }

  .avatar.placeholder {
    background: #e5e7f1;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #9ca3af;
    border: 2px solid #d0d4e6;

    i {
      font-size: 42px;
    }
  }
  
  .avatar-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #fff;
    opacity: 0;
    transition: opacity 0.3s;
    border-radius: 50%;
    
    i {
      font-size: 24px;
      margin-bottom: 4px;
    }
    
    span {
      font-size: 12px;
    }
  }
  
  .avatar-uploader {
    display: none;
  }
}
</style>