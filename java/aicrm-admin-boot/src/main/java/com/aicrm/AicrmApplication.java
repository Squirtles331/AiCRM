package com.aicrm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 销售线索系统 - 核心业务服务启动类
 * <p>
 * Java 17 + Spring Boot 3.2
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.aicrm.module.**.mapper")
public class AicrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AicrmApplication.class, args);
    }
}
