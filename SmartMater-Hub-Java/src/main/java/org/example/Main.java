package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 标记为SpringBoot启动类
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // 启动SpringBoot项目
        SpringApplication.run(Main.class, args);
    }
}