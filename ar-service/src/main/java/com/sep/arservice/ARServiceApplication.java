package com.sep.arservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ARServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ARServiceApplication.class, args);
    }

}
