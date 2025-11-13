package com.sep.rookieservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String path = System.getenv("FIREBASE_CREDENTIALS_PATH");

            InputStream in;
            if (path != null && !path.isEmpty()) {
                System.out.println("Using Firebase credentials from: " + path);
                in = new FileInputStream(path); 
            } else {
                System.out.println("FIREBASE_CREDENTIALS_PATH not set, using classpath fallback");
                in = new ClassPathResource("firebase-service-account.json").getInputStream();
            }

            try (in) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println(" Firebase initialized successfully");
            }
        }
    }
}
