-- ============================================
-- 农业系统数据库建表脚本
-- 数据库: farmproject_db
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 用户表 (users)
-- ============================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    `password_hash` VARCHAR(128) NOT NULL COMMENT '密码哈希',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(256) DEFAULT NULL COMMENT '头像URL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `role` VARCHAR(32) DEFAULT NULL COMMENT '角色: FARMER/EXPERT/BANK',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 专家表 (experts)
-- ============================================
DROP TABLE IF EXISTS `experts`;
CREATE TABLE `experts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '专家ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '关联用户ID',
    `name` VARCHAR(255) NOT NULL COMMENT '专家姓名',
    `title` VARCHAR(255) NOT NULL COMMENT '专家职称',
    `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '专家描述',
    `experience_years` INT DEFAULT NULL COMMENT '经验年限',
    `contact_info` VARCHAR(255) DEFAULT NULL COMMENT '联系方式',
    `is_available` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可用',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    INDEX `idx_is_available` (`is_available`),
    CONSTRAINT `fk_expert_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家表';

-- ============================================
-- 3. 专家专长表 (expert_specialties) - ElementCollection
-- ============================================
DROP TABLE IF EXISTS `expert_specialties`;
CREATE TABLE `expert_specialties` (
    `expert_id` BIGINT NOT NULL COMMENT '专家ID',
    `specialty` VARCHAR(255) NOT NULL COMMENT '专长',
    PRIMARY KEY (`expert_id`, `specialty`),
    CONSTRAINT `fk_expert_specialties_expert` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家专长表';

-- ============================================
-- 4. 问题表 (questions)
-- ============================================
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    `title` VARCHAR(255) NOT NULL COMMENT '问题标题',
    `content` VARCHAR(2000) NOT NULL COMMENT '问题内容',
    `user_id` BIGINT DEFAULT NULL COMMENT '提问用户ID',
    `expert_id` BIGINT DEFAULT NULL COMMENT '指定专家ID',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/ANSWERED/CLOSED',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_expert_id` (`expert_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_question_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_question_expert` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';

-- ============================================
-- 5. 问题附件表 (question_attachments) - ElementCollection
-- ============================================
DROP TABLE IF EXISTS `question_attachments`;
CREATE TABLE `question_attachments` (
    `question_id` BIGINT NOT NULL COMMENT '问题ID',
    `file_url` VARCHAR(512) NOT NULL COMMENT '文件URL',
    PRIMARY KEY (`question_id`, `file_url`),
    CONSTRAINT `fk_question_attachments_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题附件表';

-- ============================================
-- 6. 问题标签表 (question_tags) - ElementCollection
-- ============================================
DROP TABLE IF EXISTS `question_tags`;
CREATE TABLE `question_tags` (
    `question_id` BIGINT NOT NULL COMMENT '问题ID',
    `tag` VARCHAR(255) NOT NULL COMMENT '标签',
    PRIMARY KEY (`question_id`, `tag`),
    CONSTRAINT `fk_question_tags_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题标签表';

-- ============================================
-- 7. 回答表 (answers)
-- ============================================
DROP TABLE IF EXISTS `answers`;
CREATE TABLE `answers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回答ID',
    `question_id` BIGINT NOT NULL COMMENT '问题ID',
    `expert_id` BIGINT NOT NULL COMMENT '专家ID',
    `content` VARCHAR(5000) NOT NULL COMMENT '回答内容',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_expert_id` (`expert_id`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_answer_expert` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回答表';

-- ============================================
-- 8. 预约表 (appointments)
-- ============================================
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `expert_id` BIGINT DEFAULT NULL COMMENT '专家ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(255) DEFAULT NULL COMMENT '用户姓名',
    `user_contact` VARCHAR(255) DEFAULT NULL COMMENT '用户联系方式',
    `appointment_start_time` DATETIME NOT NULL COMMENT '预约开始时间',
    `appointment_end_time` DATETIME NOT NULL COMMENT '预约结束时间',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '预约描述',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/CONFIRMED/COMPLETED/CANCELLED',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_expert_id` (`expert_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_appointment_time` (`appointment_start_time`),
    CONSTRAINT `fk_appointment_expert` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_appointment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- ============================================
-- 9. 知识库表 (knowledge)
-- ============================================
DROP TABLE IF EXISTS `knowledge`;
CREATE TABLE `knowledge` (
    `knowledge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '知识ID',
    `title` VARCHAR(255) NOT NULL COMMENT '标题',
    `content` VARCHAR(5000) NOT NULL COMMENT '内容',
    `summary` VARCHAR(1000) DEFAULT NULL COMMENT '摘要',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID（专家）',
    `source` VARCHAR(255) DEFAULT NULL COMMENT '来源',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `is_published` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否发布',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`knowledge_id`),
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_is_published` (`is_published`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_knowledge_author` FOREIGN KEY (`author_id`) REFERENCES `experts` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- ============================================
-- 10. 知识分类表 (knowledge_categories) - ElementCollection
-- ============================================
DROP TABLE IF EXISTS `knowledge_categories`;
CREATE TABLE `knowledge_categories` (
    `knowledge_id` BIGINT NOT NULL COMMENT '知识ID',
    `category` VARCHAR(255) NOT NULL COMMENT '分类',
    PRIMARY KEY (`knowledge_id`, `category`),
    CONSTRAINT `fk_knowledge_categories_knowledge` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge` (`knowledge_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识分类表';

-- ============================================
-- 11. 知识标签表 (knowledge_tags) - ElementCollection
-- ============================================
DROP TABLE IF EXISTS `knowledge_tags`;
CREATE TABLE `knowledge_tags` (
    `knowledge_id` BIGINT NOT NULL COMMENT '知识ID',
    `tag` VARCHAR(255) NOT NULL COMMENT '标签',
    PRIMARY KEY (`knowledge_id`, `tag`),
    CONSTRAINT `fk_knowledge_tags_knowledge` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge` (`knowledge_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识标签表';

-- ============================================
-- 12. 商品表 (products)
-- ============================================
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(255) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '商品描述',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT '图片URL',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `category` VARCHAR(255) DEFAULT NULL COMMENT '分类',
    `unit` VARCHAR(32) DEFAULT NULL COMMENT '单位',
    `is_available` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可用',
    `sold_count` INT NOT NULL DEFAULT 0 COMMENT '销售数量',
    PRIMARY KEY (`id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_is_available` (`is_available`),
    INDEX `idx_name` (`name`),
    CONSTRAINT `fk_product_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 13. 购物车表 (cart_products)
-- ============================================
DROP TABLE IF EXISTS `cart_products`;
CREATE TABLE `cart_products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_product_id` (`product_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ============================================
-- 14. 订单表 (orders)
-- ============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_number` VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `user_id` BIGINT NOT NULL COMMENT '用户ID（买家）',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `shipping_address` VARCHAR(512) DEFAULT NULL COMMENT '收货地址',
    `receiver_name` VARCHAR(255) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(32) DEFAULT NULL COMMENT '收货人电话',
    `payment_method` VARCHAR(32) DEFAULT NULL COMMENT '支付方式',
    `payment_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_number` (`order_number`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_payment_status` (`payment_status`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 15. 订单项表 (order_items)
-- ============================================
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(255) DEFAULT NULL COMMENT '商品名称（快照）',
    `product_price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格（快照）',
    `quantity` INT NOT NULL COMMENT '数量',
    `subtotal` DECIMAL(10, 2) NOT NULL COMMENT '小计',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ============================================
-- 16. 地址表 (addresses)
-- ============================================
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(255) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(32) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(64) NOT NULL COMMENT '省份',
    `city` VARCHAR(64) NOT NULL COMMENT '城市',
    `district` VARCHAR(64) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(512) NOT NULL COMMENT '详细地址',
    `postal_code` VARCHAR(16) DEFAULT NULL COMMENT '邮政编码',
    `is_default` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否默认地址',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_default` (`is_default`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地址表';

-- ============================================
-- 17. 需求表 (demands)
-- ============================================
DROP TABLE IF EXISTS `demands`;
CREATE TABLE `demands` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '需求ID',
    `title` VARCHAR(255) NOT NULL COMMENT '需求标题',
    `description` VARCHAR(2000) DEFAULT NULL COMMENT '需求描述',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `category` VARCHAR(255) DEFAULT NULL COMMENT '分类',
    `quantity` INT DEFAULT NULL COMMENT '数量',
    `unit` VARCHAR(32) DEFAULT NULL COMMENT '单位',
    `contact_info` VARCHAR(255) DEFAULT NULL COMMENT '联系方式',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_demand_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求表';

-- ============================================
-- 18. 贷款产品表 (loan_products)
-- ============================================
DROP TABLE IF EXISTS `loan_products`;
CREATE TABLE `loan_products` (
    `loan_product_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '贷款产品ID',
    `name` VARCHAR(255) NOT NULL COMMENT '产品名称',
    `product_status` VARCHAR(32) NOT NULL COMMENT '产品状态: SALE/DELETED',
    `bank` VARCHAR(255) NOT NULL COMMENT '银行名称',
    `amount` DECIMAL(14, 2) NOT NULL COMMENT '贷款额度',
    `term` INT NOT NULL COMMENT '期限（月）',
    `rate` DECIMAL(5, 2) NOT NULL COMMENT '利率',
    `fastest_disbursement` VARCHAR(255) NOT NULL COMMENT '最快放款时间',
    `description` TEXT NOT NULL COMMENT '产品描述',
    `tags` VARCHAR(255) NOT NULL COMMENT '标签',
    `staff_id` BIGINT NOT NULL COMMENT '创建人员工ID',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`loan_product_id`),
    INDEX `idx_product_status` (`product_status`),
    INDEX `idx_staff_id` (`staff_id`),
    INDEX `idx_bank` (`bank`),
    CONSTRAINT `fk_loan_product_staff` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='贷款产品表';

-- ============================================
-- 19. 贷款申请表 (loans)
-- ============================================
DROP TABLE IF EXISTS `loans`;
CREATE TABLE `loans` (
    `loan_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '贷款ID',
    `loan_amount` DECIMAL(14, 2) NOT NULL COMMENT '申请金额',
    `loan_purpose` VARCHAR(255) DEFAULT NULL COMMENT '贷款用途',
    `loan_term_months` INT DEFAULT NULL COMMENT '贷款期限（月）',
    `interest_rate` DECIMAL(5, 2) DEFAULT NULL COMMENT '利率（年化）',
    `application_date` DATETIME DEFAULT NULL COMMENT '申请时间',
    `update_date` DATETIME DEFAULT NULL COMMENT '更新时间',
    `status` VARCHAR(32) DEFAULT NULL COMMENT '状态: CREATED/REVIEWING/APPROVED/REJECTED/SIGNED/REPAYING/CLEARED_NORMAL/CLEARED_EARLY',
    `disbursement_date` DATETIME DEFAULT NULL COMMENT '放款时间',
    `repayment_due_date` DATE DEFAULT NULL COMMENT '还款截止日期',
    `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    `loan_product_id` BIGINT DEFAULT NULL COMMENT '关联贷款产品ID',
    `staff_id` BIGINT DEFAULT NULL COMMENT '处理人员工ID',
    PRIMARY KEY (`loan_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_loan_product_id` (`loan_product_id`),
    INDEX `idx_staff_id` (`staff_id`),
    INDEX `idx_application_date` (`application_date`),
    CONSTRAINT `fk_loan_product` FOREIGN KEY (`loan_product_id`) REFERENCES `loan_products` (`loan_product_id`) ON DELETE SET NULL,
    CONSTRAINT `fk_loan_staff` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='贷款申请表';

-- ============================================
-- 20. 贷款用户状态表 (loan_user_status) - 多对多关系表
-- ============================================
DROP TABLE IF EXISTS `loan_user_status`;
CREATE TABLE `loan_user_status` (
    `loan_user_status_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `loan_id` BIGINT NOT NULL COMMENT '贷款ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID（农户）',
    `status` VARCHAR(32) DEFAULT NULL COMMENT '状态: CREATED/REVIEWING/APPROVED/REJECTED/SIGNED/REPAYING/CLEARED_NORMAL/CLEARED_EARLY',
    PRIMARY KEY (`loan_user_status_id`),
    INDEX `idx_loan_id` (`loan_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    CONSTRAINT `fk_loan_user_status_loan` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`loan_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_loan_user_status_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='贷款用户状态表';

-- ============================================
-- 21. 贷款记录表 (loan_records)
-- ============================================
DROP TABLE IF EXISTS `loan_records`;
CREATE TABLE `loan_records` (
    `loan_record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `loan_id` BIGINT NOT NULL COMMENT '贷款ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作者用户ID',
    `record_details` VARCHAR(1000) DEFAULT NULL COMMENT '记录详情',
    `apply_status` VARCHAR(32) DEFAULT NULL COMMENT '申请状态',
    `record_date` DATETIME NOT NULL COMMENT '记录时间',
    PRIMARY KEY (`loan_record_id`),
    INDEX `idx_loan_id` (`loan_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_record_date` (`record_date`),
    CONSTRAINT `fk_loan_record_loan` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`loan_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_loan_record_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='贷款记录表';

-- ============================================
-- 22. 贷款文件表 (loan_files)
-- ============================================
DROP TABLE IF EXISTS `loan_files`;
CREATE TABLE `loan_files` (
    `loan_file_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `loan_id` BIGINT NOT NULL COMMENT '贷款ID',
    `user_id` BIGINT NOT NULL COMMENT '操作用户ID',
    `file_name` VARCHAR(255) DEFAULT NULL COMMENT '文件名',
    `file_path` VARCHAR(255) DEFAULT NULL COMMENT '文件路径',
    `file_type` VARCHAR(16) DEFAULT NULL COMMENT '文件类型',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`loan_file_id`),
    INDEX `idx_loan_id` (`loan_id`),
    INDEX `idx_user_id` (`user_id`),
    CONSTRAINT `fk_loan_file_loan` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`loan_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_loan_file_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='贷款文件表';

-- ============================================
-- 恢复外键检查
-- ============================================
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 脚本执行完成
-- ============================================






