-- 添加入库记录表
CREATE TABLE IF NOT EXISTS `inbound_record` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '入库记录ID',
    `inbound_no` VARCHAR(50) NOT NULL COMMENT '入库单号',
    `inbound_type` VARCHAR(20) NOT NULL COMMENT '入库类型：purchase-采购入库，production-生产入库，transfer-调拨入库',
    `supplier_id` INT(11) DEFAULT NULL COMMENT '供应商ID',
    `order_no` VARCHAR(50) DEFAULT NULL COMMENT '采购订单号',
    `total_quantity` INT(11) DEFAULT 0 COMMENT '总数量',
    `total_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '总金额',
    `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态：0-待审核，1-已完成',
    `creator_id` INT(11) DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inbound_no` (`inbound_no`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库记录表';

-- 添加入库明细表
CREATE TABLE IF NOT EXISTS `inbound_detail` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `inbound_id` INT(11) NOT NULL COMMENT '入库记录ID',
    `material_id` INT(11) NOT NULL COMMENT '物资ID',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物资名称',
    `specification` VARCHAR(200) DEFAULT NULL COMMENT '型号规格',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `quantity` INT(11) NOT NULL COMMENT '数量',
    `price` DECIMAL(18,2) NOT NULL COMMENT '单价',
    `amount` DECIMAL(18,2) NOT NULL COMMENT '金额',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_inbound_id` (`inbound_id`),
    KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细表';

-- 添加出库记录表
CREATE TABLE IF NOT EXISTS `outbound_record` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '出库记录ID',
    `outbound_no` VARCHAR(50) NOT NULL COMMENT '出库单号',
    `outbound_type` VARCHAR(20) NOT NULL COMMENT '出库类型：issue-领用出库，sale-销售出库',
    `recipient` VARCHAR(50) DEFAULT NULL COMMENT '领用人',
    `department` VARCHAR(50) DEFAULT NULL COMMENT '领用部门',
    `customer_info` VARCHAR(200) DEFAULT NULL COMMENT '客户信息',
    `purpose` VARCHAR(200) DEFAULT NULL COMMENT '用途',
    `total_quantity` INT(11) DEFAULT 0 COMMENT '总数量',
    `total_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '总金额',
    `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态：0-待审核，1-已完成',
    `creator_id` INT(11) DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_outbound_no` (`outbound_no`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库记录表';

-- 添加出库明细表
CREATE TABLE IF NOT EXISTS `outbound_detail` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `outbound_id` INT(11) NOT NULL COMMENT '出库记录ID',
    `material_id` INT(11) NOT NULL COMMENT '物资ID',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物资名称',
    `specification` VARCHAR(200) DEFAULT NULL COMMENT '型号规格',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `quantity` INT(11) NOT NULL COMMENT '数量',
    `price` DECIMAL(18,2) NOT NULL COMMENT '单价',
    `amount` DECIMAL(18,2) NOT NULL COMMENT '金额',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_outbound_id` (`outbound_id`),
    KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细表';

-- 添加操作日志表
CREATE TABLE IF NOT EXISTS `system_log` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-成功，0-失败',
    `error_msg` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 添加系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    `config_type` VARCHAR(20) DEFAULT 'string' COMMENT '配置类型：string-字符串，number-数字，boolean-布尔',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入初始系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('system.name', '智能物资管理系统', 'string', '系统名称'),
('system.logo', '/logo.png', 'string', '系统Logo'),
('inventory.warning.enabled', '1', 'boolean', '是否启用库存预警'),
('inventory.warning.threshold', '10', 'number', '库存预警阈值（百分比）'),
('order.auto.approve', '0', 'boolean', '是否自动审批订单');
