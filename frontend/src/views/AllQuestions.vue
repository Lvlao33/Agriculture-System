<template>
  <div class="all-questions-container">
    <div class="left-questions">
      <div class="questions-box">
        <div class="page-header">
          <h2 class="page-title">🔥 热门问答</h2>
        </div>
        <div class="search">
          <el-input 
            v-model="searchValue" 
            maxlength="100" 
            clearable 
            style="width:290px;"
            placeholder="搜索问答..."
            @keyup.enter.native="handleSearch"
          />
          <img src="@/assets/img/search.png" class="search-icon" @click="handleSearch"/>
          <i style="color:#696969;font-size:12px;font-style:normal;">热门搜索：
            <a class="tag-item" @click="handleTopicDetail('苹果果树种植方法')">苹果果树种植方法 </a> 
            <a class="tag-item" @click="handleTopicDetail('新疆哈密瓜甜度控制')">新疆哈密瓜甜度控制</a> 
            <a class="tag-item" @click="handleTopicDetail('玉米苗灌溉')">玉米苗灌溉</a>
          </i>
        </div>
        <div class="questions-list">
          <div class="question-item" v-for="(item, index) in questionsList" :key="index">
            <div class="info">
              <p class="content">
                <i class="question-text" style="" v-if="item.status == 0">[问]</i>
                <i class="question-text" v-else>[答]</i>
                <span class="question-content" @click="handleDetail(item)">{{ item.title }}</span>
              </p>
              <div class="person-contents">
                <span>提问者：{{ item.questioner }}</span>&nbsp;&nbsp;&nbsp;
                <span v-if="item.expertName">专家：{{ item.expertName }}</span>
              </div>
            </div>
          </div>
          <div v-if="questionsList.length === 0" class="empty-state">
            <p>暂无问答数据</p>
          </div>
        </div>
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next, total"
            :page-size="pageSize"
            :total="total"
            :current-page="questionCount"
            @current-change="handlePageChange"
          ></el-pagination>
        </div>
      </div>
    </div>
    <div class="right-questions">
      <div class="btn-content" v-if="!$store.getters.isAdmin && !$store.getters.isExpert">
        <a @click="goToQuestion" style="cursor: pointer;">在线问答</a> |
        <a @click="goToAppointment" style="cursor: pointer;">专家预约</a>
      </div>
      <div class="expert-item-section">
        <div style="font-size:18px;display: flex;justify-content: space-between;">
          <strong>专家列表</strong> 
          <a style="font-size:12px;cursor: pointer;" @click="goToAllExperts">更多专家>></a>
        </div>
        <div class="expert-item" v-for="(item,index) in expertArray" :key="index" :style="index===expertArray.length-1?'':'border-bottom: 1px dashed #f2f2f2;'">
          <img :src="$store.state.imgShowRoad + '/file/' + 'experta.png'" alt="" />
          <div class="info">
            <div class="item-style">
              <div class="content">专家姓名：{{item.realName}}</div>
              <div class="content">职称：{{item.position}}</div>
            </div>
            <div class="item-style">
              <div class="content">从事专业：{{item.profession}}</div>
              <div class="content">电话：{{item.phone}}</div>   
            </div>    
            <div class="item-content">单位：{{item.belong}}</div>
            <div class="item-btn">
              <div class="btn-item" @click="handleQuestion(item)" v-if="!$store.getters.isAdmin&&!$store.getters.isExpert">去提问</div>
              <div class="btn-item" @click="handleAppointment(item)" v-if="!$store.getters.isAdmin&&!$store.getters.isExpert">去预约</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { selectQuestions, selectExpert } from "../api/order";

