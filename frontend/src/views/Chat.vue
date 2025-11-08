<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-header">
        <el-button icon="el-icon-arrow-left" @click="goBack" circle></el-button>
        <h3>与负责人联系</h3>
        <div class="manager-info">
          <span>负责人：{{ managerName }}</span>
        </div>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="['message-item', message.type]"
        >
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您要咨询的问题..."
          @keyup.ctrl.enter.native="sendMessage"
        ></el-input>
        <div class="input-actions">
          <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">
            <i class="el-icon-s-promotion"></i> 发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Chat',
  data() {
    return {
      managerId: '',
      managerName: '贷款负责人',
      productId: '',
      inputMessage: '',
      messages: [
        {
          type: 'received',
          content: '您好，我是贷款负责人，有什么可以帮助您的吗？',
          time: this.formatTime(new Date())
        }
      ]
    }
  },
  mounted() {
    this.managerId = this.$route.params.managerId || 'default_manager';
    this.productId = this.$route.query.productId || '';
    
    // 如果有产品ID，可以加载相关信息
    if (this.productId) {
      const productStr = localStorage.getItem('loanProduct');
      if (productStr) {
        const product = JSON.parse(productStr);
        this.managerName = product.managerName || product.bank + '贷款负责人';
      }
    }

    // 滚动到底部
    this.$nextTick(() => {
      this.scrollToBottom();
    });
  },
  methods: {
    sendMessage() {
      if (!this.inputMessage.trim()) {
        return;
      }

      // 添加用户消息
      this.messages.push({
        type: 'sent',
        content: this.inputMessage,
        time: this.formatTime(new Date())
      });

      const userMessage = this.inputMessage;
      this.inputMessage = '';

      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom();
      });

      // 模拟回复（实际应该调用API）
      setTimeout(() => {
        this.messages.push({
          type: 'received',
          content: this.getAutoReply(userMessage),
          time: this.formatTime(new Date())
        });
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }, 1000);
    },
    getAutoReply(message) {
      // 简单的自动回复逻辑（实际应该调用后端API）
      if (message.includes('材料') || message.includes('资料')) {
        return '申请材料包括：身份证扫描件、银行流水、收入证明等。您可以在产品详情页查看完整的材料清单和模板下载。';
      } else if (message.includes('利率') || message.includes('利息')) {
        return '利率信息可以在产品详情页查看。实际利率会根据您的信用状况和贷款金额进行调整，最终以银行审批为准。';
      } else if (message.includes('时间') || message.includes('多久')) {
        return '审核时间一般为3-5个工作日，放款时间根据产品不同，最快3个工作日可以放款。';
      } else if (message.includes('额度') || message.includes('金额')) {
        return '贷款额度根据您的信用状况、收入情况和产品要求来确定。您可以在申请时填写期望的贷款金额。';
      } else {
        return '感谢您的咨询。如果您有关于贷款申请的具体问题，请告诉我，我会尽力为您解答。您也可以在产品详情页查看详细的申请材料说明。';
      }
    },
    formatTime(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  }
}
</script>

<style lang="less" scoped>
.chat-page {
  width: 100%;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20px 0;
}

.chat-container {
  width: 800px;
  margin: 0 auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  max-height: 700px;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  gap: 15px;

  h3 {
    margin: 0;
    flex: 1;
    font-size: 18px;
    color: #333;
  }

  .manager-info {
    font-size: 14px;
    color: #666;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f9f9f9;

  .message-item {
    margin-bottom: 20px;
    display: flex;

    &.sent {
      justify-content: flex-end;

      .message-content {
        background-color: #4CAF50;
        color: white;
        border-radius: 12px 12px 0 12px;
      }
    }

    &.received {
      justify-content: flex-start;

      .message-content {
        background-color: white;
        color: #333;
        border-radius: 12px 12px 12px 0;
      }
    }

    .message-content {
      max-width: 70%;
      padding: 12px 16px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

      .message-text {
        font-size: 14px;
        line-height: 1.5;
        word-wrap: break-word;
      }

      .message-time {
        font-size: 12px;
        margin-top: 5px;
        opacity: 0.7;
      }
    }
  }
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e0e0e0;
  background-color: white;

  .input-actions {
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
  }
}

@media (max-width: 900px) {
  .chat-container {
    width: 95%;
    height: calc(100vh - 40px);
  }
}
</style>

