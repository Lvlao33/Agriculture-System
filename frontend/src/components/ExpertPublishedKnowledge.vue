<template>
  <div class="published-message">
    <div class="toolbar">
      <el-button type="success" @click="publishKnowledgeClick">发布知识</el-button>
      <el-button type="warning" plain @click="batchManageClick">批量管理</el-button>
    </div>
    <div class="publish-content">
      <div class="message" v-for="(item, index) in userKnowledges" :key="index" :style="(index+1)%2===0?'margin-right:0':'margin-right:20px'">
        <img v-if="item.picPath" class="knowleage-icon" :src="$store.state.imgShowRoad + '/file/' + item.picPath" alt="" />
        <img class="knowleage-icon" v-else src="../assets/img/wutu.gif">
        <div class="info">
          <h4 class="title">{{ item.title }}</h4>
          <span class="initiator">发起人：{{ item.ownName }}</span>
          <p class="content">{{ item.content }}</p>
          <div class="btns-style">
            <div @click.once="changeKnowledgeInfo(item.knowledgeId)">
              <change-knowledge :cupdateKnowledgeInfo="updateInfo"></change-knowledge>
            </div>
            <delete-knowledge :knowledge-id="item.knowledgeId" @deleted="loadUserKnowledges" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  selectKnowledgeById,
  selectKnowledgeByUsername,
} from "../api/knowledge";
import ChangeKnowledge from "./ChangeKnowledge.vue";
import DeleteKnowledge from "./DeleteKnowledge.vue";
import { deleteKnowledgeById } from "../api/knowledge";

export default {
  data() {
    return {
      userKnowledges: [],
      updateInfo: {},
    };
  },
  components: { DeleteKnowledge, ChangeKnowledge },
  methods: {
    publishKnowledgeClick() {
      this.$router
        .push("/home/addmessage/publishknowledges")
        .catch((err) => err);
    },
    batchManageClick() {
      this.$message.info("可在此页逐条删除，批量删除暂未开放");
    },
    loadUserKnowledges() {
      selectKnowledgeByUsername({})
        .then((res) => {
          this.userKnowledges = res.data;
        })
        .catch((err) => {
          console.log(err);
        });
    },
    changeKnowledgeInfo(item) {
      this.$store.commit("updateChangedKnowledgeId", item);
      selectKnowledgeById({
        knowledgeId: this.$store.state.changedKnowledgeId,
      })
        .then((res) => {
          this.updateInfo = res.data;
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
  created() {
    this.$store.commit("updateUserActiveIndex", "4-3");
    this.loadUserKnowledges();
  },
};
</script>

<style lang="less" scoped>
.published-message {
  margin: 0 auto;
  width: 900px;
  height: 100%;
  // display: flex;
  // flex-direction: row;
  // justify-content: flex-start;
  // flex-wrap: wrap;
  background: #fff;
  // padding: 0px 20px;
  .publish-content{
    background: #fff;
    display: flex;
    flex-wrap: wrap;
    padding: 10px 20px;
  }
  .message {
    width: 420px;
    height: 180px;
    border: 1px solid #f2f2f2;
    background-color: white;
    margin: 10px 0 10px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    img{
      border-radius: 6px;
    }
    .knowleage-icon{
      width: 200px;
      height: 178px;
      margin-right: 10px;
      border-radius: 6px;
    }
    .info {
      width: 200px;
      padding: 0 10px;
      .initiator {
        color: #666;
      }
      .title{
        height: 30px;
        line-height: 30px;
        /*瓒呭嚭鐨勯儴鍒嗛殣钘�*/
        overflow: hidden;
        /*鏂囧瓧鐢ㄧ渷鐣ュ彿鏇夸唬瓒呭嚭鐨勯儴鍒�*/
        text-overflow: ellipsis;
        /*寮规€т几缂╃洅瀛愭ā鍨嬫樉绀�*/
        display: -webkit-box;
        /*闄愬埗鍦ㄤ竴涓潡鍏冪礌鏄剧ず鏂囨湰鐨勮鏁�*/
        -webkit-line-clamp: 1;
        /*璁剧疆鎴栨绱几缂╃洅瀵硅薄鐨勫瓙鍏冪礌鎺掑垪鏂瑰紡*/
        -webkit-box-orient: vertical;
        word-break: break-all;
      }
      .content {
        height: 75px;
        line-height: 25px;
        /*瓒呭嚭鐨勯儴鍒嗛殣钘�*/
        overflow: hidden;
        /*鏂囧瓧鐢ㄧ渷鐣ュ彿鏇夸唬瓒呭嚭鐨勯儴鍒�*/
        text-overflow: ellipsis;
        /*寮规€т几缂╃洅瀛愭ā鍨嬫樉绀�*/
        display: -webkit-box;
        /*闄愬埗鍦ㄤ竴涓潡鍏冪礌鏄剧ず鏂囨湰鐨勮鏁�*/
        -webkit-line-clamp: 3;
        /*璁剧疆鎴栨绱几缂╃洅瀵硅薄鐨勫瓙鍏冪礌鎺掑垪鏂瑰紡*/
        -webkit-box-orient: vertical;
        word-break: break-all;
      }
    }
    .btns-style{
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
    }
  }
}
</style>