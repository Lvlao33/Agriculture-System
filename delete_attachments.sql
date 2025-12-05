-- ============================================
-- 删除 question_attachments 表中的记录
-- ============================================
-- 使用方法：
-- 1. 在数据库工具中打开 SQL 编辑器（通常在顶部菜单：Query -> New Query 或类似选项）
-- 2. 复制下面的 SQL 语句
-- 3. 粘贴到 SQL 编辑器中
-- 4. 修改 question_id 的值（例如改为 10）
-- 5. 点击执行按钮（通常是 ⚡ 图标或 F5 键）

-- 删除 question_id = 10 的附件记录
DELETE FROM question_attachments WHERE question_id = 10;

-- 如果上面报错，使用这个（临时禁用外键检查）
-- SET FOREIGN_KEY_CHECKS = 0;
-- DELETE FROM question_attachments WHERE question_id = 10;
-- SET FOREIGN_KEY_CHECKS = 1;

-- 删除后，可以删除问题记录
-- DELETE FROM questions WHERE id = 10;

