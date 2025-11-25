<template>
  <div class="ask-question-container">
    <div class="ask-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <h2 class="page-title">我要提问</h2>
        <div class="back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
          <span>返回</span>
        </div>
      </div>

      <!-- 提问表单 -->
      <div class="question-form">
        <el-form 
          :model="questionForm" 
          :rules="rules" 
          ref="questionForm"
          label-width="100px"
        >
          <el-form-item label="问题标题" prop="title">
            <el-input
              v-model="questionForm.title"
              placeholder="请输入问题标题，简洁明了地描述您的问题"
              maxlength="100"
              show-word-limit
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="问题内容" prop="content">
            <el-input
              type="textarea"
              v-model="questionForm.content"
              :rows="10"
              placeholder="请详细描述您遇到的问题，包括具体情况、发生时间、已采取的措施等，越详细越有助于专家给出准确的解答"
              maxlength="1000"
              show-word-limit
            ></el-input>
          </el-form-item>

          <el-form-item label="选择专家" prop="expertId">
            <el-select
              v-model="questionForm.expertId"
              placeholder="可选择特定专家，或留空等待专家回答"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="expert in expertList"
                :key="expert.id"
                :label="`${expert.realName} - ${expert.position}`"
                :value="expert.id"
              >
                <span style="float: left">{{ expert.realName }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ expert.position }}</span>
              </el-option>
            </el-select>
            <div class="form-tip">不选择专家时，所有专家都可以回答您的问题</div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitQuestion" :loading="submitting">
              <i class="el-icon-check"></i>
              提交问题
            </el-button>
            <el-button @click="resetForm">
              <i class="el-icon-refresh-left"></i>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 提示信息 -->
      <div class="tips-section">
        <h3 class="tips-title">
          <i class="el-icon-info"></i>
          提问须知
        </h3>
        <ul class="tips-list">
          <li>请详细描述您的问题，包括具体情况、发生时间等</li>
          <li>问题标题要简洁明了，便于专家快速理解</li>
          <li>可以选择特定专家提问，也可以等待专家主动回答</li>
          <li>提交后请耐心等待，专家会在看到问题后尽快回复</li>
          <li>您可以在"在线提问"页面查看问题的回答情况</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
// import { submitQuestion, getExpertList } from '@/api/qa'

export default {
  name: 'AskQuestion',
  data() {
    return {
      questionForm: {
        title: '',
        content: '',
        expertId: null
      },
      rules: {
        title: [
          { required: true, message: '请输入问题标题', trigger: 'blur' },
          { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入问题内容', trigger: 'blur' },
          { min: 10, max: 1000, message: '内容长度在 10 到 1000 个字符', trigger: 'blur' }
        ]
      },
      expertList: [],
      submitting: false
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '4')
    this.loadExpertList()
  },
  methods: {
    // 加载专家列表
    loadExpertList() {
      // TODO: 调用后端接口
      // getExpertList().then(res => {
      //   if (res.flag) {
      //     this.expertList = res.data || []
      //   }
      // })

      // 临时使用模拟数据
      this.expertList = [
        { id: 1, realName: '李教授', position: '高级农艺师' },
        { id: 2, realName: '王专家', position: '高级畜牧师' },
        { id: 3, realName: '刘教授', position: '高级兽医师' },
        { id: 4, realName: '陈专家', position: '高级农艺师' },
        { id: 5, realName: '周专家', position: '高级园艺师' }
      ]
    },
    // 提交问题
    submitQuestion() {
      this.$refs.questionForm.validate((valid) => {
        if (valid) {
          this.submitting = true
          
          // TODO: 调用后端接口
          // submitQuestion(this.questionForm).then(res => {
          //   if (res.flag) {
          //     this.$message.success('问题提交成功！')
          //     this.$router.push('/home/onlineQuestions').catch(() => {})
          //   } else {
          //     this.$message.error(res.message || '提交失败')
          //   }
          // }).catch(err => {
          //   this.$message.error('提交失败，请重试')
          // }).finally(() => {
          //   this.submitting = false
          // })

          // 临时模拟提交
          setTimeout(() => {
            this.$message.success('问题提交成功！')
            this.$router.push('/home/onlineQuestions').catch(() => {})
            this.submitting = false
          }, 1000)
        } else {
          this.$message.warning('请完善问题信息')
        }
      })
    },
    // 重置表单
    resetForm() {
      this.$refs.questionForm.resetFields()
    },
    // 返回上一页
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="less" scoped>
.ask-question-container {
  width: 100%;
  min-height: calc(100vh - 200px);
  background-color: #f5f5f5;
  padding: 20px 0;

  .ask-wrapper {
    width: 1100px;
    margin: 0 auto;

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 2px solid #67C23A;

      .page-title {
        font-size: 28px;
        font-weight: bold;
        color: #333;
        margin: 0;
      }

      .back-btn {
        display: inline-flex;
        align-items: center;
        gap: 5px;
        color: #67C23A;
        cursor: pointer;
        font-size: 14px;
        padding: 8px 12px;
        border-radius: 4px;
        transition: all 0.3s;

        &:hover {
          background-color: #f0f9ff;
          color: #5daf34;
        }

        i {
          font-size: 16px;
        }
      }
    }

    .question-form {
      background-color: #fff;
      border-radius: 8px;
      padding: 30px;
      margin-bottom: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .form-tip {
        font-size: 12px;
        color: #999;
        margin-top: 5px;
      }
    }

    .tips-section {
      background-color: #fff;
      border-radius: 8px;
      padding: 25px 30px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .tips-title {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        margin: 0 0 15px 0;
        display: flex;
        align-items: center;
        gap: 8px;

        i {
          color: #67C23A;
          font-size: 20px;
        }
      }

      .tips-list {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          font-size: 14px;
          line-height: 2;
          color: #666;
          padding-left: 20px;
          position: relative;

          &:before {
            content: "•";
            color: #67C23A;
            font-weight: bold;
            position: absolute;
            left: 0;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .ask-question-container .ask-wrapper {
    width: 95%;
    padding: 0 10px;
  }
}

@media (max-width: 768px) {
  .ask-question-container .ask-wrapper {
    .question-form,
    .tips-section {
      padding: 20px;
    }
  }
}
</style>

