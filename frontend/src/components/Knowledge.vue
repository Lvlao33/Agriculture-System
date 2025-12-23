<template>
  <div class="knowledge-box">
    <!-- 文字知识卡片 -->
    <div 
      v-for="(item, index) in textKnowledges" 
      :key="'text-' + index"
      class="knowledge-card knowledge-text"
      @click="handleDetail(item)"
    >
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <div class="knowledge-header">
          <div class="type-badge type-text">
            <i class="el-icon-document"></i>
            <span>文字知识</span>
          </div>
          <div class="knowledge-meta">
            <span class="author">
              <i class="el-icon-user"></i>
              {{ item.ownName || '未知' }}
            </span>
            <span class="date">
              <i class="el-icon-time"></i>
              {{ formatDate(item.updateTime || item.createTime) }}
            </span>
          </div>
        </div>
        <h3 class="knowledge-title">{{ item.title }}</h3>
        <p class="knowledge-content">{{ item.content }}</p>
        <div class="knowledge-footer">
          <span class="read-more">阅读全文 <i class="el-icon-arrow-right"></i></span>
        </div>
      </el-card>
    </div>

    <!-- 图片知识卡片 -->
    <div 
      v-for="(item, index) in imageKnowledges" 
      :key="'image-' + index"
      class="knowledge-card knowledge-image"
      @click="handleDetail(item)"
    >
      <el-card shadow="hover" :body-style="{ padding: '0' }">
        <div class="image-wrapper">
          <img 
            :src="$store.state.imgShowRoad + '/file/' + item.picPath" 
            alt="知识图片"
            class="knowledge-img"
          />
          <div class="type-badge type-image">
            <i class="el-icon-picture"></i>
            <span>图片知识</span>
          </div>
        </div>
        <div class="knowledge-info">
          <div class="knowledge-header">
            <h3 class="knowledge-title">{{ item.title }}</h3>
            <div class="knowledge-meta">
              <span class="author">
                <i class="el-icon-user"></i>
                {{ item.ownName || '未知' }}
              </span>
              <span class="date">
                <i class="el-icon-time"></i>
                {{ formatDate(item.updateTime || item.createTime) }}
              </span>
            </div>
          </div>
          <p class="knowledge-content">{{ item.content }}</p>
          <div class="knowledge-footer">
            <span class="read-more">查看详情 <i class="el-icon-arrow-right"></i></span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 视频知识卡片 -->
    <div 
      v-for="(item, index) in videoKnowledges" 
      :key="'video-' + index"
      class="knowledge-card knowledge-video"
      @click="handleDetail(item)"
    >
      <el-card shadow="hover" :body-style="{ padding: '0' }">
        <div class="video-wrapper">
          <video 
            :src="$store.state.imgShowRoad + '/file/' + item.picPath" 
            class="knowledge-video-player"
            preload="metadata"
            @mouseenter="playPreview($event)"
            @mouseleave="pausePreview($event)"
          ></video>
          <div class="video-overlay">
            <div class="play-button">
              <i class="el-icon-video-play"></i>
            </div>
            <div class="type-badge type-video">
              <i class="el-icon-video-camera"></i>
              <span>视频知识</span>
            </div>
          </div>
        </div>
        <div class="knowledge-info">
          <div class="knowledge-header">
            <h3 class="knowledge-title">{{ item.title }}</h3>
            <div class="knowledge-meta">
              <span class="author">
                <i class="el-icon-user"></i>
                {{ item.ownName || '未知' }}
              </span>
              <span class="date">
                <i class="el-icon-time"></i>
                {{ formatDate(item.updateTime || item.createTime) }}
              </span>
            </div>
          </div>
          <p class="knowledge-content">{{ item.content }}</p>
          <div class="knowledge-footer">
            <span class="read-more">观看视频 <i class="el-icon-arrow-right"></i></span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 空状�? -->
    <div v-if="cknowledges.length === 0" class="empty-knowledge">
      <i class="el-icon-document-delete"></i>
      <p>暂无知识内容</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "Knowledge",
  props: {
    cknowledges: {
      type: Array,
      default: () => []
    },
  },
  computed: {
    textKnowledges() {
      return this.cknowledges.filter(item => {
        return !item.type || (item.type !== 'mp4' && item.type !== 'MP4' && !item.picPath);
      });
    },
    imageKnowledges() {
      return this.cknowledges.filter(item => {
        return item.type && item.type !== 'mp4' && item.type !== 'MP4' && item.picPath;
      });
    },
    videoKnowledges() {
      return this.cknowledges.filter(item => {
        return item.type === 'mp4' || item.type === 'MP4';
      });
    }
  },
  methods: {
    handleDetail(item) {
      // 如果有外部链接，则优先跳转到外站链接
      if (item.url) {
        window.open(item.url, '_blank');
        return;
      }
      // 否则跳转到站内的知识详情页
      this.$router.push(`/home/knowledge/${item.knowledgeId}`).catch((err) => err);
    },
    playPreview(event) {
      const video = event.target;
      if (video.tagName === 'VIDEO') {
        video.play().catch(() => {
          // 自动播放失败时忽略错�?
        });
      }
    },
    pausePreview(event) {
      const video = event.target;
      if (video.tagName === 'VIDEO') {
        video.pause();
        video.currentTime = 0;
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  }
};
</script>

<style lang="less" scoped>
.knowledge-box {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  margin-bottom: 20px;

  .knowledge-card {
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
    height: 100%;

    &:hover {
      transform: translateY(-5px);
    }

    .el-card {
      height: 100%;
      border-radius: 8px;
      overflow: hidden;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }

    .type-badge {
      display: inline-flex;
      align-items: center;
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
      color: #fff;

      i {
        margin-right: 4px;
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

    .knowledge-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;

      .knowledge-meta {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 4px;
        font-size: 12px;
        color: #909399;

        span {
          display: flex;
          align-items: center;
          gap: 4px;

          i {
            font-size: 12px;
          }
        }
      }
    }

    .knowledge-title {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
      margin: 0 0 12px 0;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .knowledge-content {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
      margin: 0 0 15px 0;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .knowledge-footer {
      display: flex;
      justify-content: flex-end;
      padding-top: 10px;
      border-top: 1px solid #f0f0f0;

      .read-more {
        color: #67c23a;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 5px;
        transition: all 0.3s;

        i {
          transition: transform 0.3s;
        }
      }
    }

    &:hover .read-more i {
      transform: translateX(3px);
    }
  }

  // 文字知识样式
  .knowledge-text {
    .knowledge-header {
      margin-bottom: 15px;
    }
  }

  // 图片知识样式
  .knowledge-image {
    .image-wrapper {
      position: relative;
      width: 100%;
      height: 200px;
      overflow: hidden;
      background: #f5f7fa;

      .knowledge-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s;
      }

      .type-badge {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 2;
      }
    }

    .knowledge-info {
      padding: 20px;
    }

    &:hover .knowledge-img {
      transform: scale(1.05);
    }
  }

  // 视频知识样式
  .knowledge-video {
    .video-wrapper {
      position: relative;
      width: 100%;
      height: 200px;
      overflow: hidden;
      background: #000;

      .knowledge-video-player {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .video-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background 0.3s;

        .play-button {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.9);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #67c23a;
          transition: transform 0.3s;
        }

        .type-badge {
          position: absolute;
          top: 10px;
          right: 10px;
        }
      }

      &:hover .video-overlay {
        background: rgba(0, 0, 0, 0.5);

        .play-button {
          transform: scale(1.1);
        }
      }
    }

    .knowledge-info {
      padding: 20px;
    }
  }

  .empty-knowledge {
    grid-column: 1 / -1;
    text-align: center;
    padding: 60px 20px;
    color: #999;

    i {
      font-size: 64px;
      margin-bottom: 20px;
      display: block;
    }

    p {
      font-size: 16px;
    }
  }
}

// 响应式设�?
@media (max-width: 1200px) {
  .knowledge-box {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .knowledge-box {
    grid-template-columns: 1fr;
  }
}
</style>
