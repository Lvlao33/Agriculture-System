<!-- 农业知识详情 -->
<template>
  <div class="knowledge-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button" @click="goBack">
      <i class="el-icon-arrow-left"></i>
      <span>返回</span>
    </div>

    <!-- 标题和元信息 -->
    <div class="detail-header">
      <div class="type-badge" :class="getTypeClass()">
        <i :class="getTypeIcon()"></i>
        <span>{{ getTypeName() }}</span>
      </div>
      <h1 class="detail-title">{{updateInfo.title}}</h1>
      <div class="detail-meta">
        <span class="meta-item">
          <i class="el-icon-user"></i>
          <span>作者：{{updateInfo.ownName || '未知'}}</span>
        </span>
        <span class="meta-item">
          <i class="el-icon-time"></i>
          <span>发布时间：{{updateInfo.updateTime | formatTimer}}</span>
        </span>
      </div>
    </div>

    <!-- 媒体内容 -->
    <div class="detail-media" v-if="updateInfo.picPath">
      <video 
        v-if="updateInfo.type==='mp4'||updateInfo.type==='MP4'" 
        class="detail-video"
        :src="$store.state.imgShowRoad + '/file/' + updateInfo.picPath" 
        controls
        preload="metadata"
      ></video>
      <img 
        v-else 
        class="detail-image"
        :src="$store.state.imgShowRoad + '/file/' + updateInfo.picPath" 
        alt="知识图片"
      />
    </div>

    <!-- 文字内容 -->
    <div class="detail-content">
      <div class="content-text" v-html="formatContent(updateInfo.content)"></div>
    </div>

    <!-- 评论区域 -->
    <div class="comment-section">
      <h3 class="comment-title">
        <i class="el-icon-chat-line-square"></i>
        评论 ({{commentArray.length||0}})
      </h3>
      
      <div class="comment-input-area">
        <el-input 
          type="textarea" 
          v-model="content" 
          :rows="4"
          placeholder="写下您的评论..."
          maxlength="500"
          show-word-limit
        ></el-input>
        <div class="comment-actions">
          <el-button type="success" @click="handleComment" :disabled="!content.trim()">
            <i class="el-icon-check"></i>
            发表评论
          </el-button>
        </div>
      </div>

      <div class="comment-list">
        <div 
          class="comment-item" 
          v-for="(item,index) in commentArray" 
          :key="index"
        >
          <div class="comment-avatar">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="comment-body">
            <div class="comment-content">{{item.content}}</div>
            <div class="comment-meta">
              <span class="comment-author">{{item.ownName || '匿名用户'}}</span>
              <span class="comment-time">{{item.createTime|formatTimer2}}</span>
            </div>
          </div>
        </div>
        
        <div v-if="commentArray.length === 0" class="no-comments">
          <i class="el-icon-chat-line-round"></i>
          <p>暂无评论，快来发表第一条评论吧！</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { selectKnowledgeById,selectComment,addComment } from "../api/knowledge";

