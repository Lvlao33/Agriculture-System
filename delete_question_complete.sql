-- ============================================
-- 完整删除问题的 SQL 脚本
-- 使用方法：在数据库工具的 SQL 编辑器中执行
-- ============================================

-- 步骤 1: 切换到正确的数据库
USE farmproject_db;

-- 步骤 2: 删除问题 ID = 1 的所有相关记录（按顺序执行）

-- 2.1 先删除附件记录
DELETE FROM question_attachments WHERE question_id = 1;

-- 2.2 删除标签记录
DELETE FROM question_tags WHERE question_id = 1;

-- 2.3 删除回答记录（这是关键！必须先删除）
DELETE FROM answers WHERE question_id = 1;

-- 2.4 最后删除问题记录
DELETE FROM questions WHERE id = 1;

-- ============================================
-- 如果上面的步骤仍然报错，使用这个（临时禁用外键检查）：
-- ============================================
-- USE farmproject_db;
-- SET FOREIGN_KEY_CHECKS = 0;
-- DELETE FROM question_attachments WHERE question_id = 1;
-- DELETE FROM question_tags WHERE question_id = 1;
-- DELETE FROM answers WHERE question_id = 1;
-- DELETE FROM questions WHERE id = 1;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 批量删除多个问题（例如删除 ID 为 1, 10, 11 的问题）：
-- ============================================
-- USE farmproject_db;
-- SET FOREIGN_KEY_CHECKS = 0;
-- DELETE FROM question_attachments WHERE question_id IN (1, 10, 11);
-- DELETE FROM question_tags WHERE question_id IN (1, 10, 11);
-- DELETE FROM answers WHERE question_id IN (1, 10, 11);
-- DELETE FROM questions WHERE id IN (1, 10, 11);
-- SET FOREIGN_KEY_CHECKS = 1;

