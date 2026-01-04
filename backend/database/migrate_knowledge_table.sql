-- 迁移knowledge表结构，使其与实体类匹配
-- 此脚本会保留现有数据，只修改表结构

USE farmproject_db;

-- ============================================
-- 步骤1: 检查并删除comment表的外键约束
-- ============================================
-- 查看comment表的外键约束
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'farmproject_db' 
  AND TABLE_NAME = 'comment' 
  AND REFERENCED_TABLE_NAME = 'knowledge';

-- 删除comment表的外键约束（如果存在）
SET @constraint_name = (
    SELECT CONSTRAINT_NAME 
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'farmproject_db' 
      AND TABLE_NAME = 'comment' 
      AND REFERENCED_TABLE_NAME = 'knowledge'
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL, 
    CONCAT('ALTER TABLE `comment` DROP FOREIGN KEY `', @constraint_name, '`'),
    'SELECT "No foreign key constraint found" AS message');
    
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 步骤2: 重命名主键字段从 id 到 knowledge_id
-- ============================================
-- 先删除主键约束
ALTER TABLE `knowledge` DROP PRIMARY KEY;

-- 重命名字段
ALTER TABLE `knowledge` 
CHANGE COLUMN `id` `knowledge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '知识ID';

-- 重新添加主键
ALTER TABLE `knowledge` 
ADD PRIMARY KEY (`knowledge_id`);

-- ============================================
-- 步骤3: 删除不需要的旧字段
-- ============================================
-- 删除旧的category字段（单个值，将被knowledge_categories表替代）
ALTER TABLE `knowledge` DROP COLUMN IF EXISTS `category`;

-- 删除旧的author字段（字符串，将被author_id外键替代）
ALTER TABLE `knowledge` DROP COLUMN IF EXISTS `author`;

-- 删除旧的created_time和updated_time字段（使用create_time和update_time）
ALTER TABLE `knowledge` DROP COLUMN IF EXISTS `created_time`;
ALTER TABLE `knowledge` DROP COLUMN IF EXISTS `updated_time`;

-- ============================================
-- 步骤4: 创建关联表（如果不存在）
-- ============================================
-- 创建知识分类关联表
CREATE TABLE IF NOT EXISTS `knowledge_categories` (
    `knowledge_id` BIGINT NOT NULL COMMENT '知识ID',
    `category` VARCHAR(255) NOT NULL COMMENT '分类',
    PRIMARY KEY (`knowledge_id`, `category`),
    CONSTRAINT `fk_knowledge_categories_knowledge` 
        FOREIGN KEY (`knowledge_id`) 
        REFERENCES `knowledge` (`knowledge_id`) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识分类表';

-- 创建知识标签关联表
CREATE TABLE IF NOT EXISTS `knowledge_tags` (
    `knowledge_id` BIGINT NOT NULL COMMENT '知识ID',
    `tag` VARCHAR(255) NOT NULL COMMENT '标签',
    PRIMARY KEY (`knowledge_id`, `tag`),
    CONSTRAINT `fk_knowledge_tags_knowledge` 
        FOREIGN KEY (`knowledge_id`) 
        REFERENCES `knowledge` (`knowledge_id`) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识标签表';

-- ============================================
-- 步骤5: 恢复comment表的外键约束（如果需要）
-- ============================================
-- 检查comment表是否有knowledge_id字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'farmproject_db' 
  AND TABLE_NAME = 'comment' 
  AND COLUMN_NAME LIKE '%knowledge%';

-- 如果comment表有knowledge_id字段，重新添加外键约束
-- ALTER TABLE `comment` 
-- ADD CONSTRAINT `fk_comment_knowledge` 
-- FOREIGN KEY (`knowledge_id`) 
-- REFERENCES `knowledge` (`knowledge_id`) 
-- ON DELETE CASCADE;

-- ============================================
-- 步骤6: 验证表结构
-- ============================================
DESCRIBE knowledge;
DESCRIBE knowledge_categories;
DESCRIBE knowledge_tags;

-- 查看knowledge表的所有字段
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_KEY
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'farmproject_db' 
  AND TABLE_NAME = 'knowledge'
ORDER BY ORDINAL_POSITION;

