<!--专家问答-->
<template>
  <div class="expert-appoint-container">
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-date"></i> 我的预约</h2>
      <p class="page-desc">在此查看您提交或收到的预约，专家确认后状态会更新。</p>
    </div>
    <div class="appoints-wrapper">
      <div v-for="(item,index) in appointArray" :key="index" class="appoint-item">
        <div class="appoint-card">
          <div class="appoint-main">
            <h3 class="appoint-title" @click="handleDetail(item)">{{ truncateText(item.plantDetail || item.description || item.plantName || item.title || '预约详情', 8) }}</h3>
            <div class="appoint-meta">
              <span class="meta-item"><i class="el-icon-user"></i> 咨询者：{{ item.questioner || item.userName || '匿名用户' }}</span>
              <span class="meta-item" v-if="item.phone"><i class="el-icon-phone"></i> 联系：{{ item.phone }}</span>
              <span class="meta-item"><i class="el-icon-time"></i> 时间：{{ formatDate(item.appointmentTime || item.startTime || item.createTime) }}</span>
            </div>
            <div class="appoint-desc">{{ item.plantDetail || item.description || '' }}</div>
          </div>
          <div class="appoint-side">
            <el-tag class="status-tag" :type="item.status === 0 ? 'info' : 'success'">{{ item.status === 0 ? '待确认' : '已确认' }}</el-tag>
          </div>
        </div>
        <div class="appoint-actions">
          <el-button type="text" @click="handleDetail(item)">详情</el-button>
          <el-button type="text" @click="handleEdit(item)">修改</el-button>
          <el-button type="text" style="color:#f56c6c" @click="delAppoint(item)">删除</el-button>
        </div>
      </div>
    <el-dialog title="详情" v-model:visible="showDetail" width="600px" :before-close="detailClose">
      <div class="detail-content">
        <div class="detail-item">
          <div class="item-title">种植作物：</div>
          <div class="item-content">{{detailObj.plantName}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物详细信息：</div>
          <div class="item-content">{{detailObj.plantDetail}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">地址：</div>
          <div class="item-content">{{detailObj.address}}</div>
        </div>
         <div class="detail-item">
          <div class="item-title">土壤条件：</div>
          <div class="item-content">{{detailObj.soilCondition}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">面积：</div>
          <div class="item-content">{{detailObj.area}}</div>
        </div>
         <div class="detail-item">
          <div class="item-title">作物条件：</div>
          <div class="item-content">{{detailObj.plantCondition}}</div>
        </div>
         <div class="detail-item" v-if="role==='expert'">
          <div class="item-title">联系方式：</div>
          <div class="item-content">{{detailObj.phone}}</div>
        </div>
         <div class="detail-item" v-if="role==='expert'">
          <div class="item-title">提问者：</div>
          <div class="item-content">{{detailObj.questioner}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">问题状态：</div>
          <el-tag type="danger" size="mini">{{detailObj.status === 0 ? '未回答' :'已回答'}}</el-tag>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDetail = false">取 消</el-button>
        <el-button type="primary" @click="showDetail = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="修改" v-model:visible="dialogVisible" width="650px" :before-close="closeRevise">
      <div class="detail-content">
        <div class="detail-item">
          <div class="item-title">种植作物：</div>
          <div class="item-content">{{detailObj.plantName}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物详细信息：</div>
          <div class="item-content">{{detailObj.plantDetail}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">地址：</div>
          <div class="item-content">{{detailObj.address}}</div>
        </div>
         <div class="detail-item">
          <div class="item-title">土壤条件：</div>
          <div class="item-content">{{detailObj.soilCondition}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">面积：</div>
          <div class="item-content">{{detailObj.area}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物条件：</div>
          <div class="item-content">{{detailObj.plantCondition}}</div>
        </div>
         <div class="detail-item">
          <div class="item-title">联系方式：</div>
          <div class="item-content">{{detailObj.phone}}</div>
        </div>
         <div class="detail-item">
          <div class="item-title">提问者：</div>
          <div class="item-content">{{detailObj.questioner}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">问题状态：</div>
          <el-tag type="danger" size="mini">{{detailObj.status === 0 ? '未回答' :'已回答'}}</el-tag>
        </div>
        <el-form ref="form" :model="detailObj" label-width="60px">
          <el-form-item label="回答：">
            <el-input type="textarea" v-model="detailObj.answer"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeRevise">取 消</el-button>
        <el-button type="primary" @click="submitRevise">确 定</el-button>
      </span>
    </el-dialog>
    </div>
  </div>
</template>

<script>
import { selectAppointByUser,reviseAppointByUserId,delAppointByUserId } from '../api/question.js'
import { getAppointmentListByUserId } from '@/api/appointment'

export default {
  data(){
    return{
      appointArray:[],
      role:"",
      showDetail: false,
      dialogVisible: false,
      detailObj:{
        title:'',
        expertName:'',
        questioner:'',
        status:''
      }
    }
  },
  methods:{
    async getData(){
      this.role =  this.$store.getters.isExpert?'expert':'questioner'
      const userId = this.$store.state.loginUserId
      try {
        if (userId) {
          const res = await getAppointmentListByUserId(userId)
          let list = []
          if (res) {
            if (res.flag === true && res.data) {
              list = Array.isArray(res.data) ? res.data : (res.data.list || [])
            } else if (Array.isArray(res.data)) {
              list = res.data
            } else if (Array.isArray(res)) {
              list = res
            } else if (res.data && Array.isArray(res.data.list)) {
              list = res.data.list
            }
          }
          this.appointArray = list || []
        } else {
          this.appointArray = []
        }
      } catch (err) {
        console.error('加载预约列表失败：', err)
        this.appointArray = []
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      try {
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (e) {
        return dateStr;
      }
    },
    delAppoint(item){
      this.$confirm('确认删除该行信息？', '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delAppointByUserId({id:item.id}).then(res=>{
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getData()
        }).catch(err=>{
          console.log(err)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });          
      });
    },
    handleDetail(item){
      this.showDetail = true
      this.detailObj = Object.assign({},{...item})
    },
    detailClose(){
      this.showDetail = false
    },
    handleEdit(item){
      this.dialogVisible = true
      this.detailObj = Object.assign({},{...item})
    },
    closeRevise(){
      this.dialogVisible = false
    },
    submitRevise(){
      this.detailObj.status = 1
      reviseAppointByUserId(this.detailObj).then(res => {
        this.$message.success('修改成功！')
        this.dialogVisible = false
        this.getData()
      }).catch(err=>{
        console.log(err)
      })
    }
    ,
    truncateText(text, maxLen = 8) {
      if (!text && text !== 0) return ''
      const s = String(text)
      if (s.length <= maxLen) return s
      return s.slice(0, maxLen) + '...'
    }
  },
  mounted(){
    this.$store.commit("updateUserActiveIndex", "4-2");
    this.getData()
  }
}
</script>

<style lang="less" scoped>
.expert-appoint-container{
  width: 100%;
  min-height: 100%;
  background: #f5f7f9;
  padding: 12px 0;

  .appoints-wrapper {
    width: 900px;
    max-width: calc(100% - 160px);
    margin: 0 auto;
    background: #fff;
    border-radius: 8px;
    padding: 16px 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  }

  .page-header {
    width: 900px;
    max-width: calc(100% - 160px);
    margin: 0 auto 12px auto;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;
    gap: 6px;
    h2.page-title {
      font-size: 22px;
      margin: 0;
      color: #333;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .page-desc {
      margin: 0;
      color: #666;
      font-size: 13px;
    }
  }
  .appoint-item {
    margin: 12px 0;

    .appoint-card {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      border: 1px solid #e9eef1;
      border-radius: 8px;
      padding: 12px 14px;
      background: #fff;
      box-shadow: 0 1px 4px rgba(0,0,0,0.02);

      .appoint-main {
        flex: 1;
        padding-right: 12px;

        .appoint-title {
          font-size: 18px;
          font-weight: 700;
          color: #222;
          margin: 0 0 8px 0;
          cursor: pointer;
        }

        .appoint-meta {
          display: flex;
          gap: 16px;
          align-items: center;
          color: #6b6f7b;
          font-size: 13px;
        }

        .appoint-desc {
          margin-top: 8px;
          color: #666;
          line-height: 1.6;
        }
      }

      .appoint-side {
        display: flex;
        align-items: flex-start;
      }
    }

    .appoint-actions {
      display: flex;
      gap: 8px;
      padding: 6px 10px;
      align-items: center;
    }

    .appoint-actions ::v-deep .el-button {
      border: 1px solid #e6e6e6;
      background: #fff;
      color: #333;
      border-radius: 6px;
      padding: 6px 12px;
      min-width: 56px;
      box-shadow: none;
    }

    .appoint-actions ::v-deep .el-button:hover {
      background: #fafafa;
      border-color: #dcdfe6;
    }
  }
  .detail-content{
    max-height: 500px;
    height: auto;
    overflow-y: auto;
  }
  .detail-item{
    display: flex;
    line-height: 30px;
    .item-content{
      line-height: 30px;
      height: auto;
      width: 480px;
      display: flex;
    }
    .item-title{
      width: 110px;
      height: 30px;
      font-weight: bold;
      color: #333;
    }
  }
}
</style>
</style>