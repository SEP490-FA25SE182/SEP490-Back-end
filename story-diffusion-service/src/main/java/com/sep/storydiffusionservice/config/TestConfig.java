package com.sep.storydiffusionservice.config;

import com.sep.storydiffusionservice.model.Illustration;
import com.sep.storydiffusionservice.repository.IllustrationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class TestConfig {
    @Bean
    CommandLineRunner testMongo(IllustrationRepository repo) {
        return args -> {
            Illustration ill = Illustration.builder()
                    .prompt("Haiz pretty")
                    .imageUrl("i dont know")
                    .style("anime")
                    .build();

            Illustration saved = repo.save(ill);
            System.out.println("Inserted illustration: " + saved);
        };
    }
}
