package com.caiwusys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.caiwusys.mapper")
public class CaiwusysApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaiwusysApplication.class, args);
    }
}
