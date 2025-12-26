<!--专家问答-->
<template>
  <div class="expert-question-container">
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-chat-line-round"></i> 我的问答</h2>
      <p class="page-desc">显示您提交的问题与专家的回答</p>
    </div>
    <div class="questions-wrapper">
      <div v-for="(item,index) in displayedQuestions" :key="index" class="question-item">
        <div class="question-card">
          <div class="question-main">
            <h3 class="question-title" @click="goToDetail(item)">{{ item.title }}</h3>
            <div class="question-meta">
              <span class="meta-item"><i class="el-icon-chat-line-round"></i> {{ item.answerCount || item.answersCount || 0 }} 个回答</span>
              <span class="meta-item"><i class="el-icon-user"></i> 提问者：{{ item.questioner || item.username || '匿名用户' }}</span>
              <span class="meta-item"><i class="el-icon-time"></i> 最后更新：{{ formatDate(item.updateTime || item.createTime || item.create_time) }}</span>
            </div>
          </div>
          <div class="question-side">
            <el-tag class="status-tag" :type="item.status === 0 || item.status === 'PENDING' ? 'info' : 'success'">{{ item.status === 0 || item.status === 'PENDING' ? '待解答' : '已解答' }}</el-tag>
          </div>
        </div>
        <div class="question-actions">
          <el-button type="text" @click="handleDetail(item)">详情</el-button>
          <el-button type="text" v-if="role==='questioner'" @click="handleEdit(item)">修改</el-button>
          <el-button type="text" v-if="role==='expert'" @click="handleEdit(item)">回答</el-button>
          <el-button type="text" style="color:#f56c6c" @click="delQuestion(item)">删除</el-button>
        </div>
      </div>
      <div class="pagination-wrapper" v-if="total > pageSize" style="padding:12px 0; display:flex; justify-content:center;">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :page-size="pageSize"
          :total="total"
          :current-page="currentPage"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    <el-dialog title="详情" v-model:visible="showDetail" width="600px" :before-close="detailClose">
      <div>
        <div class="detail-item">
          <div class="item-title">问题标题：</div>
          <div class="item-content">{{detailObj.title}}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">问题内容：</div>
          <div class="item-content">{{detailObj.question}}</div>
        </div>
        <div class="detail-item" v-if="role==='questioner'">
          <div class="item-title">专家姓名：</div>
          <div class="item-content">{{detailObj.expertName}}</div>
        </div>
        <!-- <div class="detail-item">
          <div class="item-title">提问者：</div>
          <div class="item-content">{{detailObj.questioner}}</div>
        </div> -->
        <div class="detail-item">
          <div class="item-title">问题状态：</div>
          <el-tag type="danger" size="mini">{{detailObj.status === 0 ? '未回答' :'已回答'}}</el-tag>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button @click="showDetail = false">取 消</el-button> -->
        <el-button type="primary" @click="showDetail = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog :title="title" v-model:visible="dialogVisible" width="650px" :before-close="closeRevise">
      <div>
        <div class="detail-item">
          <div class="item-title">问题标题：</div>
          <div class="item-content">{{detailObj.title}}</div>
        </div>
        <!-- 专家不能改问题内容 -->
        <div class="detail-item" v-if="role==='expert'||detailObj.status === 1">
          <div class="item-title">内容：</div>
          <div class="item-content">{{detailObj.question}}</div>
        </div>
        <!-- 提问者可以改问题内容 -->
        <el-form ref="form" :model="detailObj" label-width="60px" v-if="role==='questioner'&&detailObj.status === 0">
          <el-form-item label="内容：">
            <el-input type="textarea" v-model="detailObj.question"></el-input>
          </el-form-item>
        </el-form>

        <div class="detail-item" v-if="role==='questioner'">
          <div class="item-title">回答：</div>
          <div class="item-content">{{detailObj.answer}}</div>
        </div>
        <el-form ref="form" :model="detailObj" label-width="60px" v-if="role==='expert'">
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
import { selectQuestionByUser,reviseQuestionByUserId,delQuestionByUserId } from '../api/question.js'
import { getQuestionsList } from '@/api/qa'

