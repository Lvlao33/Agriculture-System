-- 修复knowledge表结构，使其与实体类匹配
-- 注意：此脚本会删除旧数据，请先备份！

USE farmproject_db;

-- 1. 备份现有数据（如果表中有重要数据，请先备份）
-- CREATE TABLE knowledge_backup AS SELECT * FROM knowledge;
-- 如果需要迁移数据，可以在这里添加迁移逻辑

-- 2. 删除关联表（先删除外键依赖）
DROP TABLE IF EXISTS `knowledge_tags`;
DROP TABLE IF EXISTS `knowledge_categories`;

-- 3. 删除主表
DROP TABLE IF EXISTS `knowledge`;

-- 4. 创建正确的knowledge表结构
CREATE TABLE `knowledge` (
    `knowledge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '知识ID',
    `title` VARCHAR(255) NOT NULL COMMENT '标题',
    `content` VARCHAR(5000) NOT NULL COMMENT '内容',
    `summary` VARCHAR(1000) DEFAULT NULL COMMENT '摘要',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID（专家）',
    `source` VARCHAR(255) DEFAULT NULL COMMENT '来源',
    `pic_path` VARCHAR(500) DEFAULT NULL COMMENT '图片路径',
    `url` VARCHAR(500) DEFAULT NULL COMMENT '链接URL',
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

-- 5. 创建知识分类关联表（ElementCollection）
CREATE TABLE `knowledge_categories` (
    `knowledge_id` BIGINT NOT NULL COMMENT '知识ID',
    `category` VARCHAR(255) NOT NULL COMMENT '分类',
    PRIMARY KEY (`knowledge_id`, `category`),
    CONSTRAINT `fk_knowledge_categories_knowledge` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge` (`knowledge_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识分类表';

-- 6. 创建知识标签关联表（ElementCollection）
CREATE TABLE `knowledge_tags` (
    `knowledge_id` BIGINT NOT NULL COMMENT '知识ID',
    `tag` VARCHAR(255) NOT NULL COMMENT '标签',
    PRIMARY KEY (`knowledge_id`, `tag`),
    CONSTRAINT `fk_knowledge_tags_knowledge` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge` (`knowledge_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识标签表';

-- 7. 验证表结构
DESCRIBE knowledge;
DESCRIBE knowledge_categories;
DESCRIBE knowledge_tags;

