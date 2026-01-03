<template>
  <div class="user-avatar">
    <div class="avatar-display" @click="triggerFileSelect">
      <img
        v-if="currentAvatar"
        :src="$store.state.imgShowRoad + '/file/' + currentAvatar"
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
      :before-upload="beforeUpload"
      accept="image/*"
      ref="upload">
      <div style="display: none;"></div>
    </el-upload>
  </div>
</template>

<script>
import { loginUpdateByUsername } from '@/api/user'

export default {
  data() {
    return {
      dialogImageUrl: "",
      dialogVisible: false,
      showBtnImg: true,
      noneBtnImg: false,
      limitCountImg: 1,
      upurl: '',
      upheaders: {},
      fileList: [],
      imgurl: "",
      currentAvatar: this.cUserAvatar || '',
    };
  },
  props: {
    cUserAvatar: {
      type: String,
      default: '',
    },
  },
  watch: {
    cUserAvatar(newVal) {
      this.currentAvatar = newVal;
    }
  },
  methods: {
    triggerFileSelect() {
      // 触发文件选择
      const uploadEl = this.$refs.upload;
      if (uploadEl && uploadEl.$el) {
        const input = uploadEl.$el.querySelector('input[type="file"]');
        if (input) {
          input.click();
        }
      }
    },
    beforeUpload(file) {
      // 验证文件类型
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('只能上传图片文件！');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB！');
        return false;
      }
      return true;
    },
    handleError(err, file, fileList) {
      console.error('上传失败:', err);
      console.error('文件信息:', file);
      console.error('错误详情:', err);
      console.error('上传 URL:', this.upurl);
      console.error('请求头:', this.upheaders);
      
      // 尝试从错误响应中获取错误信息
      let errorMsg = '头像上传失败，请稍后重试！';
      
      // 检查是否是 404 错误（接口不存在）
      if (err && err.message && err.message.includes('Cannot POST')) {
        errorMsg = '接口不存在，请确保后端服务已重启并加载了 /file/upload/avatar 接口';
        console.error('⚠️ 404 错误：后端接口不存在，需要重启后端服务');
      } else if (err && err.message) {
        errorMsg = err.message;
      } else if (file && file.response) {
        errorMsg = file.response.message || file.response.data || errorMsg;
      } else if (file && file.status === 404) {
        errorMsg = '接口不存在（404），请重启后端服务';
      } else if (file && file.status === 401) {
        errorMsg = '认证失败（401），请检查 token 是否正确';
      } else if (file && file.status === 403) {
        errorMsg = '无权限（403），请检查 token 是否有效';
      }
      
      this.$message.error(errorMsg);
    },
    handleSuccess(response, file, fileList) {
      console.log('上传成功响应:', response);
      console.log('文件响应:', file.response);
      
      // 处理响应，兼容不同的响应格式
      let avatarUrl = '';
      let message = '';
      
      // 优先使用 file.response（el-upload 的标准格式）
      const res = file.response || response;
      
      if (res) {
        if (res.flag === true || res.success === true) {
          avatarUrl = res.data || res.url || res.fileName || '';
          message = res.message || '头像上传成功！';
        } else {
          const errorMsg = res.message || res.data || '上传失败';
          this.$message.error(errorMsg);
          return;
        }
      } else {
        this.$message.error('未收到服务器响应');
        return;
      }

      if (avatarUrl) {
        this.currentAvatar = avatarUrl;
        // 通过事件通知父组件更新头像
        this.$emit('avatar-updated', avatarUrl);
        // 更新 store 中的头像
        this.$store.commit('updateLoginUserAvatar', avatarUrl);
        
        // 更新用户头像到数据库
        // 优先从 localStorage 获取 username（登录时存储），如果没有则从 store 获取
        const username = localStorage.getItem('username') 
          || (this.$store.state.user && this.$store.state.user.username)
          || '';
        
        if (username) {
          loginUpdateByUsername({
            username: username,
            avatar: avatarUrl
          }).then((res) => {
            if (res.flag || res.success) {
              console.log('头像已保存到数据库:', avatarUrl);
            } else {
              console.warn('头像保存到数据库失败:', res.message || res.data);
            }
          }).catch((err) => {
            console.error('更新用户头像到数据库失败:', err);
            // 不显示错误提示，因为文件已经上传成功，后端也会自动更新
          });
        } else {
          console.warn('无法获取用户名，跳过前端数据库更新（后端会自动更新）');
        }
        
        this.$message.success(message);
      } else {
        console.error('上传响应中没有头像地址:', res);
        this.$message.error('上传成功但未返回头像地址');
      }
    },
    handleChange(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
    },
    handleRemove(file, fileList) {
      this.noneBtnImg = fileList.length >= this.limitCountImg;
      this.fileList = [];
      this.uploadDisabled = false;
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    submitUpload() {
      // 这个方法保留用于外部调用，但实际应该通过 triggerFileSelect 触发
      this.triggerFileSelect();
    },
  },
  mounted() {
    // 初始化上传配置
    // 使用完整 URL 确保请求能正确到达后端
    // 如果代理有问题，直接使用完整 URL 可以绕过代理问题
    const isDevelopment = process.env.NODE_ENV === 'development';
    this.upurl = isDevelopment 
      ? 'http://localhost:8080/file/upload/avatar'  // 开发环境使用完整 URL
      : '/file/upload/avatar';  // 生产环境使用相对路径（通过 nginx 代理）
    
    // 获取 token，与其他组件保持一致（直接使用 token，不添加 Bearer 前缀）
    // 注意：el-upload 组件使用原生 XMLHttpRequest，不会经过 axios 拦截器
    const token = window.localStorage.token || localStorage.getItem('token') || this.$store.state.token || '';
    this.upheaders = {
      Authorization: token || '',
    };
    console.log('头像上传配置:', {
      url: this.upurl,
      hasToken: !!token,
      tokenLength: token ? token.length : 0,
      tokenPreview: token ? (token.length > 20 ? token.substring(0, 20) + '...' : token) : '无token'
    });
    
    // 如果 token 为空，给出警告
    if (!token) {
      console.warn('⚠️ 未找到 token，上传可能会失败');
    }
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