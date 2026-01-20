-- 添加分类描述字段
ALTER TABLE category ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '分类描述' AFTER parent_id;

-- 更新现有数据，添加一些描述
UPDATE category SET description = '各种电子设备及其配件' WHERE name = '电子设备';
UPDATE category SET description = '办公所需的电脑配件' WHERE name = '电脑配件';
UPDATE category SET description = '各种办公文具用品' WHERE name = '办公文具';
UPDATE category SET description = '打印机相关耗材' WHERE name = '打印耗材';
UPDATE category SET description = '各种办公用品' WHERE name = '办公用品';
UPDATE category SET description = '办公设备及其配件' WHERE name = '办公设备';
UPDATE category SET description = '各种办公纸张用品' WHERE name = '纸张用品';
UPDATE category SET description = '各种办公耗材' WHERE name = '办公耗材';