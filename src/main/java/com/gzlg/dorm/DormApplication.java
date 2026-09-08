package com.gzlg.dorm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生宿舍管理系统 后端启动类
 * 包结构调整为 com.gzlg.dorm（对齐开发设计文档第三章工程结构）。
 */
@SpringBootApplication
@MapperScan("com.gzlg.dorm.mapper")
public class DormApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormApplication.class, args);
    }

}