<template>
<<<<<<< HEAD
  <div class="my-question-page">
    <div class="page-header">
      <h2>我的问答</h2>
      <p class="subtitle">管理您的所有问答记录</p>
    </div>

    <div class="toolbar">
      <el-button type="success" @click="goToAskQuestion">
        <i class="el-icon-plus"></i>
        提问
      </el-button>
      <el-button 
        :type="isSelectMode ? 'danger' : 'warning'" 
        plain 
        @click="toggleSelectMode"
      >
        <i class="el-icon-check"></i>
        {{ isSelectMode ? '取消选择' : '选择' }}
      </el-button>
      <el-button 
        v-if="isSelectMode && selectedQuestionIds.length > 0"
        type="danger" 
        @click="handleBatchDelete"
        style="margin-left: 10px;"
      >
        <i class="el-icon-delete"></i>
        删除 ({{ selectedQuestionIds.length }})
      </el-button>
    </div>

    <div class="question-list" v-loading="loading">
      <div 
        class="question-card" 
        v-for="(item, index) in questionArray" 
        :key="item.id || index"
        :class="{ 'selected': isSelectMode && selectedQuestionIds.includes(item.id) }"
        @click="handleQuestionClick(item.id)"
      >
        <div v-if="isSelectMode" class="checkbox-wrapper">
          <el-checkbox 
            :value="selectedQuestionIds.includes(item.id)"
            @change="toggleQuestionSelection(item.id)"
            @click.stop
          ></el-checkbox>
        </div>
        <div class="question-icon">
          <i class="el-icon-question" :class="{ 'answered': item.status === 1 }"></i>
        </div>
        <div class="question-content">
          <h3 class="question-title">{{ item.title || '无标题' }}</h3>
          <p class="question-text">{{ item.question || '暂无内容' }}</p>
          <div class="question-meta">
            <span class="meta-item" v-if="role === 'expert'">
              <i class="el-icon-user"></i>
              提问者：{{ item.questioner || '未知' }}
            </span>
            <span class="meta-item" v-if="role === 'expert'">
              <i class="el-icon-phone"></i>
              {{ item.phone || '未提供' }}
            </span>
            <span class="meta-item" v-if="role === 'questioner'">
              <i class="el-icon-user-solid"></i>
              专家：{{ item.expertName || '未指定' }}
            </span>
            <span class="meta-item">
              <i class="el-icon-time"></i>
              {{ formatDate(item.createTime) }}
            </span>
          </div>
          <div class="question-status">
            <el-tag 
              :type="item.status === 0 ? 'warning' : 'success'" 
              size="small"
            >
              <i :class="item.status === 0 ? 'el-icon-warning' : 'el-icon-success'"></i>
              {{ item.status === 0 ? '未回答' : '已回答' }}
            </el-tag>
            <div class="question-actions" @click.stop>
              <el-button 
                type="text" 
                size="small" 
                @click="handleDetail(item)"
                icon="el-icon-view"
              >
                详情
              </el-button>
              <el-button 
                v-if="role === 'questioner' && item.status === 0"
                type="text" 
                size="small" 
                @click="handleEdit(item)"
                icon="el-icon-edit"
              >
                修改
              </el-button>
              <el-button 
                v-if="role === 'expert' && item.status === 0"
                type="text" 
                size="small" 
                @click="handleEdit(item)"
                icon="el-icon-chat-line-round"
              >
                回答
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click.stop="delQuestion(item)"
                icon="el-icon-delete"
                style="color: #F56C6C;"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="questionArray.length === 0 && !loading" class="empty-state">
        <i class="el-icon-question"></i>
        <p>暂无问答记录</p>
        <el-button type="primary" @click="goToAskQuestion">去提问</el-button>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog 
      title="问答详情" 
      :visible.sync="showDetail" 
      width="700px"
      :before-close="detailClose"
    >
      <div class="detail-content">
