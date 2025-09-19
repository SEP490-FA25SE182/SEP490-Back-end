package com.sep.storydiffusionservice.config;

import com.sep.storydiffusionservice.model.Illustration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoConfig {
    @Bean
    CommandLineRunner ensureIndexes(MongoTemplate mongoTemplate) {
        return args -> {
            mongoTemplate.indexOps(Illustration.class)
                    .ensureIndex(new Index().on("email", org.springframework.data.domain.Sort.Direction.ASC).unique());
        };
    }
}
