package com.caiwu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.caiwu.mapper")
public class HoutaiSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoutaiSysApplication.class, args);
    }
}
