<template>
  <div class="expert-source-container">
    <!-- 搜索框 -->
    <div class="search-section">
      <div class="search-wrapper">
        <el-input 
          v-model="searchValue" 
          maxlength="100" 
          clearable 
          placeholder="搜索专家姓名、职称、专业..."
          class="search-input"
          @keyup.enter.native="handleSearch"
          @clear="handleSearch"
        >
          <el-button 
            slot="append" 
            icon="el-icon-search" 
            @click="handleSearch"
            class="search-btn"
          ></el-button>
        </el-input>
      </div>
    </div>

    <!-- 专家列表 -->
    <div class="expert-list">
      <div 
        class="expert-card" 
        v-for="(item, index) in cgoods" 
        :key="index"
      >
        <div class="expert-avatar-wrapper">
          <img 
            src="/person.png"
            alt="专家头像" 
            class="expert-avatar"
          />
        </div>
        
        <div class="expert-info">
          <div class="expert-header">
            <h3 class="expert-name">{{ item.realName || '未设置' }}</h3>
            <div class="expert-title">{{ item.position || '未设置' }}</div>
          </div>
          
          <div class="expert-details">
            <div class="detail-item">
              <i class="el-icon-medal"></i>
              <span class="detail-label">专业：</span>
              <span class="detail-value">{{ item.profession || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-office-building"></i>
              <span class="detail-label">简介：</span>
              <span class="detail-value">{{ item.belong || '未设置' }}</span>
            </div>
            <div class="detail-item" v-if="item.phone">
              <i class="el-icon-phone"></i>
              <span class="detail-label">电话：</span>
              <span class="detail-value">{{ item.phone }}</span>
            </div>
          </div>
        </div>

        <div class="expert-actions">
          <el-button 
            type="success" 
            plain 
            icon="el-icon-date"
            @click="handleAppoint(item)"
            class="action-btn"
          >
            预约指导
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="cgoods.length === 0" class="empty-state">
        <i class="el-icon-user-solid"></i>
        <p>暂无专家信息</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchValue:''
    };
  },
  filters: {
    formatTimer: function (value) {
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
  },
  props: {
    cgoods: {
      type: Array,
    },
  },
  methods: {
    detailsClick(item) {
      this.$store.commit("updateOrderId", item);
      this.$router.push("/home/details").catch((err) => err);
    },
    handleQuestion(item){
      this.$router.push(`/home/question?id=${item.userName}`).catch((err) => err);
    },
    handleAppoint(item){
      this.$router.push(`/home/appointment?id=${item.userName}`).catch((err) => err);
    },
    handleSearch(){
      this.$emit('handleSearch',this.searchValue)
    }
  },
};
</script>

