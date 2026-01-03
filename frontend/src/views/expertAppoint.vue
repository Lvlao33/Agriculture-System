<template>
  <div class="my-appointment-page">
    <div class="page-header">
      <h2>我的预约</h2>
      <p class="subtitle">管理您的所有预约记录</p>
    </div>

    <div class="toolbar">
      <el-button type="success" @click="goToAppointment">
        <i class="el-icon-plus"></i>
        新建预约
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
        v-if="isSelectMode && selectedAppointmentIds.length > 0"
        type="danger" 
        @click="handleBatchDelete"
        style="margin-left: 10px;"
      >
        <i class="el-icon-delete"></i>
        删除 ({{ selectedAppointmentIds.length }})
      </el-button>
    </div>

    <div class="appointment-list" v-loading="loading">
      <div 
        class="appointment-card" 
        v-for="(item, index) in appointArray" 
        :key="item.id || index"
        :class="{ 'selected': isSelectMode && selectedAppointmentIds.includes(item.id) }"
        @click="handleAppointmentClick(item.id)"
      >
        <div v-if="isSelectMode" class="checkbox-wrapper">
          <el-checkbox 
            :value="selectedAppointmentIds.includes(item.id)"
            @change="toggleAppointmentSelection(item.id)"
            @click.stop
          ></el-checkbox>
        </div>
        <div class="appointment-icon">
          <i class="el-icon-alarm-clock" :class="{ 'answered': item.status === 1 }"></i>
        </div>
        <div class="appointment-content">
          <h3 class="appointment-title">{{ item.plantName || '未命名作物' }}</h3>
          <p class="appointment-text">{{ item.plantDetail || '暂无详细信息' }}</p>
          <div class="appointment-info">
            <div class="info-row">
              <span class="info-item" v-if="role === 'expert'">
                <i class="el-icon-user"></i>
                咨询者：{{ item.questioner || '未知' }}
              </span>
              <span class="info-item" v-if="role === 'expert'">
                <i class="el-icon-phone"></i>
                {{ item.phone || '未提供' }}
              </span>
              <span class="info-item" v-if="role === 'questioner'">
                <i class="el-icon-user-solid"></i>
                专家：{{ item.expertName || '未指定' }}
              </span>
            </div>
            <div class="info-row">
              <span class="info-item">
                <i class="el-icon-location"></i>
                地址：{{ item.address || '未提供' }}
              </span>
              <span class="info-item">
                <i class="el-icon-data-line"></i>
                面积：{{ item.area || '未知' }}亩
              </span>
            </div>
            <div class="info-row">
              <span class="info-item">
                <i class="el-icon-sunny"></i>
                作物条件：{{ item.plantCondition || '未提供' }}
              </span>
              <span class="info-item">
                <i class="el-icon-s-grid"></i>
                土壤条件：{{ truncateText(item.soilCondition, 20) || '未提供' }}
              </span>
            </div>
          </div>
          <div class="appointment-status">
            <el-tag 
              :type="item.status === 0 ? 'warning' : 'success'" 
              size="small"
            >
              <i :class="item.status === 0 ? 'el-icon-warning' : 'el-icon-success'"></i>
              {{ item.status === 0 ? '未回复' : '已回复' }}
            </el-tag>
            <div class="appointment-actions" @click.stop>
              <el-button 
                type="text" 
                size="small" 
                @click="handleDetail(item)"
                icon="el-icon-view"
              >
                详情
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click="handleEdit(item)"
                icon="el-icon-edit"
              >
                回复
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click.stop="delAppoint(item)"
                icon="el-icon-delete"
                style="color: #F56C6C;"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="appointArray.length === 0 && !loading" class="empty-state">
        <i class="el-icon-alarm-clock"></i>
        <p>暂无预约记录</p>
        <el-button type="primary" @click="goToAppointment">创建预约</el-button>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog 
      title="预约详情" 
      :visible.sync="showDetail" 
      width="700px"
      :before-close="detailClose"
    >
      <div class="detail-content">
        <div class="detail-item">
          <div class="item-title">种植作物：</div>
          <div class="item-content">{{ detailObj.plantName || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物详细信息：</div>
          <div class="item-content">{{ detailObj.plantDetail || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">地址：</div>
          <div class="item-content">{{ detailObj.address || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">面积：</div>
          <div class="item-content">{{ detailObj.area || '未知' }}亩</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物条件：</div>
          <div class="item-content">{{ detailObj.plantCondition || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">土壤条件：</div>
          <div class="item-content">{{ detailObj.soilCondition || '未提供' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert'">
          <div class="item-title">咨询者：</div>
          <div class="item-content">{{ detailObj.questioner || '未知' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert'">
          <div class="item-title">联系方式：</div>
          <div class="item-content">{{ detailObj.phone || '未提供' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'questioner'">
          <div class="item-title">专家姓名：</div>
          <div class="item-content">{{ detailObj.expertName || '未指定' }}</div>
        </div>
        <div class="detail-item" v-if="detailObj.answer">
          <div class="item-title">专家回复：</div>
          <div class="item-content answer-content">{{ detailObj.answer }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">预约状态：</div>
          <el-tag 
            :type="detailObj.status === 0 ? 'warning' : 'success'" 
            size="small"
          >
            {{ detailObj.status === 0 ? '未回复' : '已回复' }}
          </el-tag>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDetail = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 编辑/回复弹窗 -->
    <el-dialog 
      title="回复预约" 
      :visible.sync="dialogVisible" 
      width="700px"
      :before-close="closeRevise"
    >
      <div class="detail-content">
        <div class="detail-item">
          <div class="item-title">种植作物：</div>
          <div class="item-content">{{ detailObj.plantName || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物详细信息：</div>
          <div class="item-content">{{ detailObj.plantDetail || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">地址：</div>
          <div class="item-content">{{ detailObj.address || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">面积：</div>
          <div class="item-content">{{ detailObj.area || '未知' }}亩</div>
        </div>
        <div class="detail-item">
          <div class="item-title">作物条件：</div>
          <div class="item-content">{{ detailObj.plantCondition || '未提供' }}</div>
        </div>
        <div class="detail-item">
          <div class="item-title">土壤条件：</div>
          <div class="item-content">{{ detailObj.soilCondition || '未提供' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert'">
          <div class="item-title">咨询者：</div>
          <div class="item-content">{{ detailObj.questioner || '未知' }}</div>
        </div>
        <div class="detail-item" v-if="role === 'expert'">
          <div class="item-title">联系方式：</div>
          <div class="item-content">{{ detailObj.phone || '未提供' }}</div>
        </div>
        <el-form 
          ref="form" 
          :model="detailObj" 
          label-width="80px"
        >
          <el-form-item label="回复内容：">
            <el-input 
              type="textarea" 
              :rows="6"
              v-model="detailObj.answer"
              placeholder="请输入您的回复内容"
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
        <p>确定要删除选中的 <strong style="color: #F56C6C;">{{ selectedAppointmentIds.length }}</strong> 条预约吗？</p>
        <p style="color: #909399; font-size: 12px; margin-top: 10px;">删除后无法恢复。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取 消</el-button>
        <el-button type="danger" :loading="deleting" @click="confirmDelete">确认删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { selectAppointByUser, reviseAppointByUserId, delAppointByUserId } from '../api/question.js'
import { getAppointmentListByUserId } from '@/api/appointment'

export default {
  name: "MyAppointment",
  data() {
    return {
      appointArray: [],
      loading: false,
      isSelectMode: false,
      selectedAppointmentIds: [],
      showDetail: false,
      dialogVisible: false,
      deleteDialogVisible: false,
      deleting: false,
      submitting: false,
      role: "",
      detailObj: {
        id: '',
        plantName: '',
        plantDetail: '',
        address: '',
        area: '',
        plantCondition: '',
        soilCondition: '',
        expertName: '',
        questioner: '',
        phone: '',
        answer: '',
        status: 0
      }
    }
  },
  methods: {
    // 加载预约列表 - 优先使用新API，失败则回退到旧API
    async getData() {
      this.loading = true;
      this.role = this.$store.getters.isExpert ? 'expert' : 'questioner';
      
      try {
        // 优先尝试新API
        const userId = this.$store.state.loginUserId;
        if (userId) {
          try {
            const res = await getAppointmentListByUserId(userId);
            let list = [];
            if (res) {
              if (res.flag === true && res.data) {
                list = Array.isArray(res.data) ? res.data : (res.data.list || []);
              } else if (Array.isArray(res.data)) {
                list = res.data;
              } else if (Array.isArray(res)) {
                list = res;
              } else if (res.data && Array.isArray(res.data.list)) {
                list = res.data.list;
              }
            }
            this.appointArray = list || [];
            this.loading = false;
            return;
          } catch (newApiError) {
            console.warn('新API调用失败，回退到旧API:', newApiError);
          }
        }
        
        // 回退到旧API
        const res = await selectAppointByUser({ type: this.role });
        if (res && res.data) {
          this.appointArray = Array.isArray(res.data) ? res.data : [];
        } else {
          this.appointArray = [];
        }
      } catch (err) {
        console.error('加载预约列表失败:', err);
        this.$message.error('加载预约列表失败，请重试');
        this.appointArray = [];
      } finally {
        this.loading = false;
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
    // 切换选择模式
    toggleSelectMode() {
      this.isSelectMode = !this.isSelectMode;
      if (!this.isSelectMode) {
        this.selectedAppointmentIds = [];
      }
    },
    // 点击预约卡片
    handleAppointmentClick(appointmentId) {
      if (this.isSelectMode) {
        this.toggleAppointmentSelection(appointmentId);
      }
    },
    // 切换预约选择状态
    toggleAppointmentSelection(appointmentId) {
      const index = this.selectedAppointmentIds.indexOf(appointmentId);
      if (index > -1) {
        this.selectedAppointmentIds.splice(index, 1);
      } else {
        this.selectedAppointmentIds.push(appointmentId);
      }
    },
    // 批量删除
    handleBatchDelete() {
      if (this.selectedAppointmentIds.length === 0) {
        this.$message.warning("请先选择要删除的预约");
        return;
      }
      this.deleteDialogVisible = true;
    },
    // 确认删除
    confirmDelete() {
      this.deleting = true;
      const deletePromises = this.selectedAppointmentIds.map(id => 
        delAppointByUserId({ id: id })
      );

      Promise.all(deletePromises)
        .then((results) => {
          const successCount = results.filter(r => r && (r.flag !== false)).length;
          if (successCount === this.selectedAppointmentIds.length) {
            this.$message.success(`成功删除 ${successCount} 条预约`);
          } else {
            this.$message.warning(`部分删除失败，成功删除 ${successCount} 条`);
          }
          this.selectedAppointmentIds = [];
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
    // 删除单个预约
    delAppoint(item) {
      this.$confirm('确认删除该预约信息？', '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delAppointByUserId({ id: item.id })
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
    // 查看详情
    handleDetail(item) {
      this.showDetail = true;
      this.detailObj = Object.assign({}, { ...item });
    },
    detailClose() {
      this.showDetail = false;
    },
    // 编辑/回复
    handleEdit(item) {
      this.dialogVisible = true;
      this.detailObj = Object.assign({}, { ...item });
    },
    closeRevise() {
      this.dialogVisible = false;
    },
    // 提交回复
    submitRevise() {
      if (!this.detailObj.answer || this.detailObj.answer.trim() === '') {
        this.$message.warning('请输入回复内容');
        return;
      }

      this.detailObj.status = 1;
      this.submitting = true;
      reviseAppointByUserId(this.detailObj)
        .then(res => {
          this.$message.success('回复成功');
          this.dialogVisible = false;
          this.getData();
        })
        .catch(err => {
          console.error('回复失败:', err);
          this.$message.error('回复失败，请重试');
        })
        .finally(() => {
          this.submitting = false;
        });
    },
    // 去预约
    goToAppointment() {
      this.$router.push("/home/appointment").catch((err) => err);
    },
    // 截断文本
    truncateText(text, length) {
      if (!text) return '';
      if (text.length <= length) return text;
      return text.substring(0, length) + '...';
    },
  },
  mounted() {
    this.$store.commit("updateUserActiveIndex", "4-2");
    this.getData();
  }
}
</script>

<style lang="less" scoped>
.my-appointment-page {
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

  .appointment-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .appointment-card {
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

    .appointment-icon {
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

    .appointment-content {
      flex: 1;
      display: flex;
      flex-direction: column;

      .appointment-title {
        margin: 0 0 10px 0;
        font-size: 18px;
        color: #303133;
        font-weight: 600;
      }

      .appointment-text {
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

      .appointment-info {
        margin-bottom: 15px;

        .info-row {
          display: flex;
          gap: 20px;
          margin-bottom: 8px;
          flex-wrap: wrap;

          .info-item {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 12px;
            color: #909399;

            i {
              font-size: 14px;
            }
          }
        }
      }

      .appointment-status {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .appointment-actions {
          display: flex;
          gap: 10px;
        }
      }
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
        width: 120px;
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
  .my-appointment-page {
    width: 100%;
    padding: 15px;

    .appointment-card {
      flex-direction: column;

      .appointment-icon {
        margin-right: 0;
        margin-bottom: 15px;
      }
    }
  }
}
</style>
