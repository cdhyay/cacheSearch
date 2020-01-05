package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ComdataApplication {
    public static void main(String[] args){
        SpringApplication.run(ComdataApplication.class,args);
    }
}
