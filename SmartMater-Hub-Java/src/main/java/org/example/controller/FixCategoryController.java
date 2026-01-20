package org.example.controller;

import org.example.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@RestController
@RequestMapping("/api/fix")
public class FixCategoryController {

    private final DataSource dataSource;

    public FixCategoryController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/add-category-description")
    public Result<String> addCategoryDescription() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 检查description字段是否存在
            try {
                stmt.execute("SELECT description FROM category LIMIT 1");
                return Result.success("description字段已存在");
            } catch (Exception e) {
                // 字段不存在，添加字段
                stmt.execute("ALTER TABLE category ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '分类描述' AFTER parent_id");
                
                // 更新现有数据，添加一些描述
                String[] updates = {
                    "UPDATE category SET description = '各种电子设备及其配件' WHERE name = '电子设备'",
                    "UPDATE category SET description = '办公所需的电脑配件' WHERE name = '电脑配件'",
                    "UPDATE category SET description = '各种办公文具用品' WHERE name = '办公文具'",
                    "UPDATE category SET description = '打印机相关耗材' WHERE name = '打印耗材'",
                    "UPDATE category SET description = '各种办公用品' WHERE name = '办公用品'",
                    "UPDATE category SET description = '办公设备及其配件' WHERE name = '办公设备'",
                    "UPDATE category SET description = '各种办公纸张用品' WHERE name = '纸张用品'",
                    "UPDATE category SET description = '各种办公耗材' WHERE name = '办公耗材'"
                };
                
                for (String sql : updates) {
                    try {
                        stmt.executeUpdate(sql);
                    } catch (Exception ex) {
                        System.out.println("更新描述失败: " + sql);
                    }
                }
                
                return Result.success("成功添加description字段并更新数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加description字段失败：" + e.getMessage());
        }
    }
}