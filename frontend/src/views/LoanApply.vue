<template>
  <div class="loan-apply">
    <div class="apply-container">
      <div class="apply-header">
        <h2>贷款申请</h2>
        <p class="product-name">产品：{{ product.name }} - {{ product.bank }}</p>
      </div>

      <el-form :model="loanForm" :rules="rules" ref="loanForm" label-width="150px" class="loan-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          
          <el-form-item label="申请金额" prop="loanAmount">
            <el-input-number
              v-model="loanForm.loanAmount"
              :min="0"
              :max="maxAmount"
              :precision="2"
              :step="1000"
              placeholder="请输入申请金额"
              style="width: 100%"
            ></el-input-number>
            <div class="form-tip">最高可申请：{{ maxAmount.toLocaleString() }} 元</div>
          </el-form-item>

          <el-form-item label="贷款用途" prop="loanPurpose">
            <el-input
              v-model="loanForm.loanPurpose"
              type="textarea"
              :rows="4"
              maxlength="255"
              show-word-limit
              placeholder="请详细说明贷款用途，例如：购买农资、扩大种植规模等"
            ></el-input>
          </el-form-item>

          <el-form-item label="贷款期限" prop="loanTermMonths">
            <el-select v-model="loanForm.loanTermMonths" placeholder="请选择贷款期限" style="width: 100%">
              <el-option
                v-for="term in availableTerms"
                :key="term"
                :label="`${term} 个月`"
                :value="term"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="参考利率" prop="interestRate">
            <el-input
              v-model="loanForm.interestRate"
              disabled
              style="width: 100%"
            >
              <template slot="append">%</template>
            </el-input>
            <div class="form-tip">年化利率，仅供参考，实际利率以银行审批为准</div>
          </el-form-item>
        </div>

        <!-- 其他材料 -->
        <div class="form-section">
          <h3 class="section-title">其他材料</h3>
          
          <div class="upload-section" v-for="(material, index) in requiredMaterials" :key="index">
            <div class="upload-item">
              <div class="upload-label">
                <i class="el-icon-document"></i>
                <span>{{ material.name }}</span>
                <span class="required-mark" v-if="material.required">*</span>
              </div>
              <div class="upload-description">{{ material.description }}</div>
              <el-upload
                class="upload-demo"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :http-request="(options) => handleCustomUpload(options, index)"
                :on-success="(response, file) => handleUploadSuccess(response, file, index)"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                :file-list="material.fileList"
                :limit="material.limit || 1"
                :accept="material.accept || '.pdf,.jpg,.jpeg,.png,.doc,.docx'"
              >
                <el-button size="small" type="primary">
                  <i class="el-icon-upload2"></i> 点击上传
                </el-button>
                <div slot="tip" class="el-upload__tip">
                  支持 {{ material.accept || 'PDF、JPG、PNG、DOC、DOCX' }} 格式，单个文件不超过10MB
                </div>
              </el-upload>
              <el-button
                v-if="material.templateUrl"
                type="text"
                size="small"
                @click="downloadTemplate(material.templateUrl)"
                style="margin-top: 10px;"
              >
                <i class="el-icon-download"></i> 下载模板
              </el-button>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button @click="goBack">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ submitting ? '提交中...' : '确认提交' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { applyLoan, applyLoanWithFiles, uploadLoanFile, getLoanProducts } from '../api/finance'

