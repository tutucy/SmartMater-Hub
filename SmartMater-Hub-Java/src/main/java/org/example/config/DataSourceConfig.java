package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/material_db?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // 设置连接池属性
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(300000);
        dataSource.setConnectionTimeout(20000);
        dataSource.setMaxLifetime(1200000);
        
        // 设置连接初始化SQL，确保使用正确的字符集
        dataSource.setConnectionInitSql("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
        
        return dataSource;
    }
}