export default {
  data(){
    return{
      updateInfo:{},
      content:'',
      commentArray:[]
    }
  },
  filters: {
    formatTimer: function(value) {
      if (!value) return '';
      let date = new Date(value);
      let y = date.getFullYear();
      let MM = date.getMonth() + 1;
      MM = MM < 10 ? "0" + MM : MM;
      let d = date.getDate();
      d = d < 10 ? "0" + d : d;
      let h = date.getHours();
      h = h < 10 ? "0" + h : h;
      let m = date.getMinutes();
      m = m < 10 ? "0" + m : m;
      let s = date.getSeconds();
      s = s < 10 ? "0" + s : s;
      return y + "-" + MM + "-" + d + " " + h + ":" + m;
    },
    formatTimer2: function(value) {
      if (!value) return '';
      let date = new Date(value);
      let y = date.getFullYear();
      let MM = date.getMonth() + 1;
      MM = MM < 10 ? "0" + MM : MM;
      let d = date.getDate();
      d = d < 10 ? "0" + d : d;
      let h = date.getHours();
      h = h < 10 ? "0" + h : h;
      let m = date.getMinutes();
      m = m < 10 ? "0" + m : m;
      let s = date.getSeconds();
      s = s < 10 ? "0" + s : s;
      return y + "-" + MM + "-" + d + " "+h+":"+m;
    }
  },
  computed: {
    knowledgeType() {
      if (!this.updateInfo.type && !this.updateInfo.picPath) {
        return 'text';
      }
      if (this.updateInfo.type === 'mp4' || this.updateInfo.type === 'MP4') {
        return 'video';
      }
      if (this.updateInfo.picPath) {
        return 'image';
      }
      return 'text';
    }
  },
  methods:{
    getData(){
      this.$store.commit("updateActiveIndex", "4");
      selectKnowledgeById({
        knowledgeId: this.$route.params.id
      }).then((res) => {
        let tmp = res.data;
        if (tmp.picPath) {
          const flieArr = tmp.picPath.split('.');
          tmp.type = flieArr[flieArr.length - 1];
        } else {
          tmp.type = 'text';
        }
        this.updateInfo = tmp;
      })
      .catch((err) => {
        console.log(err);
      });
    },
    getTypeClass() {
      if (this.knowledgeType === 'video') return 'type-video';
      if (this.knowledgeType === 'image') return 'type-image';
      return 'type-text';
    },
    getTypeIcon() {
      if (this.knowledgeType === 'video') return 'el-icon-video-camera';
      if (this.knowledgeType === 'image') return 'el-icon-picture';
      return 'el-icon-document';
    },
    getTypeName() {
      if (this.knowledgeType === 'video') return '视频知识';
      if (this.knowledgeType === 'image') return '图片知识';
      return '文字知识';
    },
    formatContent(content) {
      if (!content) return '';
      // 将换行符转换为 <br>
      return content.replace(/\n/g, '<br>');
    },
    goBack() {
      this.$router.go(-1);
    },
    // 查询评论
    getCommentData(){
      selectComment({
        knowledgeId: this.$route.params.id
      }).then(res => {
        this.commentArray = res.data || []
      }).catch(err=>{
        console.log(err)
      })
    },
    handleComment(){
      if(this.content===''){
        this.$message.error('评论内容不能为空！')
        return
      }
      if(localStorage.getItem('token')){
        addComment({
          knowledgeId: this.$route.params.id,
          content: this.content
        }).then(res=>{
          this.content=''
          this.$message.success('评论成功！')
          this.getCommentData()
        }).catch(err=>{
          console.log(err)
        })
      }else{
        this.$message.error('请先登录')
      }
    }
  },
  mounted(){
    this.getData()
    this.getCommentData()
  }
}
</script>

<style lang="less" scoped>
.knowledge-detail-container{
  width: 1100px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
  padding: 30px;
  margin-top: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .back-button {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 8px 16px;
    margin-bottom: 20px;
    color: #606266;
    cursor: pointer;
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
      color: #67c23a;
    }

    i {
      font-size: 16px;
    }
  }

  .detail-header {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;

    .type-badge {
      display: inline-flex;
      align-items: center;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 500;
      color: #fff;
      margin-bottom: 15px;

      i {
        margin-right: 6px;
        font-size: 14px;
      }

      &.type-text {
        background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
      }

      &.type-image {
        background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      }

      &.type-video {
        background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
      }
    }

    .detail-title {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
      margin: 0 0 15px 0;
      line-height: 1.4;
    }

    .detail-meta {
      display: flex;
      gap: 20px;
      color: #909399;
      font-size: 14px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 5px;

        i {
          font-size: 14px;
        }
      }
    }
  }

  .detail-media {
    margin: 30px 0;
    display: flex;
    justify-content: center;
    background: #f5f7fa;
    border-radius: 8px;
    padding: 20px;
    overflow: hidden;

    .detail-video {
      width: 100%;
      max-width: 900px;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .detail-image {
      max-width: 100%;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .detail-content {
    margin: 40px 0;
    padding: 30px;
    background: #fafafa;
    border-radius: 8px;
    line-height: 1.8;

    .content-text {
      font-size: 16px;
      color: #606266;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }

  .comment-section {
    margin-top: 50px;
    padding-top: 30px;
    border-top: 2px solid #f0f0f0;

    .comment-title {
      font-size: 20px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 20px;
      display: flex;
      align-items: center;
      gap: 8px;

      i {
        color: #67c23a;
      }
    }

    .comment-input-area {
      margin-bottom: 30px;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;

      .comment-actions {
        margin-top: 15px;
        display: flex;
        justify-content: flex-end;
      }
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: 15px;
        padding: 20px;
        margin-bottom: 15px;
        background: #fafafa;
        border-radius: 8px;
        transition: background 0.3s;

        &:hover {
          background: #f0f0f0;
        }

        .comment-avatar {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 18px;
          flex-shrink: 0;
        }

        .comment-body {
          flex: 1;

          .comment-content {
            font-size: 14px;
            color: #606266;
            line-height: 1.6;
            margin-bottom: 10px;
            word-break: break-word;
          }

          .comment-meta {
            display: flex;
            gap: 15px;
            font-size: 12px;
            color: #909399;

            .comment-author {
              font-weight: 500;
            }
          }
        }
      }

      .no-comments {
        text-align: center;
        padding: 40px 20px;
        color: #999;

        i {
          font-size: 48px;
          margin-bottom: 15px;
          display: block;
        }

        p {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
