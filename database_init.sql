-- ============================================
-- 农业系统数据库初始化脚本
-- 数据库名称: farmproject_db
-- MySQL版本: 8.0+
-- ============================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `farmproject_db` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 使用数据库
USE `farmproject_db`;

-- 3. 创建用户表
-- 如果数据库中不存在表后端的modal可自动创建表
-- CREATE TABLE IF NOT EXISTS `users` (
--     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
--     `username` VARCHAR(64) NOT NULL COMMENT '用户名，唯一',
--     `password_hash` VARCHAR(128) NOT NULL COMMENT '密码哈希值（MD5加密）',
--     `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
--     `avatar` VARCHAR(256) DEFAULT NULL COMMENT '头像路径',
--     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     PRIMARY KEY (`id`),
--     UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
--     KEY `idx_username` (`username`) COMMENT '用户名索引'
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 4. 插入测试数据（可选）
-- 测试用户：用户名 test，密码 123456（MD5: e10adc3949ba59abbe56e057f20f883e）
INSERT INTO
    `users` (
        `username`,
        `password_hash`,
        `nickname`,
        `avatar`,
        `created_at`,
        `role`
    )
VALUES (
        'test',
        'e10adc3949ba59abbe56e057f20f883e',
        '测试用户',
        'default_avatar.png',
        NOW(),
        'STAFF'
    )
ON DUPLICATE KEY UPDATE
    `username` = `username`;

-- 插入测试贷款产品数据
-- 假设测试用户（STAFF）的 ID 为 1
INSERT INTO
    loan_products (
        name,
        product_status,
        bank,
        amount,
        term,
        rate,
        fastest_disbursement,
        description,
        tags,
        created_at,
        updated_at,
        staff_id
    )
VALUES (
        '农业生产贷款A',
        'SALE',
        '农业银行',
        100000.00,
        12,
        4.35,
        '3个工作日',
        '适用于农业生产、种植、养殖等用途',
        '低利率,快速审批',
        NOW(),
        NOW(),
        1
    ),
    (
        '农业生产贷款B',
        'SALE',
        '农村信用社',
        50000.00,
        6,
        3.85,
        '1个工作日',
        '小额快速贷款,适合短期资金周转',
        '超快放款,灵活还款',
        NOW(),
        NOW(),
        1
    ),
    (
        '农机购置贷款',
        'SALE',
        '工商银行',
        200000.00,
        24,
        4.75,
        '5个工作日',
        '专门用于购买农业机械设备',
        '长期贷款,专项用途',
        NOW(),
        NOW(),
        1
    );

SELECT * FROM loan_products;

-- ============================================
-- 说明：
-- 1. 数据库字符集使用 utf8mb4，支持中文和emoji
-- 2. 表使用 InnoDB 引擎，支持事务
-- 3. username 字段设置了唯一索引，防止重复用户名
-- 4. created_at 字段自动设置为当前时间
-- 5. 如果数据库已存在，使用 CREATE DATABASE IF NOT EXISTS 不会报错
-- 6. 如果表已存在，使用 CREATE TABLE IF NOT EXISTS 不会报错
-- ============================================