package org.example.controller;

import org.example.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@RestController
@RequestMapping("/api/init")
public class InitDataController {

    private final DataSource dataSource;

    public InitDataController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/test-data")
    public Result<String> initTestData() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            int totalRows = 0;
            
            String[] userSqls = {
                "INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES (2, 'manager', 'manager123', '李四', '13800138002', 'lisi@example.com', 2, 1)",
                "INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES (3, 'purchaser', 'purchaser123', '王五', '13800138003', 'wangwu@example.com', 3, 1)",
                "INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES (4, 'warehouse', 'warehouse123', '赵六', '13800138004', 'zhaoliu@example.com', 4, 1)",
                "INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES (5, 'user1', 'user1123', '钱七', '13800138005', 'qianqi@example.com', 5, 1)"
            };
            
            for (String sql : userSqls) {
                try {
                    int rows = stmt.executeUpdate(sql);
                    totalRows += rows;
                } catch (Exception e) {
                    System.out.println("SQL执行失败: " + sql);
                    System.out.println("错误信息: " + e.getMessage());
                }
            }
            
            String[] supplierSqls = {
                "INSERT INTO `supplier` (`id`, `name`, `contact`, `phone`, `address`, `email`, `status`, `remark`) VALUES (1, '北京科技有限公司', '张经理', '13800138011', '北京市海淀区中关村大街1号', 'bjtech@example.com', 1, '电子设备供应商')",
                "INSERT INTO `supplier` (`id`, `name`, `contact`, `phone`, `address`, `email`, `status`, `remark`) VALUES (2, '上海办公用品有限公司', '李主管', '13800138012', '上海市浦东新区张江高科技园区', 'shoffice@example.com', 1, '办公用品供应商')",
                "INSERT INTO `supplier` (`id`, `name`, `contact`, `phone`, `address`, `email`, `status`, `remark`) VALUES (3, '广州文具批发市场', '王老板', '13800138013', '广州市天河区天河路123号', 'gzstationery@example.com', 1, '文具批发商')",
                "INSERT INTO `supplier` (`id`, `name`, `contact`, `phone`, `address`, `email`, `status`, `remark`) VALUES (4, '深圳电子配件有限公司', '赵总监', '13800138014', '深圳市南山区科技园', 'szelectronics@example.com', 1, '电子配件供应商')",
                "INSERT INTO `supplier` (`id`, `name`, `contact`, `phone`, `address`, `email`, `status`, `remark`) VALUES (5, '杭州办公耗材有限公司', '钱经理', '13800138015', '杭州市西湖区文三路', 'hzconsumables@example.com', 1, '办公耗材供应商')"
            };
            
            for (String sql : supplierSqls) {
                try {
                    int rows = stmt.executeUpdate(sql);
                    totalRows += rows;
                } catch (Exception e) {
                    System.out.println("SQL执行失败: " + sql);
                    System.out.println("错误信息: " + e.getMessage());
                }
            }
            
