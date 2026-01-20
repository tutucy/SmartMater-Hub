-- 插入测试物资数据
INSERT INTO `material` (`code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES
('MAT001', 'A4打印纸', 2, '80g 500张/包', '包', 25.50, '得力办公', 1, '高品质A4打印纸，适合日常办公使用'),
('MAT002', '黑色中性笔', 2, '0.5mm', '支', 2.50, '晨光文具', 1, '经典黑色中性笔，书写流畅'),
('MAT003', '鼠标', 5, '有线 USB', '个', 89.00, '科技数码', 1, '人体工学设计，舒适耐用'),
('MAT004', '键盘', 5, '机械键盘 青轴', '个', 299.00, '科技数码', 1, '机械键盘，打字手感好'),
('MAT005', '文件夹', 2, 'A4 塑料材质', '个', 5.00, '齐心办公', 1, '耐用塑料文件夹，方便整理文档'),
('MAT006', '订书机', 2, '标准型', '个', 18.50, '得力办公', 1, '得力经典订书机，适合办公使用'),
('MAT007', '硒鼓', 3, 'HP LaserJet Pro M404n', '个', 499.00, '惠普耗材', 1, '原装硒鼓，打印质量稳定');

-- 插入库存数据
INSERT INTO `inventory` (`material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES
(1, 150, 50, 500, '主仓库', 'A区01架'),
(2, 300, 100, 1000, '主仓库', 'A区02架'),
(3, 20, 10, 100, '主仓库', 'B区01架'),
(4, 8, 5, 50, '主仓库', 'B区02架'),
(5, 450, 200, 1000, '主仓库', 'A区03架'),
(6, 35, 20, 100, '主仓库', 'A区04架'),
(7, 5, 3, 20, '主仓库', 'C区01架');

-- 插入入库记录
INSERT INTO `inbound_record` (`inbound_no`, `inbound_type`, `supplier_id`, `total_quantity`, `total_amount`, `operator`, `remark`, `status`, `creator_id`) VALUES
('RK20240101001', 'purchase', 1, 150, 3825.00, 'admin', '办公用品采购订单', 1, 1),
('RK20240102002', 'production', 4, 100, 5000.00, 'admin', '内部生产入库', 1, 1),
('RK20240103003', 'purchase', 2, 300, 750.00, 'admin', '文具采购订单', 1, 1),
('RK20240104004', 'transfer', 1, 50, 4450.00, 'admin', '仓库A调拨入库', 0, 1);

-- 插入入库明细
INSERT INTO `inbound_detail` (`inbound_id`, `material_id`, `material_name`, `specification`, `unit`, `quantity`, `price`, `amount`) VALUES
(1, 1, 'A4打印纸', '80g 500张/包', '包', 150, 25.50, 3825.00),
(2, 2, '黑色中性笔', '0.5mm', '支', 100, 2.50, 250.00),
(3, 3, '鼠标', '有线 USB', '个', 100, 50.00, 5000.00),
(4, 2, '黑色中性笔', '0.5mm', '支', 300, 2.50, 750.00),
(5, 1, 'A4打印纸', '80g 500张/包', '包', 50, 89.00, 4450.00);

-- 插入出库记录
INSERT INTO `outbound_record` (`outbound_no`, `outbound_type`, `recipient`, `department`, `purpose`, `total_quantity`, `total_amount`, `operator`, `remark`, `status`, `creator_id`) VALUES
('CK20240101001', 'issue', '张三', '行政部', '日常办公使用', 10, 255.00, 'admin', '', 1, 1),
('CK20240102002', 'issue', '李四', '技术部', '项目开发需要', 5, 445.00, 'admin', '', 1, 1),
('CK20240103003', 'sale', '外部客户', '销售部', '销售出库', 20, 510.00, 'admin', '客户A', 1, 1),
('CK20240104004', 'issue', '王五', '生产部', '生产使用', 30, 75.00, 'admin', '', 0, 1);

-- 插入出库明细
INSERT INTO `outbound_detail` (`outbound_id`, `material_id`, `material_name`, `specification`, `unit`, `quantity`, `price`, `amount`) VALUES
(1, 2, '黑色中性笔', '0.5mm', '支', 10, 2.50, 25.00),
(2, 3, '鼠标', '有线 USB', '个', 5, 89.00, 445.00),
(3, 1, 'A4打印纸', '80g 500张/包', '包', 20, 25.50, 510.00),
(4, 2, '黑色中性笔', '0.5mm', '支', 30, 2.50, 75.00);

-- 插入采购订单
INSERT INTO `purchase_order` (`order_no`, `supplier_id`, `total_amount`, `status`, `remark`, `creator_id`) VALUES
('PO20240101001', 1, 25500.00, 2, '办公用品采购订单', 1),
('PO20240102002', 3, 50000.00, 2, '技术部设备采购订单', 1),
('PO20240103003', 2, 7500.00, 2, '文具采购订单', 1),
('PO20240104004', 5, 49900.00, 0, '打印耗材采购订单', 1),
('PO20240105005', 6, 85000.00, 0, '办公家具采购订单', 1);

-- 插入采购订单明细
INSERT INTO `purchase_order_item` (`order_id`, `material_id`, `quantity`, `price`, `amount`) VALUES
(1, 1, 1000, 25.50, 25500.00),
(2, 3, 500, 100.00, 50000.00),
(3, 2, 300, 2.50, 750.00),
(4, 7, 100, 499.00, 49900.00),
(5, 8, 50, 1500.00, 75000.00);

-- 插入库存流水记录
INSERT INTO `inventory_record` (`record_no`, `material_id`, `record_type`, `quantity`, `price`, `operator_id`, `remark`) VALUES
('IN202401010001', 1, 1, 150, 25.50, 1, '采购入库'),
('IN202401010002', 2, 1, 300, 2.50, 1, '采购入库'),
('IN202401010003', 3, 1, 20, 89.00, 1, '采购入库'),
('OUT202401010001', 2, 2, 10, 2.50, 1, '领用出库'),
('OUT202401010002', 3, 2, 5, 89.00, 1, '领用出库'),
('OUT202401010003', 1, 2, 20, 25.50, 1, '销售出库'),
('OUT202401010004', 2, 2, 30, 2.50, 1, '领用出库');
