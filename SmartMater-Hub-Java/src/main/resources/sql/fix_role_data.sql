-- 清空角色表
DELETE FROM role;

-- 重新插入正确的中文数据（使用utf8mb4编码）
INSERT INTO `role` (`id`, `name`, `description`, `status`) VALUES
(1, '超级管理员', '拥有系统所有权限', 1),
(2, '管理员', '拥有大部分管理权限', 1),
(3, '普通用户', '基本使用权限', 1);
