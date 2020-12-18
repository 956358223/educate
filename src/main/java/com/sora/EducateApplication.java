package com.sora;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
@MapperScan("com.sora.modules.*.mapper")
public class EducateApplication {

    public static void main(String[] args) {
        SpringApplication.run(EducateApplication.class, args);
    }

}