=======
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
>>>>>>> 76ff3b7e203814c914ed9256b66340a20762ad39
        <div class="detail-item">
          <div class="item-title">问题标题：</div>
          <div class="item-content">{{ detailObj.title || '无标题' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">问题内容：</div>
          <div class="item-content">{{ detailObj.question || '暂无内容' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'questioner'">
          <div class="item-title">专家姓名：</div>
          <div class="item-content">{{ detailObj.expertName || '未指定' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert'">
          <div class="item-title">提问者：</div>
          <div class="item-content">{{ detailObj.questioner || '未知' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert'">
          <div class="item-title">联系方式：</div>
          <div class="item-content">{{ detailObj.phone || '未提供' }}</div>
        </div>
        <div class="detail-item" v-if="detailObj.answer">
          <div class="item-title">专家回答：</div>
          <div class="item-content answer-content">{{ detailObj.answer }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">问题状态：</div>
          <el-tag 
            :type="detailObj.status === 0 ? 'warning' : 'success'" 
            size="small"
          >
            {{ detailObj.status === 0 ? '未回答' : '已回答' }}
          </el-tag>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDetail = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 编辑/回答弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogVisible" 
      width="700px"
      :before-close="closeRevise"
    >
      <div class="detail-content">
        <div class="detail-item">
          <div class="item-title">问题标题：</div>
          <div class="item-content">{{ detailObj.title || '无标题' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert' || detailObj.status === 1">
          <div class="item-title">问题内容：</div>
          <div class="item-content">{{ detailObj.question || '暂无内容' }}</div>
        </div>
        <el-form 
          ref="form" 
          :model="detailObj" 
          label-width="80px" 
          v-if="role === 'questioner' && detailObj.status === 0"
        >
          <el-form-item label="问题内容：">
            <el-input 
              type="textarea" 
              :rows="4"
              v-model="detailObj.question"
              placeholder="请输入问题内容"
            ></el-input>
          </el-form-item>
        </el-form>
        <div class="detail-item" v-if="role === 'questioner' && detailObj.answer">
          <div class="item-title">专家回答：</div>
          <div class="item-content answer-content">{{ detailObj.answer }}</div>
        </div>
        <el-form 
          ref="form" 
          :model="detailObj" 
          label-width="80px" 
          v-if="role === 'expert'"
        >
          <el-form-item label="回答内容：">
            <el-input 
              type="textarea" 
              :rows="6"
              v-model="detailObj.answer"
              placeholder="请输入您的回答"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeRevise">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitRevise">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 确认删除弹窗 -->
    <el-dialog
      title="确认删除"
      :visible.sync="deleteDialogVisible"
      width="400px"
    >
      <div class="delete-dialog-content">
        <i class="el-icon-warning" style="color: #E6A23C; font-size: 24px; margin-right: 10px;"></i>
        <p>确定要删除选中的 <strong style="color: #F56C6C;">{{ selectedQuestionIds.length }}</strong> 条问答吗？</p>
        <p style="color: #909399; font-size: 12px; margin-top: 10px;">删除后无法恢复。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取 消</el-button>
        <el-button type="danger" :loading="deleting" @click="confirmDelete">确认删除</el-button>
      </span>
    </el-dialog>
    </div>
  </div>
</template>

<script>
<<<<<<< HEAD
import { selectQuestionByUser, reviseQuestionByUserId, delQuestionByUserId } from '../api/question.js'
=======
import { selectQuestionByUser,reviseQuestionByUserId,delQuestionByUserId } from '../api/question.js'
import { getQuestionsList } from '@/api/qa'
>>>>>>> 76ff3b7e203814c914ed9256b66340a20762ad39

export default {
  name: "MyQuestion",
  data() {
    return {
      questionArray: [],
      loading: false,
      isSelectMode: false,
      selectedQuestionIds: [],
      showDetail: false,
      dialogVisible: false,
<<<<<<< HEAD
      deleteDialogVisible: false,
      deleting: false,
      submitting: false,
      role: "",
      dialogTitle: "",
      detailObj: {
        id: '',
        title: '',
        question: '',
        expertName: '',
        answer: '',
        questioner: '',
        phone: '',
        status: 0,
        createTime: ''
      }
    }
  },
  methods: {
    // 加载问答列表
    getData() {
      this.loading = true;
      this.role = this.$store.getters.isExpert ? 'expert' : 'questioner';
      
      selectQuestionByUser({ role: this.role })
        .then(res => {
          if (res && res.data) {
            this.questionArray = Array.isArray(res.data) ? res.data : [];
          } else {
            this.questionArray = [];
          }
        })
        .catch(err => {
          console.error('加载问答列表失败:', err);
          this.$message.error('加载问答列表失败，请重试');
          this.questionArray = [];
        })
        .finally(() => {
          this.loading = false;
        });
=======
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
>>>>>>> 76ff3b7e203814c914ed9256b66340a20762ad39
    },
    // 切换选择模式
    toggleSelectMode() {
      this.isSelectMode = !this.isSelectMode;
      if (!this.isSelectMode) {
        this.selectedQuestionIds = [];
      }
    },
    // 点击问答卡片
    handleQuestionClick(questionId) {
      if (this.isSelectMode) {
        this.toggleQuestionSelection(questionId);
      }
    },
    // 切换问答选择状态
    toggleQuestionSelection(questionId) {
      const index = this.selectedQuestionIds.indexOf(questionId);
      if (index > -1) {
        this.selectedQuestionIds.splice(index, 1);
      } else {
        this.selectedQuestionIds.push(questionId);
      }
    },
    // 批量删除
    handleBatchDelete() {
      if (this.selectedQuestionIds.length === 0) {
        this.$message.warning("请先选择要删除的问答");
        return;
      }
      this.deleteDialogVisible = true;
    },
    // 确认删除
    confirmDelete() {
      this.deleting = true;
      const deletePromises = this.selectedQuestionIds.map(id => 
        delQuestionByUserId({ id: id })
      );

      Promise.all(deletePromises)
        .then((results) => {
          const successCount = results.filter(r => r && (r.flag !== false)).length;
          if (successCount === this.selectedQuestionIds.length) {
            this.$message.success(`成功删除 ${successCount} 条问答`);
          } else {
            this.$message.warning(`部分删除失败，成功删除 ${successCount} 条`);
          }
          this.selectedQuestionIds = [];
          this.isSelectMode = false;
          this.deleteDialogVisible = false;
          this.getData();
        })
        .catch((err) => {
          console.error("批量删除失败:", err);
          this.$message.error("删除失败，请重试");
        })
        .finally(() => {
          this.deleting = false;
        });
    },
    // 删除单个问答
    delQuestion(item) {
      this.$confirm('确认删除该问答信息？', '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delQuestionByUserId({ id: item.id })
          .then(res => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getData();
          })
          .catch(err => {
            console.error('删除失败:', err);
            this.$message.error('删除失败，请重试');
          });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
<<<<<<< HEAD
    // 查看详情
    handleDetail(item) {
      this.showDetail = true;
      this.detailObj = Object.assign({}, { ...item });
    },
    detailClose() {
      this.showDetail = false;
=======
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
>>>>>>> 76ff3b7e203814c914ed9256b66340a20762ad39
    },
    // 编辑/回答
    handleEdit(item) {
      if (this.role === 'questioner') {
        this.dialogTitle = "修改问题";
      } else {
        this.dialogTitle = "回答问题";
      }
      this.dialogVisible = true;
      this.detailObj = Object.assign({}, { ...item });
    },
    closeRevise() {
      this.dialogVisible = false;
    },
    // 提交修改/回答
    submitRevise() {
      if (this.role === 'expert') {
        if (!this.detailObj.answer || this.detailObj.answer.trim() === '') {
          this.$message.warning('请输入回答内容');
          return;
        }
        this.detailObj.status = 1;
      } else {
        if (!this.detailObj.question || this.detailObj.question.trim() === '') {
          this.$message.warning('请输入问题内容');
          return;
        }
        this.detailObj.status = 0;
      }

      this.submitting = true;
      reviseQuestionByUserId(this.detailObj)
        .then(res => {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          this.getData();
        })
        .catch(err => {
          console.error('操作失败:', err);
          this.$message.error('操作失败，请重试');
        })
        .finally(() => {
          this.submitting = false;
        });
    },
    // 去提问
    goToAskQuestion() {
      this.$router.push("/home/askQuestion").catch((err) => err);
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return "未知时间";
      try {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      } catch (e) {
        return dateStr;
      }
    },
  },
  mounted() {
    this.$store.commit("updateUserActiveIndex", "4-1");
    this.getData();
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
<<<<<<< HEAD
.my-question-page {
  width: 1100px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  min-height: 600px;

  .page-header {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;

    h2 {
      margin: 0 0 10px 0;
      font-size: 24px;
      color: #303133;
    }

    .subtitle {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .toolbar {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .question-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .question-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    cursor: pointer;
    transition: all 0.3s;
    position: relative;
    background: #fff;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      border-color: #67C23A;
    }

    &.selected {
      border: 2px solid #67C23A;
      background-color: #f0f9ff;
      box-shadow: 0 2px 12px rgba(103, 194, 58, 0.2);
    }

    .checkbox-wrapper {
      position: absolute;
      top: 15px;
      left: 15px;
      z-index: 10;
      background: rgba(255, 255, 255, 0.95);
      padding: 5px;
      border-radius: 4px;
    }

    .question-icon {
      width: 60px;
      height: 60px;
      margin-right: 20px;
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f0f9ff;
      border-radius: 50%;
      
      i {
        font-size: 32px;
        color: #67C23A;
        
        &.answered {
          color: #409EFF;
        }
      }
    }

    .question-content {
      flex: 1;
      display: flex;
      flex-direction: column;

      .question-title {
        margin: 0 0 10px 0;
        font-size: 18px;
        color: #303133;
        font-weight: 600;
      }

      .question-text {
        margin: 0 0 15px 0;
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .question-meta {
        display: flex;
        gap: 20px;
        margin-bottom: 15px;
        font-size: 12px;
        color: #909399;
        flex-wrap: wrap;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 5px;

          i {
            font-size: 14px;
          }
        }
      }

      .question-status {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .question-actions {
          display: flex;
          gap: 10px;
=======
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
>>>>>>> 76ff3b7e203814c914ed9256b66340a20762ad39
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

  .empty-state {
    width: 100%;
    text-align: center;
    padding: 80px 0;
    color: #909399;

    i {
      font-size: 64px;
      margin-bottom: 20px;
      display: block;
      color: #c0c4cc;
    }

    p {
      margin: 0 0 20px 0;
      font-size: 16px;
    }
  }

  .detail-content {
    max-height: 500px;
    overflow-y: auto;
    padding-right: 10px;

    .detail-item {
      display: flex;
      margin-bottom: 20px;
      align-items: flex-start;

      .item-title {
        width: 100px;
        font-weight: bold;
        color: #303133;
        flex-shrink: 0;
      }

      .item-content {
        flex: 1;
        color: #606266;
        line-height: 1.6;
        word-break: break-word;

        &.answer-content {
          background: #f5f7fa;
          padding: 10px;
          border-radius: 4px;
          border-left: 3px solid #67C23A;
        }
      }
    }
  }

  .delete-dialog-content {
    display: flex;
    align-items: flex-start;
    padding: 10px 0;

    p {
      margin: 0;
      line-height: 1.6;
    }
  }
}

@media (max-width: 1200px) {
  .my-question-page {
    width: 100%;
    padding: 15px;

    .question-card {
      flex-direction: column;

      .question-icon {
        margin-right: 0;
        margin-bottom: 15px;
      }
    }
  }
}
</style>