export default {
  name: 'LoanApply',
  data() {
    return {
      product: {},
      submitting: false,
      maxAmount: 0,
      loanForm: {
        loanAmount: null,
        loanPurpose: '',
        loanTermMonths: null,
        interestRate: null,
        productId: null
      },
      rules: {
        loanAmount: [
          { required: true, message: '请输入申请金额', trigger: 'blur' },
          { type: 'number', min: 0, message: '申请金额必须大于0', trigger: 'blur' }
        ],
        loanPurpose: [
          { required: true, message: '请输入贷款用途', trigger: 'blur' },
          { min: 5, max: 255, message: '贷款用途长度在 5 到 255 个字符', trigger: 'blur' }
        ],
        loanTermMonths: [
          { required: true, message: '请选择贷款期限', trigger: 'change' }
        ]
      },
      availableTerms: [6, 12, 18, 24, 30, 36],
      requiredMaterials: [
        {
          name: '身份证扫描件',
          description: '请提供本人有效身份证正反面扫描件',
          required: true,
          accept: '.pdf,.jpg,.jpeg,.png',
          limit: 2,
          fileList: []
        },
        {
          name: '营业执照',
          description: '如为个体工商户或企业，需提供营业执照扫描件',
          required: false,
          accept: '.pdf,.jpg,.jpeg,.png',
          fileList: []
        },
        {
          name: '银行流水',
          description: '提供近6个月的银行流水记录',
          required: true,
          accept: '.pdf,.jpg,.jpeg,.png',
          fileList: []
        },
        {
          name: '贷款申请表',
          description: '填写完整的贷款申请表',
          required: true,
          templateUrl: '/templates/loan_application_form.docx',
          accept: '.pdf,.doc,.docx',
          fileList: []
        },
        {
          name: '收入证明',
          description: '提供收入证明文件',
          required: true,
          accept: '.pdf,.jpg,.jpeg,.png,.doc,.docx',
          fileList: []
        }
      ],
      uploadUrl: 'http://localhost:8080/api/finance/loans/apply',
      uploadHeaders: {
        'Authorization': window.localStorage.token || ''
      },
      currentLoanId: null,
      allProducts: [] // 存储所有产品列表，用于通过 productName 查询 productId
    }
  },
  mounted() {
    this.loadProductsAndProduct();
  },
  methods: {
    async loadProductsAndProduct() {
      // 先加载所有产品列表
      try {
        const response = await getLoanProducts()
        // 处理响应数据，确保数据结构正确
        if (response && Array.isArray(response)) {
          this.allProducts = response
        } else if (response && response.data && Array.isArray(response.data)) {
          this.allProducts = response.data
        } else if (response && response.list && Array.isArray(response.list)) {
          this.allProducts = response.list
        }
      } catch (error) {
        console.error('加载产品列表失败:', error)
      }
      
      // 然后加载选中的产品
      this.loadProduct()
    },
    loadProduct() {
      const productStr = localStorage.getItem('loanProduct');
      if (productStr) {
        this.product = JSON.parse(productStr);
        this.maxAmount = this.product.amount || 200000;
        this.loanForm.interestRate = this.product.rate || 0;
        
        // 如果产品有 id，直接使用
        if (this.product.id != null) {
          const productIdNum = Number(this.product.id);
          if (!isNaN(productIdNum)) {
            this.loanForm.productId = productIdNum;
          }
        } else if (this.product.name) {
          // 如果产品没有 id，通过 productName 从产品列表中查找对应的 id
          const foundProduct = this.allProducts.find(p => p.name === this.product.name);
          if (foundProduct && foundProduct.id != null) {
            const productIdNum = Number(foundProduct.id);
            if (!isNaN(productIdNum)) {
              this.loanForm.productId = productIdNum;
            }
          }
        }
        
        // 设置默认期限
        if (this.product.term) {
          this.loanForm.loanTermMonths = this.product.term;
        }
      } else {
        this.$message.warning('未找到产品信息');
        this.$router.push('/home/smartMatch');
      }
    },
    beforeUpload(file) {
      const isValidType = /\.(pdf|jpg|jpeg|png|doc|docx)$/i.test(file.name);
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isValidType) {
        this.$message.error('上传文件格式不正确！');
        return false;
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB！');
        return false;
      }
      return true;
    },
    async handleCustomUpload(options, materialIndex) {
      // 自定义上传方法
      // 注意：这里需要先提交贷款申请获得 loanId，然后再上传文件
      // 为了简化流程，这里先保存文件，等提交申请后再上传
      const file = options.file;
      if (!this.requiredMaterials[materialIndex].fileList) {
        this.$set(this.requiredMaterials[materialIndex], 'fileList', []);
      }
      // 先添加到文件列表，实际提交时再上传
      this.requiredMaterials[materialIndex].fileList.push({
        name: file.name,
        raw: file,
        status: 'ready'
      });
      this.$message.success('文件已选择，将在提交申请时上传');
    },
    handleUploadSuccess(response, file, materialIndex) {
      this.$message.success('文件上传成功');
      if (!this.requiredMaterials[materialIndex].fileList) {
        this.$set(this.requiredMaterials[materialIndex], 'fileList', []);
      }
      this.requiredMaterials[materialIndex].fileList.push({
        name: file.name,
        url: response.url || response.data || file.response?.url
      });
    },
    handleUploadError(err, file) {
      this.$message.error('文件上传失败，请重试');
      console.error('Upload error:', err);
    },
    downloadTemplate(url) {
      if (url) {
        window.open(url, '_blank');
      } else {
        this.$message.info('模板文件暂未提供');
      }
    },
    validateAmount() {
      if (this.loanForm.loanAmount > this.maxAmount) {
        this.$message.error(`申请金额不能超过最高额度 ${this.maxAmount.toLocaleString()} 元`);
        return false;
      }
      if (this.loanForm.loanAmount <= 0) {
        this.$message.error('申请金额必须大于0');
        return false;
      }
      return true;
    },
    async submitForm() {
      this.$refs.loanForm.validate(async (valid) => {
        if (!valid) {
          return false;
        }

        if (!this.validateAmount()) {
          return false;
        }

        // 检查必填材料
        const requiredMaterials = this.requiredMaterials.filter(m => m.required);
        const missingMaterials = requiredMaterials.filter(m => 
          !m.fileList || m.fileList.length === 0
        );

        if (missingMaterials.length > 0) {
          this.$message.warning(`请上传必填材料：${missingMaterials.map(m => m.name).join('、')}`);
          return false;
        }

        this.submitting = true;

        try {
          // 提交贷款申请
          const loanData = {
            loanAmount: this.loanForm.loanAmount,
            loanPurpose: this.loanForm.loanPurpose,
            loanTermMonths: this.loanForm.loanTermMonths,
            interestRate: this.loanForm.interestRate,
            productId: this.loanForm.productId
          };

          // Create FormData including loan fields and all files
          const formData = new FormData();
          // 临时方案：使用常量 farmerId，待用户认证系统完成后从 token 获取
          formData.append('farmerId', '1'); // TODO: 待用户认证系统完成后，从 token 中获取 farmerId
          formData.append('loanAmount', this.loanForm.loanAmount);
          formData.append('loanPurpose', this.loanForm.loanPurpose);
          formData.append('loanTermMonths', this.loanForm.loanTermMonths);
          formData.append('interestRate', this.loanForm.interestRate != null ? this.loanForm.interestRate : 0);
          if (this.loanForm.productId) formData.append('productId', this.loanForm.productId);

          // Append all selected files; use the same field name 'file' for each file
          for (const material of this.requiredMaterials) {
            if (material.fileList && material.fileList.length > 0) {
              for (const fileItem of material.fileList) {
                if (fileItem.raw) {
                  formData.append('file', fileItem.raw, fileItem.name);
                }
              }
            }
          }

          const response = await applyLoanWithFiles(formData);

          if (response && response.success !== false) {
            // backend may return loan id in different fields
            const loanId = response.data?.loanId || response.loanId || response.id || response.data?.id;
            this.currentLoanId = loanId;
            this.$message.success('申请提交并上传文件成功');

            // 跳转到成功页面
            this.$router.push({
              path: '/home/loanApplySuccess',
              query: {
                loanId: loanId || 'unknown'
              }
            });
          } else {
            this.$message.error(response?.message || '申请提交失败，请重试');
          }
        } catch (error) {
          console.error('Submit error:', error);
          this.$message.error('申请提交失败，请重试');
        } finally {
          this.submitting = false;
        }
      });
    },
    goBack() {
      this.$router.go(-1);
    }
  }
}
</script>