export default {
  data(){
    return{
      questionArray:[],
      showDetail: false,
      dialogVisible: false,
      role:"",
      title:"",
      pageSize: 3,
      currentPage: 1,
      total: 0,
      detailObj:{
        title:'',
        question:'',
        answer:'',
        address:'',
        area:'',
        expertName:'',
        plantCondition:'',
        plantDetail:'',
        plantName:'',
        questioner:'',
        soilCondition:'',
        status:1
      }
    }
  },
  methods:{
    async getData(){
      this.role = this.$store.getters.isExpert?'expert':'questioner'
      console.log('role',this.role)
      // 优先使用主接口 /api/qa/questions
      try {
        const res = await getQuestionsList({
          pageNum: 1,
          pageSize: 1000,
          mine: this.role === 'questioner'
        })
        console.log('获取问题（主接口）', res)
        let list = []
        if (res) {
          if (res.flag && res.data && Array.isArray(res.data)) list = res.data
          else if (res.data && Array.isArray(res.data)) list = res.data
          else if (Array.isArray(res)) list = res
          else if (res.list && Array.isArray(res.list)) list = res.list
        }
        // 如果主接口没有返回数据，则回退到 legacy 接口
        if (!Array.isArray(list) || list.length === 0) {
          try {
            const fallback = await selectQuestionByUser({ role: this.role })
            console.log('获取问题（fallback）', fallback)
            if (fallback && fallback.data && Array.isArray(fallback.data)) list = fallback.data
            else if (Array.isArray(fallback)) list = fallback
          } catch (e) {
            console.warn('回退接口也失败', e)
          }
        }
        this.questionArray = list || []
        this.total = (this.questionArray || []).length
      } catch (err) {
        console.error('加载问题失败：', err)
        // 尝试 legacy 回退
        try {
          const fallback = await selectQuestionByUser({ role: this.role })
          this.questionArray = (fallback && fallback.data) ? fallback.data : []
          this.total = (this.questionArray || []).length
        } catch (e) {
          console.error('回退接口失败：', e)
          this.questionArray = []
        }
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
    delQuestion(item){
      this.$confirm('确认删除该行信息？', '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delQuestionByUserId({id:item.id}).then(res=>{
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
      // keep for backwards compatibility with edit modal flows
      this.showDetail = true
      this.detailObj = Object.assign({},{...item})
    },
    goToDetail(item){
      if (!item || !item.id) return
      this.$router.push(`/home/questionDetail/${item.id}`).catch(()=>{})
    },
    handlePageChange(page) {
      this.currentPage = page
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    detailClose(){
      this.showDetail = false
    },
    handleEdit(item){
      if(this.role === 'questioner'){
        this.title = "修改";
      }else{
        this.title = "回答";
      }
      this.dialogVisible = true
      this.detailObj = Object.assign({},{...item})
    },
    closeRevise(){
      this.dialogVisible = false
    },
    submitRevise(){
      if(this.role === 'expert'){
        this.detailObj.status = 1
      }else{
        this.detailObj.status = 0
      }
      
      reviseQuestionByUserId(this.detailObj).then(res => {
        this.$message.success('修改成功！')
        this.dialogVisible = false
        this.getData()
      }).catch(err=>{
        console.log(err)
      })
    }
  },
  mounted(){
    this.$store.commit("updateUserActiveIndex", "4-1");
    this.getData()
  }
,
  computed: {
    displayedQuestions() {
      const start = (this.currentPage - 1) * this.pageSize
      return (this.questionArray || []).slice(start, start + this.pageSize)
    }
  },
}
</script>

<style lang="less" scoped>
.expert-question-container{
  width: 100%;
  min-height: 100%;
  background: #f5f7f9;
  padding: 12px 0;

  .questions-wrapper {
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
  .question-item{
    margin: 12px 0;
    .question-card {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      border: 1px solid #e9eef1;
      border-radius: 8px;
      padding: 12px 14px; /* 更紧凑的内边距 */
      background: #fff;
      box-shadow: 0 1px 4px rgba(0,0,0,0.02);

      .question-main {
        flex: 1;
        padding-right: 12px;

        .question-title {
          font-size: 18px;
          font-weight: 700;
          color: #222;
          margin: 0 0 10px 0;
          cursor: pointer;
        }

        .question-meta {
          display: flex;
          gap: 18px;
          align-items: center;
          color: #6b6f7b;
          font-size: 13px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 6px;

            i {
              color: #67C23A;
              font-size: 16px;
            }
          }
        }
      }

      .question-side {
        display: flex;
        align-items: flex-start;
      }
    }

    .question-actions {
      display: flex;
      gap: 8px;
      padding: 6px 10px;
      align-items: center;
    }

    /* 按钮边框样式，使每个操作看起来是个小方块 */
    .question-actions ::v-deep .el-button {
      border: 1px solid #e6e6e6;
      background: #fff;
      color: #333;
      border-radius: 6px;
      padding: 6px 12px;
      min-width: 56px;
      box-shadow: none;
    }

    .question-actions ::v-deep .el-button:hover {
      background: #fafafa;
      border-color: #dcdfe6;
    }
  }
  .detail-item{
    display: flex;
    line-height: 30px;
    .item-content{
      line-height: 30px;
      height: auto;
      width: 480px;
    }
    .item-title{
      width: 80px;
      height: 30px;
      font-weight: bold;
      color: #333;
    }
  }
}
</style>