<template>
  <div class="appointment-container">
    <div class="appointment-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <h2 class="page-title">
          <i class="el-icon-date"></i>
          预约指导
        </h2>
        <p class="page-desc">填写预约信息，专家将为您提供专业的农业指导服务</p>
      </div>

      <!-- 预约表单 -->
      <div class="appointment-form-wrapper">
        <el-form 
          :model="appointmentForm" 
          :rules="rules" 
          ref="appointmentForm"
          label-width="120px"
          class="appointment-form"
        >
          <el-form-item label="用户名" prop="userName">
            <el-input
              v-model="appointmentForm.userName"
              placeholder="请输入用户名"
              :disabled="true"
            >
              <template slot="prepend">
                <i class="el-icon-user"></i>
              </template>
            </el-input>
            <div class="form-tip">当前登录用户</div>
          </el-form-item>

          <el-form-item label="联系方式" prop="contact">
            <el-input
              v-model="appointmentForm.contact"
              placeholder="请输入手机号码或微信号"
              maxlength="50"
            >
              <template slot="prepend">
                <i class="el-icon-phone"></i>
              </template>
            </el-input>
            <div class="form-tip">用于专家与您联系，请填写手机号或微信号</div>
          </el-form-item>

          <el-form-item label="预约时间" prop="appointmentTime">
            <el-date-picker
              v-model="appointmentForm.appointmentTime"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="yyyy-MM-dd HH:mm"
              value-format="yyyy-MM-dd HH:mm:ss"
              :picker-options="pickerOptions"
              style="width: 100%"
            ></el-date-picker>
            <div class="form-tip">请选择您期望的时间段，专家后续将与您协商确认最终指导时间</div>
          </el-form-item>

          <el-form-item label="问题描述" prop="description">
            <el-input
              type="textarea"
              v-model="appointmentForm.description"
              :rows="6"
              placeholder="请详细描述您需要咨询的问题，包括具体情况、遇到的问题等，越详细越有助于专家给出准确的指导..."
              maxlength="500"
              show-word-limit
            ></el-input>
            <div class="form-tip">请详细描述您的问题，便于专家提前准备</div>
          </el-form-item>

          <el-form-item label="选择专家" prop="expertId">
            <el-select
              v-model="appointmentForm.expertId"
              placeholder="请选择专家"
              filterable
              style="width: 100%"
              @change="handleExpertChange"
            >
              <el-option
                v-for="expert in expertList"
                :key="expert.id"
                :label="`${expert.name || expert.realName} - ${expert.title || '专家'}`"
                :value="expert.id"
              >
                <div class="expert-option">
                  <span class="expert-name">{{ expert.name || expert.realName }}</span>
                  <span class="expert-title">{{ expert.title || '专家' }}</span>
                </div>
              </el-option>
            </el-select>
            <div class="form-tip">选择您希望预约的专家，也可以留空等待系统分配</div>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              size="medium"
              @click="handleSubmit"
              :loading="submitting"
              class="submit-btn"
            >
              <i class="el-icon-check"></i>
              提交预约
            </el-button>
            <el-button 
              @click="handleReset"
              size="medium"
            >
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
          预约须知
        </h3>
        <ul class="tips-list">
          <li>请确保联系方式正确，以便专家能够及时与您联系</li>
          <li>预约时间请选择您方便的时间段，建议提前1-2天预约</li>
          <li>问题描述越详细，专家越能给出准确的指导建议</li>
          <li>提交预约后，专家会在24小时内确认预约</li>
          <li>您可以在"我的预约"中查看预约状态</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { addAppointment } from '@/api/appointment'
import { selectExperts } from '@/api/expert'

