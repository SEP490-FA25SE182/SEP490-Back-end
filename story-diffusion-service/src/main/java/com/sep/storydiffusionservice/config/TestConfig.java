package com.sep.storydiffusionservice.config;

import com.sep.storydiffusionservice.model.Illustration;
import com.sep.storydiffusionservice.repository.IllustrationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    CommandLineRunner testMongo(IllustrationRepository repo) {
        return args -> {
            Illustration illustration = new Illustration();
            illustration.setPrompt("Alice pretty");
            illustration.setImageUrl("i dont know");
            illustration.setStyle("anime");

            repo.save(illustration);

            System.out.println("Inserted illustration: " + repo.findById(""));
        };
    }
}
