<template>
  <div class="edit-product-page">
    <el-card>
      <h3>编辑商品</h3>
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品图片">
          <div class="images-row">
            <div
              class="existing-image"
              v-for="(img, idx) in images"
              :key="idx"
            >
              <img :src="$store.state.imgShowRoad + '/file/' + img" class="preview-large" />
              <div class="image-actions">
                <el-button size="mini" type="primary" @click="triggerReplace(idx)">修改</el-button>
                <el-button size="mini" @click="removeImage(idx)">删除</el-button>
              </div>
            </div>

            <el-upload
              ref="upload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              list-type="picture-card"
              :on-success="handleUploadSuccess"
              :file-list="fileList"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </div>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="商品名称">
              <el-input v-model="form.name" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价">
              <el-input-number v-model.number="form.price" :min="0" :step="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存">
              <el-input-number v-model.number="form.stock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品分类">
          <el-input v-model="form.category" placeholder="请输入商品分类" style="width: 400px;" />
        </el-form-item>

        <el-form-item label="商品简介">
          <el-input type="textarea" v-model="form.description" placeholder="请输入商品简介" class="wide-textarea" />
        </el-form-item>

        <el-form-item>
          <el-button @click="onCancel">返回</el-button>
          <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getProductDetail, updateProduct } from "../api/trade";
export default {
  name: "EditProduct",
  data() {
    return {
      form: {
        name: "",
        description: "",
        price: 0,
        stock: 0,
        imageUrl: "",
        category: "",
      },
      fileList: [],
      saving: false,
      uploadUrl: this.$store.state.fileUploadRoad + "/file/upload/order",
      uploadHeaders: {
        Authorization: window.localStorage.token,
      },
      images: [], // 支持多图展示
      replacingIndex: null,
    };
  },
  created() {
    const id = this.$route.params.id || this.$route.query.id;
    if (id) {
      getProductDetail(id)
        .then((res) => {
          if (res.flag === true || res.success === true) {
            const p = res.data;
            this.form = {
              name: p.name || "",
              description: p.description || "",
              price: p.price || 0,
              stock: p.stock || 0,
              imageUrl: p.imageUrl || "",
              category: p.category || "",
            };
          // 初始化 images 数组（后端可能只返回单个 imageUrl 字符串）
          this.images = this.form.imageUrl ? [this.form.imageUrl] : [];
          } else {
            this.$message.error("获取商品详情失败");
          }
        })
        .catch(() => {
          this.$message.error("获取商品详情失败");
        });
    } else {
      this.$message.error("缺少商品ID");
    }
  },
  methods: {
    handleUploadSuccess(response, file) {
      if (response && response.flag === true) {
        const imageUrl = response.data;
        this.fileList = [file];
        if (this.replacingIndex !== null && this.replacingIndex >= 0) {
          // 替换指定索引的图片
          this.images.splice(this.replacingIndex, 1, imageUrl);
          this.replacingIndex = null;
        } else {
          // 添加新图片
          this.images.push(imageUrl);
        }
        // 同步第一个图片到 form.imageUrl（兼容旧后端）
        this.form.imageUrl = this.images[0] || "";
        this.$message.success(response.message || "上传成功");
      } else {
        this.$message.error((response && response.data) || "上传失败");
      }
    },
    triggerReplace(index) {
      this.replacingIndex = index;
      // 打开上传文件选择
      const uploadComp = this.$refs.upload;
      if (uploadComp && uploadComp.$el) {
        const input = uploadComp.$el.querySelector('input[type="file"]');
        if (input) input.click();
      }
    },
    removeImage(index) {
      if (index >= 0 && index < this.images.length) {
        this.images.splice(index, 1);
        this.form.imageUrl = this.images[0] || "";
      }
    },
    
    onCancel() {
      this.$router.back();
    },
    onSave() {
      const id = this.$route.params.id || this.$route.query.id;
      if (!id) return this.$message.error("缺少商品ID");
      this.saving = true;
      updateProduct(id, {
        name: this.form.name,
        description: this.form.description,
        price: this.form.price,
        stock: this.form.stock,
        imageUrl: this.form.imageUrl,
        category: this.form.category,
      })
        .then((res) => {
          if (res.flag === true || res.success === true) {
            this.$message.success(res.message || "保存成功");
            this.$router.back();
          } else {
            this.$message.error(res.message || res.data || "保存失败");
          }
        })
        .catch(() => {
          this.$message.error("保存失败");
        })
        .finally(() => {
          this.saving = false;
        });
    },
  },
};
</script>

<style scoped>
.preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  margin-bottom: 8px;
}
.preview-large {
  width: 220px;
  height: 140px;
  object-fit: cover;
  border-radius: 6px;
}
.images-row {
  display: flex;
  gap: 18px;
  align-items: flex-start;
}
.existing-image {
  position: relative;
}
.image-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  justify-content: center;
}
.wide-textarea .el-textarea__inner {
  min-height: 120px;
  /* 缩短三分之一（从70%降为约46.67%） */
  width: 100%;
  max-width: 1000px;
  resize: vertical;
}
/* 确保包裹元素宽度生效，覆盖默认 100% 行为 */
.wide-textarea {
  display: inline-block;
  width: 70%;
  max-width: 1000px;
}
</style>