export default {
  name: 'Appointment',
  data() {
    return {
      appointmentForm: {
        userName: '',
        contact: '',
        appointmentTime: [],
        description: '',
        expertId: null
      },
      rules: {
        contact: [
          { required: true, message: '请输入联系方式', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$|^[a-zA-Z0-9_-]{5,}$/, message: '请输入正确的手机号或微信号', trigger: 'blur' }
        ],
        appointmentTime: [
          { required: true, message: '请选择预约时间段', trigger: 'change' },
          { 
            validator: (rule, value, callback) => {
              if (!value || value.length !== 2) {
                callback(new Error('请选择完整的预约时间段'))
              } else {
                const start = new Date(value[0])
                const end = new Date(value[1])
                const now = new Date()
                if (start < now) {
                  callback(new Error('预约开始时间不能早于当前时间'))
                } else if (end <= start) {
                  callback(new Error('结束时间必须晚于开始时间'))
                } else {
                  // 检查时间段是否至少30分钟
                  const diff = (end - start) / (1000 * 60) // 分钟数
                  if (diff < 30) {
                    callback(new Error('预约时间段至少需要30分钟'))
                  } else {
                    callback()
                  }
                }
              }
            },
            trigger: 'change'
          }
        ],
        description: [
          { required: true, message: '请输入问题描述', trigger: 'blur' },
          { min: 10, max: 500, message: '问题描述长度在 10 到 500 个字符', trigger: 'blur' }
        ],
        expertId: [
          { required: false, message: '请选择专家', trigger: 'change' }
        ]
      },
      expertList: [],
      submitting: false,
      pickerOptions: {
        disabledDate(time) {
          // 只能选择今天及以后的日期
          return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
        },
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            const start = new Date()
            start.setHours(9, 0, 0, 0)
            const end = new Date()
            end.setHours(17, 0, 0, 0)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '明天',
          onClick(picker) {
            const start = new Date()
            start.setTime(start.getTime() + 3600 * 1000 * 24)
            start.setHours(9, 0, 0, 0)
            const end = new Date()
            end.setTime(end.getTime() + 3600 * 1000 * 24)
            end.setHours(17, 0, 0, 0)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '后天',
          onClick(picker) {
            const start = new Date()
            start.setTime(start.getTime() + 3600 * 1000 * 48)
            start.setHours(9, 0, 0, 0)
            const end = new Date()
            end.setTime(end.getTime() + 3600 * 1000 * 48)
            end.setHours(17, 0, 0, 0)
            picker.$emit('pick', [start, end])
          }
        }]
      }
    }
  },
  created() {
    this.$store.commit('updateActiveIndex', '5')
    // 从路由参数获取专家ID（如果是从专家卡片跳转过来的）
    if (this.$route.query.id) {
      this.appointmentForm.expertId = parseInt(this.$route.query.id)
    }
    // 设置当前登录用户名
    this.appointmentForm.userName = this.$store.state.loginUserNickname || this.$store.state.user || '未登录'
    // 加载专家列表
    this.loadExpertList()
  },
  methods: {
    // 加载专家列表
    async loadExpertList() {
      try {
        const res = await selectExperts({
          pageNum: 1,
          pageSize: 100,
          keys: ''
        })
        if (res && res.flag === true && res.data && res.data.list) {
          this.expertList = res.data.list || []
        } else {
          this.$message.warning('获取专家列表失败')
        }
      } catch (error) {
        console.error('加载专家列表失败:', error)
        this.$message.error('加载专家列表失败')
      }
    },
    // 专家选择变化
    handleExpertChange(value) {
      console.log('选择的专家ID:', value)
    },
    // 提交预约
    handleSubmit() {
      // 检查登录
      if (!this.$store.state.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }

      this.$refs.appointmentForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            // 格式化提交数据
            const submitData = {
              userName: this.appointmentForm.userName,
              userContact: this.appointmentForm.contact,
              startTime: this.appointmentForm.appointmentTime[0],
              endTime: this.appointmentForm.appointmentTime[1],
              description: this.appointmentForm.description,
              expertId: this.appointmentForm.expertId,
              expertName: this.getExpertName(this.appointmentForm.expertId)
            }

            const res = await addAppointment(submitData)
            
            if (res && res.success) {
              this.$message.success('预约提交成功！专家会在24小时内确认')
              // 延迟跳转，让用户看到成功提示
              setTimeout(() => {
                this.$router.push('/home/guide').catch((err) => err)
              }, 1500)
            } else {
              this.$message.error(res?.message || '预约提交失败，请重试')
            }
          } catch (error) {
            console.error('提交预约失败:', error)
            this.$message.error('预约提交失败，请稍后重试')
          } finally {
            this.submitting = false
          }
        } else {
          this.$message.warning('请完善预约信息')
        }
      })
    },
    // 获取专家名称
    getExpertName(expertId) {
      if (!expertId) return ''
      const expert = this.expertList.find(e => e.id === expertId)
      return expert ? (expert.name || expert.realName) : ''
    },
    // 重置表单
    handleReset() {
      this.$refs.appointmentForm.resetFields()
      // 保留用户名
      this.appointmentForm.userName = this.$store.state.loginUserNickname || this.$store.state.user || '未登录'
      // 保留路由参数中的专家ID
      if (this.$route.query.id) {
        this.appointmentForm.expertId = parseInt(this.$route.query.id)
      }
    }
  }
}
</script>

<style lang="less" scoped>
.appointment-container {
  width: 100%;
  min-height: calc(100vh - 200px);
  background-color: #f5f5f5;
  padding: 20px 0;

  .appointment-wrapper {
    width: 1100px;
    margin: 0 auto;

    .page-header {
      background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      border-radius: 8px;
      padding: 30px;
      margin-bottom: 30px;
      color: #fff;
      text-align: center;

      .page-title {
        font-size: 28px;
        font-weight: bold;
        margin: 0 0 10px 0;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;

        i {
          font-size: 32px;
        }
      }

      .page-desc {
        font-size: 16px;
        margin: 0;
        opacity: 0.9;
      }
    }

    .appointment-form-wrapper {
      background: #fff;
      border-radius: 8px;
      padding: 40px;
      margin-bottom: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .appointment-form {
        .form-tip {
          font-size: 12px;
          color: #999;
          margin-top: 5px;
          line-height: 1.5;
        }

        .expert-option {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .expert-name {
            font-weight: 500;
          }

          .expert-title {
            color: #999;
            font-size: 12px;
          }
        }

        .submit-btn {
          padding: 12px 40px;
          font-size: 16px;
        }
      }
    }

    .tips-section {
      background: #fff;
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
          color: #67c23a;
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
            color: #67c23a;
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
  .appointment-container .appointment-wrapper {
    width: 95%;
    padding: 0 10px;
  }
}

@media (max-width: 768px) {
  .appointment-container .appointment-wrapper {
    .appointment-form-wrapper {
      padding: 20px;
    }

    .tips-section {
      padding: 20px;
    }
  }
}
</style>