<style lang="less" scoped>
.expert-source-container {
  width: 100%;
  margin: 0 auto;

  // 搜索区域
  .search-section {
    margin-bottom: 25px;

    .search-wrapper {
      max-width: 600px;
      margin: 0 auto;

      .search-input {
        /deep/ .el-input-group {
          display: flex;
          border-radius: 24px;
          overflow: hidden;
          border: 2px solid #dcdfe6;
          transition: all 0.3s;
          background-color: white;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

          &:focus-within {
            border-color: #67C23A;
            box-shadow: 0 2px 12px rgba(103, 194, 58, 0.2);
          }
        }

        /deep/ .el-input__inner {
          height: 48px;
          line-height: 48px;
          border: none;
          border-radius: 0;
          padding-left: 20px;
          font-size: 15px;
          flex: 1;
          background-color: white;

          &:focus {
            border: none;
            box-shadow: none;
            background-color: white;
          }
        }

        /deep/ .el-input-group__append {
          border: none;
          border-radius: 0;
          padding: 0;
          background-color: transparent;
          border-left: none;

          .el-button {
            height: 48px;
            background-color: #67C23A;
            border-color: #67C23A;
            color: white;
            border-radius: 0;
            border: none;
            padding: 0 30px;
            transition: all 0.3s;
            font-size: 16px;
            margin: 0;

            &:hover {
              background-color: #5daf34;
              border-color: #5daf34;
            }

            &:focus {
              background-color: #5daf34;
              border-color: #5daf34;
            }
          }
        }
      }
    }
  }

  // 专家列表
  .expert-list {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .expert-card {
      background-color: white;
      border-radius: 12px;
      padding: 25px;
      display: flex;
      align-items: flex-start;
      gap: 25px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s;
      border: 1px solid #f0f0f0;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(103, 194, 58, 0.15);
        border-color: #67C23A;
      }

      // 头像区域
      .expert-avatar-wrapper {
        position: relative;
        flex-shrink: 0;

        .expert-avatar {
          width: 120px;
          height: 120px;
          border-radius: 12px;
          object-fit: cover;
          border: 3px solid #f0f0f0;
          transition: all 0.3s;
        }

        .expert-badge {
          position: absolute;
          bottom: -5px;
          right: -5px;
          background: linear-gradient(135deg, #67C23A 0%, #5daf34 100%);
          color: white;
          padding: 4px 10px;
          border-radius: 12px;
          font-size: 12px;
          display: flex;
          align-items: center;
          gap: 4px;
          box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);

          i {
            font-size: 12px;
          }
        }

        &:hover .expert-avatar {
          border-color: #67C23A;
        }
      }

      // 专家信息
      .expert-info {
        flex: 1;
        min-width: 0;

        .expert-header {
          margin-bottom: 15px;
          padding-bottom: 15px;
          border-bottom: 2px solid #f0f0f0;

          .expert-name {
            font-size: 22px;
            font-weight: bold;
            color: #333;
            margin: 0 0 8px 0;
            line-height: 1.3;
          }

          .expert-title {
            font-size: 15px;
            color: #67C23A;
            font-weight: 500;
          }
        }

        .expert-details {
          display: flex;
          flex-direction: column;
          gap: 12px;

          .detail-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #666;
            line-height: 1.6;

            i {
              color: #67C23A;
              font-size: 16px;
              width: 20px;
              text-align: center;
            }

            .detail-label {
              color: #999;
              min-width: 50px;
            }

            .detail-value {
              color: #333;
              flex: 1;
            }
          }
        }
      }

      // 操作按钮
      .expert-actions {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        gap: 12px;
        flex-shrink: 0;
        min-width: 120px;

        .action-btn {
          width: 100%;
          height: 42px;
          border-radius: 8px;
          font-size: 14px;
          transition: all 0.3s;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
          }
        }
      }
    }

    // 空状态
    .empty-state {
      text-align: center;
      padding: 80px 20px;
      background-color: white;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

      i {
        font-size: 64px;
        color: #ddd;
        margin-bottom: 20px;
        display: block;
      }

      p {
        font-size: 16px;
        color: #999;
        margin: 0;
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .expert-source-container {
    padding: 0 20px;

    .expert-list .expert-card {
      flex-wrap: wrap;

      .expert-actions {
        width: 100%;
        flex-direction: row;
        margin-top: 15px;
      }
    }
  }
}

@media (max-width: 768px) {
  .expert-source-container {
    .search-section {
      .search-wrapper {
        .search-input {
          /deep/ .el-input__inner {
            height: 42px;
            font-size: 14px;
          }
        }
      }
    }

    .expert-list .expert-card {
      padding: 20px;
      flex-direction: column;
      align-items: center;
      text-align: center;

      .expert-avatar-wrapper {
        .expert-avatar {
          width: 100px;
          height: 100px;
        }
      }

      .expert-info {
        width: 100%;

        .expert-details {
          .detail-item {
            justify-content: center;
          }
        }
      }

      .expert-actions {
        width: 100%;
        flex-direction: row;
        justify-content: center;
      }
    }
  }
}
</style>