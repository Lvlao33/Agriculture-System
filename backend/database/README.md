# 数据库建表脚本使用说明

## 文件说明

- `create_tables.sql` - 完整的MySQL建表脚本，包含所有表结构

## 使用方法

### 方法一：使用MySQL命令行

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS farmproject_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用数据库
USE farmproject_db;

# 执行脚本
SOURCE /path/to/create_tables.sql;
```

### 方法二：直接执行SQL文件

```bash
mysql -u root -p farmproject_db < create_tables.sql
```

### 方法三：使用MySQL Workbench或其他数据库工具

1. 打开MySQL Workbench
2. 连接到数据库服务器
3. 创建数据库 `farmproject_db`（如果不存在）
4. 打开 `create_tables.sql` 文件
5. 执行脚本

## 数据库配置

确保 `application.properties` 中的数据库配置正确：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/farmproject_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=your_password
```

## 表结构说明

脚本包含以下22个表：

### 用户模块
1. `users` - 用户表

### 专家模块
2. `experts` - 专家表
3. `expert_specialties` - 专家专长表
4. `questions` - 问题表
5. `question_attachments` - 问题附件表
6. `question_tags` - 问题标签表
7. `answers` - 回答表
8. `appointments` - 预约表
9. `knowledge` - 知识库表
10. `knowledge_categories` - 知识分类表
11. `knowledge_tags` - 知识标签表

### 交易模块
12. `products` - 商品表
13. `cart_products` - 购物车表
14. `orders` - 订单表
15. `order_items` - 订单项表
16. `addresses` - 地址表
17. `demands` - 需求表

### 金融模块
18. `loan_products` - 贷款产品表
19. `loans` - 贷款申请表
20. `loan_user_status` - 贷款用户状态表
21. `loan_records` - 贷款记录表
22. `loan_files` - 贷款文件表

## 注意事项

1. **外键约束**：脚本包含外键约束，确保数据完整性
2. **字符集**：使用 `utf8mb4` 字符集，支持emoji和特殊字符
3. **索引**：已为常用查询字段创建索引
4. **级联删除**：部分关联表设置了级联删除，删除主表记录时会自动删除关联记录

## 执行后验证

执行脚本后，可以使用以下SQL验证表是否创建成功：

```sql
-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC users;
DESC products;
DESC orders;
-- ... 等等
```

## 重置数据库（谨慎操作）

如果需要重新创建所有表，脚本会自动删除已存在的表（`DROP TABLE IF EXISTS`），但请注意：

1. **备份数据**：执行前请备份重要数据
2. **外键检查**：脚本会自动处理外键检查的开启和关闭
3. **数据丢失**：执行脚本会删除所有现有数据

## 故障排除

如果遇到外键约束错误：
1. 确保按顺序执行脚本
2. 检查是否有其他表引用了要删除的表
3. 可以临时禁用外键检查：`SET FOREIGN_KEY_CHECKS = 0;`































