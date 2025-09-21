package com.sep.rookieservice.config;

import com.sep.rookieservice.model.User;
import com.sep.rookieservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    CommandLineRunner testMongo(UserRepository repo) {
        return args -> {
            User user = new User();
            user.setFullName("Hehe");
            user.setRole("User");
            user.setEmail("hehe@example.com");

            repo.save(user);

            System.out.println("Inserted user: " + repo.findByEmail("hehe@example.com"));
        };
    }
}