            String[] categorySqls = {
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (1, '电子设备', 0, 1, '1', 1, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (2, '办公用品', 0, 1, '2', 2, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (3, '办公耗材', 0, 1, '3', 3, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (4, '电脑配件', 1, 2, '1,4', 1, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (5, '办公设备', 1, 2, '1,5', 2, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (6, '办公文具', 2, 2, '2,6', 1, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (7, '打印耗材', 3, 2, '3,7', 1, 1)",
                "INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `path`, `sort`, `status`) VALUES (8, '纸张用品', 3, 2, '3,8', 2, 1)"
            };
            
            for (String sql : categorySqls) {
                try {
                    int rows = stmt.executeUpdate(sql);
                    totalRows += rows;
                } catch (Exception e) {
                    System.out.println("SQL执行失败: " + sql);
                    System.out.println("错误信息: " + e.getMessage());
                }
            }
            
            String[] materialSqls = {
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (1, 'WL-001', '联想笔记本电脑', 1, 'ThinkPad X1 Carbon', '台', 8999.00, '北京科技有限公司', 1, '高性能商务笔记本')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (2, 'WL-002', '戴尔显示器', 1, 'Dell U2720Q 27英寸', '台', 2499.00, '北京科技有限公司', 1, '4K高清显示器')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (3, 'WL-003', '惠普打印机', 5, 'HP LaserJet Pro M404dn', '台', 1599.00, '上海办公用品有限公司', 1, '黑白激光打印机')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (4, 'WL-004', '佳能复印机', 5, 'Canon imageRUNNER 2525', '台', 8999.00, '上海办公用品有限公司', 1, '多功能复印机')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (5, 'WL-005', 'A4打印纸', 8, '70g 500张/包', '包', 25.00, '广州文具批发市场', 1, '优质打印纸')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (6, 'WL-006', '中性笔', 6, '黑色 0.5mm 12支/盒', '盒', 8.00, '广州文具批发市场', 1, '办公用笔')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (7, 'WL-007', '订书机', 6, '重型 50页', '个', 35.00, '广州文具批发市场', 1, '办公文具')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (8, 'WL-008', '文件夹', 6, 'A4 蓝色 50个/包', '包', 15.00, '广州文具批发市场', 1, '文件整理')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (9, 'WL-009', '墨盒', 7, 'HP 88A 黑色', '个', 150.00, '杭州办公耗材有限公司', 1, '打印机耗材')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (10, 'WL-010', '硒鼓', 7, 'HP 88A 黑色', '个', 280.00, '杭州办公耗材有限公司', 1, '打印机耗材')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (11, 'WL-011', '鼠标', 4, '罗技 M185 无线', '个', 59.00, '深圳电子配件有限公司', 1, '无线鼠标')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (12, 'WL-012', '键盘', 4, '罗技 K380 无线', '个', 129.00, '深圳电子配件有限公司', 1, '无线键盘')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (13, 'WL-013', 'U盘', 4, '金士顿 64GB USB3.0', '个', 45.00, '深圳电子配件有限公司', 1, '高速存储')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (14, 'WL-014', '移动硬盘', 4, '西部数据 1TB 移动硬盘', '个', 399.00, '深圳电子配件有限公司', 1, '大容量存储')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (15, 'WL-015', '路由器', 4, 'TP-Link AC1200 双频', '台', 199.00, '深圳电子配件有限公司', 1, '无线路由器')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (16, 'WL-016', '办公椅', 5, '人体工学 可调节', '把', 599.00, '上海办公用品有限公司', 1, '舒适办公椅')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (17, 'WL-017', '办公桌', 5, '1.2m x 0.6m', '张', 899.00, '上海办公用品有限公司', 1, '实木办公桌')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (18, 'WL-018', '文件柜', 5, '四门 钢制', '个', 699.00, '上海办公用品有限公司', 1, '文件存储')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (19, 'WL-019', '白板', 6, '1.2m x 0.9m 磁性', '块', 299.00, '广州文具批发市场', 1, '会议用白板')",
                "INSERT INTO `material` (`id`, `code`, `name`, `category_id`, `specification`, `unit`, `price`, `supplier`, `status`, `remark`) VALUES (20, 'WL-020', '投影仪', 5, '爱普生 EB-X450', '台', 3999.00, '北京科技有限公司', 1, '高清投影仪')"
            };
            
            for (String sql : materialSqls) {
                try {
                    int rows = stmt.executeUpdate(sql);
                    totalRows += rows;
                } catch (Exception e) {
                    System.out.println("SQL执行失败: " + sql);
                    System.out.println("错误信息: " + e.getMessage());
                }
            }
            
            String[] inventorySqls = {
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (1, 1, 15, 5, 50, '主仓库', 'A区-01')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (2, 2, 8, 3, 30, '主仓库', 'A区-02')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (3, 3, 5, 2, 20, '主仓库', 'B区-01')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (4, 4, 3, 1, 15, '主仓库', 'B区-02')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (5, 5, 200, 50, 500, '耗材仓库', 'C区-01')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (6, 6, 150, 30, 300, '耗材仓库', 'C区-02')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (7, 7, 25, 10, 50, '耗材仓库', 'C区-03')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (8, 8, 80, 20, 200, '耗材仓库', 'C区-04')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (9, 9, 40, 10, 100, '耗材仓库', 'D区-01')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (10, 10, 30, 10, 80, '耗材仓库', 'D区-02')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (11, 11, 60, 20, 100, '电子仓库', 'E区-01')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (12, 12, 45, 15, 80, '电子仓库', 'E区-02')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (13, 13, 100, 30, 200, '电子仓库', 'E区-03')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (14, 14, 20, 5, 50, '电子仓库', 'E区-04')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (15, 15, 35, 10, 70, '电子仓库', 'E区-05')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (16, 16, 10, 3, 30, '办公仓库', 'F区-01')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (17, 17, 8, 2, 25, '办公仓库', 'F区-02')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (18, 18, 12, 4, 40, '办公仓库', 'F区-03')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (19, 19, 5, 2, 20, '办公仓库', 'F区-04')",
                "INSERT INTO `inventory` (`id`, `material_id`, `quantity`, `min_quantity`, `max_quantity`, `warehouse`, `location`) VALUES (20, 20, 6, 2, 20, '办公仓库', 'F区-05')"
            };
            
            for (String sql : inventorySqls) {
                try {
                    int rows = stmt.executeUpdate(sql);
                    totalRows += rows;
                } catch (Exception e) {
                    System.out.println("SQL执行失败: " + sql);
                    System.out.println("错误信息: " + e.getMessage());
                }
            }
            
            return Result.success("成功插入 " + totalRows + " 条测试数据");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("初始化测试数据失败：" + e.getMessage());
        }
    }
}