export default {
  name: "AllQuestions",
  data() {
    return {
      questionsList: [],
      total: 0,
      searchValue: '',
      pageSize: 8,
      questionCount: 1,
      expertCount: 1,
      expertArray: []
    };
  },
  created() {
    this.$store.commit("updateActiveIndex", "5");
    this.getData();
    this.getExpertData();
  },
  methods: {
    handlePageChange(page) {
      this.questionCount = page;
      this.getData();
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    handleSearch() {
      this.questionCount = 1;
      this.getData();
    },
    getData() {
      selectQuestions({
        pageNum: this.questionCount,
        keys: this.searchValue
      }).then((res) => {
        if (res.flag == true) {
          this.questionsList = res.data.list || [];
          this.total = res.data.total || 0;
        } else {
          this.$message.error(res.message || '加载失败');
        }
      }).catch(err => {
        console.log(err);
        this.$message.error('加载数据失败');
      });
    },
    getExpertData() {
      selectExpert({
        pageNum: this.expertCount,
        keys: this.searchValue,
        pageSize: 5
      }).then(res => {
        if (res.flag == true) {
          this.expertArray = res.data.list || [];
        } else {
          this.$message.error(res.message || '加载专家数据失败');
        }
      }).catch(err => {
        console.log(err);
      });
    },
    handleDetail(item) {
      this.$router.push(`/home/guide/${item.id}`).catch((err) => err);
    },
    handleTopicDetail(val) {
      this.searchValue = val;
      this.handleSearch();
    },
    goToQuestion() {
      this.$router.push("/home/question").catch((err) => err);
    },
    goToAppointment() {
      this.$router.push("/home/appointment").catch((err) => err);
    },
    goToAllExperts() {
      this.$router.push("/home/allExpert").catch((err) => err);
    },
    handleQuestion(item) {
      if (localStorage.getItem('token')) {
        this.$router.push(`/home/question?id=${item.userName}`).catch((err) => err);
      } else {
        this.$message.error('请先登录');
        this.$router.push(`/login`).catch((err) => err);
      }
    },
    handleAppointment(item) {
      if (localStorage.getItem('token')) {
        this.$router.push(`/home/appointment?id=${item.userName}`).catch((err) => err);
      } else {
        this.$message.error('请先登录');
        this.$router.push(`/login`).catch((err) => err);
      }
    }
  },
};
</script>

<style lang="less" scoped>
.all-questions-container {
  width: 1100px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  padding: 10px 0;

  .left-questions {
    margin-top: 10px;
    flex: 1;
    margin-right: 10px;

    .questions-box {
      background-color: white;
      padding: 20px;

      .page-header {
        margin-bottom: 20px;
        border-bottom: 2px solid #67C23A;
        padding-bottom: 10px;

        .page-title {
          font-size: 24px;
          color: #333;
          margin: 0;
        }
      }

      .search {
        width: 100%;
        height: 80px;
        background-color: #f5f5f5;
        padding: 10px 20px;
        margin-bottom: 20px;
        border-radius: 4px;

        .tag-item {
          margin-right: 8px;
          cursor: pointer;
          color: #67C23A;
          &:hover {
            text-decoration: underline;
          }
        }

        .search-icon {
          position: relative;
          bottom: 2px;
          left: -2px;
          cursor: pointer;
        }
      }

      .questions-list {
        min-height: 400px;

        .question-item {
          width: 100%;
          min-height: 80px;
          padding: 15px 20px;
          background-color: white;
          border-radius: 4px;
          border-bottom: 1px solid #f2f2f2;
          margin-bottom: 10px;
          transition: all 0.3s;

          &:hover {
            background-color: #f9f9f9;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }

          .info {
            width: 100%;

            .content {
              width: 100%;
              line-height: 24px;
              display: flex;
              flex-direction: row;
              justify-content: flex-start;
              align-items: center;
              margin-bottom: 10px;
              font-size: 16px;

              .question-text {
                color: #67C23A;
                font-style: normal;
                font-weight: bold;
                margin-right: 8px;
              }

              .question-content {
                cursor: pointer;
                color: #333;
                flex: 1;

                &:hover {
                  text-decoration: underline;
                  color: #67C23A;
                }
              }
            }

            .person-contents {
              display: flex;
              flex-direction: row;
              justify-content: flex-end;
              font-size: 12px;
              color: #666;
            }
          }
        }

        .empty-state {
          text-align: center;
          padding: 60px 0;
          color: #999;

          p {
            font-size: 16px;
          }
        }
      }
    }
  }

  .right-questions {
    margin-top: 10px;
    width: 350px;

    .btn-content {
      background-color: white;
      width: 100%;
      font-size: 16px;
      text-align: center;
      font-weight: bold;
      height: 40px;
      line-height: 40px;
      margin-bottom: 10px;
      border-radius: 4px;

      a {
        color: #67C23A;
        &:hover {
          text-decoration: underline;
        }
      }
    }

    .expert-item-section {
      background-color: #fff;
      width: 100%;
      font-size: 12px;
      padding: 10px 20px;
      border-radius: 4px;

      .expert-item {
        min-height: 80px;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-top: 20px;
        align-items: center;

        img {
          width: 80px;
          height: 80px;
          margin-right: 10px;
          border-radius: 6px;
        }

        .info {
          flex: 1;

          .item-style {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
          }

          .content {
            color: #333;
            font-size: 12px;
            font-style: normal;
            width: 120px;
            line-height: 25px;
          }

          .item-content {
            width: 100%;
            line-height: 25px;
            overflow: hidden;
            text-overflow: ellipsis;
            word-break: break-all;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .item-btn {
            display: flex;
            flex-direction: row;
            justify-content: flex-end;
            margin-bottom: 10px;
            margin-right: 10px;
            color: #67C23A;

            .btn-item {
              margin-left: 10px;
              cursor: pointer;

              &:hover {
                text-decoration: underline;
                color: #035D1C;
              }
            }
          }
        }
      }
    }
  }
}

.search /deep/ .el-input--suffix .el-input__inner {
  height: 35px;
  line-height: 35px;
}

.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>

