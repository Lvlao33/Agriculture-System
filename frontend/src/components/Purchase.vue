<template>
  <div class="needs-box">
    <div class="search3">
      <el-input v-model="searchValue" maxlength="100" clearable style="width:290px;"/>
      <img src="../assets/img/search.png" class="search-icon" @click="handleSearch"/>&nbsp;&nbsp;
        <i style="color:#696969;font-size:13px;font-style:normal;">&nbsp;鐑棬鎼滅储锛�
          <a class="tag-item" @click="handleTopicDetail('鑻规灉')">鑻规灉</a> 
          <a class="tag-item" @click="handleTopicDetail('鏂扮枂鍝堝瘑鐡�')">鏂扮枂鍝堝瘑鐡�</a>  
          <a class="tag-item" @click="handleTopicDetail('妯辨')">妯辨</a>  
          <a class="tag-item" @click="handleTopicDetail('瑗跨孩鏌�')">瑗跨孩鏌�</a>  
          <a class="tag-item" @click="handleTopicDetail('姘寸ɑ')">姘寸ɑ</a>  
          <a class="tag-item" @click="handleTopicDetail('鐜夌背')">鐜夌背</a>  
          <a class="tag-item" @click="handleTopicDetail('璧ｅ崡鑴愭')">璧ｅ崡鑴愭</a> 
        </i>
    </div>
    <div class="purchase-content">
      <div v-for="(item, index) in cneeds"
      :key="index" class="purchase-item"
      @click="detailsClick(item.orderId)">
        <div class="title">
          <span class="tips">{{item.type === 'needs'?'[姹傝喘]':''}}</span>
          <span class="title-content">{{item.content}}</span>
        </div>
        <div class="content">{{item.title}}</div>
        <div class="update-time">{{ item.updateTime | formatTimer}}</div>
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
        formatTimer: function(value) {
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
          return y + "-" + MM + "-" + d + " ";
        }
      },
  props: {
    cneeds: {
      type: Array,
    },
  },
  methods: {
    detailsClick(item) {
      this.$store.state.orderId = item;
      this.$router.push(`/home/purchaseDetails?orderId=${item}`).catch((err) => err);
    },
    handleSearch(){
      this.$emit('handleSearch',this.searchValue)
    },
    handleTopicDetail(val){
      this.searchValue = val
      this.handleSearch()
    }
  },
};
</script>

<style lang='less' scoped>
.needs-box{
  width: 100%;
  .search3 {
    height: 60px;
    background-color: white;
    padding: 10px 20px;
    margin: 10px auto;
    width: 1100px;
    .tag-item{
      margin-right: 10px;
      cursor: pointer;
    }
    .search-icon{
      position:relative;
      bottom:2px;
      left: -2px;
      cursor: pointer;
    }
  }
  .purchase-content{
    background: #fff;
    padding: 20px;
    width: 1100px;
    margin: 0 auto;
    .purchase-item{
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      padding: 10px 0;
      border-bottom: 1px solid #eee;
      .title{
        width: 620px;
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
        .tips{
          color: #999;
        }
        .title-content{
          cursor: pointer;
          &:hover{
            color: #035D1C;
            text-decoration: underline;
          }
        }
      }
      .content{
        width: 300px;
        margin-left: 20px;
        /*瓒呭嚭鐨勯儴鍒嗛殣钘�*/
        overflow: hidden;
        /*鏂囧瓧鐢ㄧ渷鐣ュ彿鏇夸唬瓒呭嚭鐨勯儴鍒�*/
        text-overflow: ellipsis;
        /*寮规€т几缂╃洅瀛愭ā鍨嬫樉绀�*/
        display: -webkit-box;
        /*闄愬埗鍦ㄤ竴涓潡鍏冪礌鏄剧ず鏂囨湰鐨勮鏁�*/
        -webkit-line-clamp: 1;
        /*璁剧疆鎴栨绱几缂╃洅瀵硅薄鐨勫瓙鍏� 绱犳帓鍒楁柟寮�*/
        -webkit-box-orient: vertical;
      }
      .update-time{
        width: 100px;
        text-align: right;
      }
    }
  }
}



.search3 /deep/ .el-input--suffix .el-input__inner{
  height: 35px;
  line-height: 35px;
}
</style>