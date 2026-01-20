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
public class FixDataController {

    private final DataSource dataSource;

    public FixDataController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/role-data")
    public Result<String> fixRoleData() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("DELETE FROM role");
            
            String[] sqls = {
                "INSERT INTO `role` (`id`, `name`, `description`, `status`) VALUES (1, '超级管理员', '拥有系统所有权限', 1)",
                "INSERT INTO `role` (`id`, `name`, `description`, `status`) VALUES (2, '管理员', '拥有大部分管理权限', 1)",
                "INSERT INTO `role` (`id`, `name`, `description`, `status`) VALUES (3, '普通用户', '基本使用权限', 1)"
            };
            
            int totalRows = 0;
            for (String sql : sqls) {
                int rows = stmt.executeUpdate(sql);
                totalRows += rows;
            }
            
            return Result.success("成功修复 " + totalRows + " 条角色数据");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修复失败：" + e.getMessage());
        }
    }
}