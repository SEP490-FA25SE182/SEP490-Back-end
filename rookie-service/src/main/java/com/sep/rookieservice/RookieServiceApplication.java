package com.sep.rookieservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RookieServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RookieServiceApplication.class, args);
    }

}
