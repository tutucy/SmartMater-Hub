CREATE DATABASE IF NOT EXISTS material_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE material_db;

DROP TABLE IF EXISTS `inventory_record`;
DROP TABLE IF EXISTS `purchase_order_item`;
DROP TABLE IF EXISTS `purchase_order`;
DROP TABLE IF EXISTS `inventory`;
DROP TABLE IF EXISTS `material`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `supplier`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `user` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role_id` INT(11) DEFAULT NULL COMMENT '角色ID',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `category` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` INT(11) DEFAULT 0 COMMENT '父分类ID',
    `level` INT(11) DEFAULT 1 COMMENT '分类层级',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '分类路径',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

CREATE TABLE `supplier` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
    `name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
    `contact` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

CREATE TABLE `material` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '物资ID',
    `code` VARCHAR(50) NOT NULL COMMENT '物资编码',
    `name` VARCHAR(100) NOT NULL COMMENT '物资名称',
    `category_id` INT(11) DEFAULT NULL COMMENT '分类ID',
    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT '参考价格',
    `supplier` VARCHAR(100) DEFAULT NULL COMMENT '供应商',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资信息表';

CREATE TABLE `inventory` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `material_id` INT(11) NOT NULL COMMENT '物资ID',
    `quantity` INT(11) DEFAULT 0 COMMENT '库存数量',
    `min_quantity` INT(11) DEFAULT 0 COMMENT '最小库存',
    `max_quantity` INT(11) DEFAULT 0 COMMENT '最大库存',
    `warehouse` VARCHAR(50) DEFAULT NULL COMMENT '仓库',
    `location` VARCHAR(50) DEFAULT NULL COMMENT '库位',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

CREATE TABLE `purchase_order` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '采购订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `supplier_id` INT(11) DEFAULT NULL COMMENT '供应商ID',
    `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总金额',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态：0-待审核，1-已审核，2-已入库，3-已取消',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator_id` INT(11) DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_supplier_id` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

CREATE TABLE `purchase_order_item` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
    `order_id` INT(11) NOT NULL COMMENT '订单ID',
    `material_id` INT(11) NOT NULL COMMENT '物资ID',
    `quantity` INT(11) NOT NULL COMMENT '数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';

CREATE TABLE `inventory_record` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `record_no` VARCHAR(50) NOT NULL COMMENT '记录编号',
    `material_id` INT(11) NOT NULL COMMENT '物资ID',
    `record_type` TINYINT(1) NOT NULL COMMENT '记录类型：1-入库，2-出库',
    `quantity` INT(11) NOT NULL COMMENT '数量',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    `operator_id` INT(11) DEFAULT NULL COMMENT '操作人ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_material_id` (`material_id`),
    KEY `idx_record_type` (`record_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水记录表';

INSERT INTO `role` (`id`, `name`, `description`, `status`) VALUES
(1, '超级管理员', '拥有系统所有权限', 1),
(2, '管理员', '拥有大部分管理权限', 1),
(3, '普通用户', '基本使用权限', 1);

INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES
(1, 'admin', 'admin123', '超级管理员', '13800138000', 'admin@example.com', 1, 1);

INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES
(1, '办公用品', 0, 1, '1', 1, 1),
(2, '电子设备', 0, 1, '2', 2, 1),
(3, '办公耗材', 1, 2, '1,3', 1, 1),
(4, '办公文具', 1, 2, '1,4', 2, 1);

INSERT INTO `supplier` (`id`, `name`, `contact`, `phone`, `address`, `email`, `status`, `remark`) VALUES
(1, '科技发展有限公司', '张三', '13800138001', '北京市朝阳区', 'zhangsan@example.com', 1, '主要供应商'),
(2, '办公用品有限公司', '李四', '13800138002', '上海市浦东新区', 'lisi@example.com', 1, '办公用品供应商');
