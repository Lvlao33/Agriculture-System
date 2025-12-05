-- 删除问题 ID = 10 及其所有相关记录
-- 在数据库工具的 SQL 编辑器中执行此脚本

-- 方法 1: 先切换到正确的数据库（推荐）
USE farmproject_db;

-- 然后执行删除
DELETE FROM question_attachments WHERE question_id = 10;
DELETE FROM question_tags WHERE question_id = 10;
DELETE FROM answers WHERE question_id = 10;
DELETE FROM questions WHERE id = 10;

-- ============================================
-- 方法 2: 使用完整的表名（包含数据库名）
-- 如果不想切换数据库，使用这个：
-- ============================================
-- DELETE FROM farmproject_db.question_attachments WHERE question_id = 10;
-- DELETE FROM farmproject_db.question_tags WHERE question_id = 10;
-- DELETE FROM farmproject_db.answers WHERE question_id = 10;
-- DELETE FROM farmproject_db.questions WHERE id = 10;

-- ============================================
-- 如果上面的步骤报错，使用这个（临时禁用外键检查）：
-- ============================================
-- USE farmproject_db;
-- SET FOREIGN_KEY_CHECKS = 0;
-- DELETE FROM question_attachments WHERE question_id = 10;
-- DELETE FROM question_tags WHERE question_id = 10;
-- DELETE FROM answers WHERE question_id = 10;
-- DELETE FROM questions WHERE id = 10;
-- SET FOREIGN_KEY_CHECKS = 1;

