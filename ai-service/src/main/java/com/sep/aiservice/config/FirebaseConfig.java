package com.sep.aiservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final Environment env;

    @Bean
    @ConditionalOnProperty(value = "storage.provider", havingValue = "firebase")
    public FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions.Builder builder = FirebaseOptions.builder();

        String saJson = env.getProperty("firebase.service-account-json");
        String saFile = env.getProperty("firebase.service-account-file");

        if (StringUtils.hasText(saJson)) {
            try (InputStream is = new ByteArrayInputStream(saJson.getBytes(StandardCharsets.UTF_8))) {
                builder.setCredentials(GoogleCredentials.fromStream(is));
            }
        } else if (StringUtils.hasText(saFile)) {
            try (InputStream is = Files.newInputStream(Paths.get(saFile))) {
                builder.setCredentials(GoogleCredentials.fromStream(is));
            }
        } else {
            // fallback: sẽ dùng ADC (Application Default Credentials)
            builder.setCredentials(GoogleCredentials.getApplicationDefault());
        }

        return FirebaseApp.initializeApp(builder.build());
    }

    @Bean
    @ConditionalOnProperty(value = "storage.provider", havingValue = "firebase")
    public Storage gcsStorage(FirebaseApp app) {
        return StorageOptions.getDefaultInstance().getService();
    }
}

