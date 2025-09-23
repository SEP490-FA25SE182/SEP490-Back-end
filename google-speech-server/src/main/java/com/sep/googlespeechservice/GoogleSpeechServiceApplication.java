package com.sep.googlespeechservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GoogleSpeechServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoogleSpeechServiceApplication.class, args);
    }

}
