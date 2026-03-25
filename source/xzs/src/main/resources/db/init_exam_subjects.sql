-- 初始化考试题库（Subject）
-- user_level: 1=驾驶员, 2=教员
-- subject_id: 1=驾驶员执照理论考试题库, 2=驾驶员执照实践考试题库, 3=教员执照理论考试题库

-- 请先检查是否已存在这些记录，避免重复插入
-- SELECT * FROM t_subject WHERE id IN (1, 2, 3);

INSERT INTO t_subject (id, name, level, level_name, item_order, deleted)
VALUES (1, '驾驶员执照理论考试题库', 1, '驾驶员', 1, false)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, level = EXCLUDED.level, level_name = EXCLUDED.level_name;

INSERT INTO t_subject (id, name, level, level_name, item_order, deleted)
VALUES (2, '驾驶员执照实践考试题库', 1, '驾驶员', 2, false)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, level = EXCLUDED.level, level_name = EXCLUDED.level_name;

INSERT INTO t_subject (id, name, level, level_name, item_order, deleted)
VALUES (3, '教员执照理论考试题库', 2, '教员', 3, false)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, level = EXCLUDED.level, level_name = EXCLUDED.level_name;

-- 设置默认用户为驾驶员身份 (user_level=1)
-- UPDATE t_user SET user_level = 1 WHERE role = 1 AND (user_level IS NULL OR user_level = 0);
