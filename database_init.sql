-- ============================================
-- 农业系统数据库初始化脚本
-- 数据库名称: farmproject_db
-- MySQL版本: 8.0+
-- ============================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `farmproject_db` 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 使用数据库
USE `farmproject_db`;

-- 3. 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名，唯一',
    `password_hash` VARCHAR(128) NOT NULL COMMENT '密码哈希值（MD5加密）',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(256) DEFAULT NULL COMMENT '头像路径',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
    KEY `idx_username` (`username`) COMMENT '用户名索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 4. 插入测试数据（可选）
-- 测试用户：用户名 test，密码 123456（MD5: e10adc3949ba59abbe56e057f20f883e）
INSERT INTO `users` (`username`, `password_hash`, `nickname`, `avatar`, `created_at`) 
VALUES 
    ('test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 'default_avatar.png', NOW())
ON DUPLICATE KEY UPDATE `username`=`username`;

-- ============================================
-- 说明：
-- 1. 数据库字符集使用 utf8mb4，支持中文和emoji
-- 2. 表使用 InnoDB 引擎，支持事务
-- 3. username 字段设置了唯一索引，防止重复用户名
-- 4. created_at 字段自动设置为当前时间
-- 5. 如果数据库已存在，使用 CREATE DATABASE IF NOT EXISTS 不会报错
-- 6. 如果表已存在，使用 CREATE TABLE IF NOT EXISTS 不会报错
-- ============================================

