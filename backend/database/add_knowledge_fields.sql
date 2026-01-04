-- 为knowledge表添加缺失的字段
-- 执行前请先检查字段是否已存在，如果已存在可以忽略错误

USE farmproject_db;

-- 添加图片路径字段（如果不存在）
-- 如果字段已存在，会报错，可以忽略
ALTER TABLE `knowledge` 
ADD COLUMN `pic_path` VARCHAR(500) DEFAULT NULL COMMENT '图片路径' AFTER `source`;

-- 添加URL字段（如果不存在）
-- 如果字段已存在，会报错，可以忽略
ALTER TABLE `knowledge` 
ADD COLUMN `url` VARCHAR(500) DEFAULT NULL COMMENT '链接URL' AFTER `pic_path`;

-- 验证字段是否添加成功（取消注释以执行）
-- SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_DEFAULT
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = 'farmproject_db' AND TABLE_NAME = 'knowledge'
-- ORDER BY ORDINAL_POSITION;