<style lang="less" scoped>
.loan-apply {
  width: 100%;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20px 0;
}

.apply-container {
  width: 1100px;
  margin: 0 auto;
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.apply-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;

  h2 {
    font-size: 28px;
    color: #333;
    margin: 0 0 10px 0;
  }

  .product-name {
    font-size: 16px;
    color: #666;
    margin: 0;
  }
}

.loan-form {
  .form-section {
    margin-bottom: 40px;
    padding-bottom: 30px;
    border-bottom: 1px solid #e0e0e0;

    &:last-child {
      border-bottom: none;
    }

    .section-title {
      font-size: 20px;
      color: #333;
      margin-bottom: 20px;
      padding-left: 10px;
      border-left: 4px solid #4CAF50;
    }
  }

  .form-tip {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
  }

  .upload-section {
    margin-bottom: 30px;

    .upload-item {
      background-color: #f9f9f9;
      border-radius: 8px;
      padding: 20px;

      .upload-label {
        display: flex;
        align-items: center;
        font-size: 16px;
        font-weight: bold;
        color: #333;
        margin-bottom: 10px;

        i {
          font-size: 18px;
          color: #4CAF50;
          margin-right: 8px;
        }

        .required-mark {
          color: #f56c6c;
          margin-left: 5px;
        }
      }

      .upload-description {
        font-size: 14px;
        color: #666;
        margin-bottom: 15px;
        line-height: 1.6;
      }
    }
  }

  .form-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 40px;
    padding-top: 30px;
    border-top: 1px solid #e0e0e0;

    .el-button {
      padding: 12px 40px;
      font-size: 16px;
    }
  }
}

@media (max-width: 1200px) {
  .apply-container {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .loan-form {
    /deep/ .el-form-item__label {
      width: 100% !important;
      text-align: left;
    }

    /deep/ .el-form-item__content {
      margin-left: 0 !important;
    }
  }
}
</style>

