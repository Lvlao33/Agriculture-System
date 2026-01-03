-- ============================================
-- 为用户表添加手机号和邮箱字段
-- 如果表已经存在，执行此脚本添加新字段
-- ============================================

USE farmproject_db;

-- 注意：如果字段已存在，执行 ALTER TABLE 会报错
-- 执行前请先检查表结构：DESCRIBE users;

-- 添加手机号字段（如果不存在）
-- 如果字段已存在，请注释掉或删除对应的 ALTER TABLE 语句
ALTER TABLE `users` 
ADD COLUMN `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号' AFTER `avatar`;

-- 添加邮箱字段（如果不存在）
-- 如果字段已存在，请注释掉或删除对应的 ALTER TABLE 语句
ALTER TABLE `users` 
ADD COLUMN `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 查看更新后的表结构
DESCRIBE users;

