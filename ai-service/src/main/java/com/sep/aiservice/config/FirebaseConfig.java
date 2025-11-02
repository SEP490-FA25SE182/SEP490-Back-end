package com.sep.aiservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final Environment env;

    @Value("${storage.bucket:}")
    private String storageBucket;

    @Value("${gcp.project-id:}")
    private String gcpProjectId;

    // 1) Bean credentials — dùng chung cho FirebaseApp & Storage
    @Bean
    @ConditionalOnProperty(value = "storage.provider", havingValue = "firebase")
    public GoogleCredentials firebaseCredentials() throws Exception {
        String saJson = env.getProperty("firebase.service-account-json");
        String saFile = env.getProperty("firebase.service-account-file");

        if (StringUtils.hasText(saJson)) {
            try (InputStream is = new ByteArrayInputStream(saJson.getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(is);
            }
        }
        if (StringUtils.hasText(saFile)) {
            try (InputStream is = Files.newInputStream(Paths.get(saFile))) {
                return GoogleCredentials.fromStream(is);
            }
        }
        // Fallback: ADC
        return GoogleCredentials.getApplicationDefault();
    }

    // 2) FirebaseApp dùng credentials bean
    @Bean
    @ConditionalOnProperty(value = "storage.provider", havingValue = "firebase")
    public FirebaseApp firebaseApp(GoogleCredentials creds) throws Exception {
        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(creds);

        if (StringUtils.hasText(storageBucket)) {
            builder.setStorageBucket(storageBucket);
        }
        if (StringUtils.hasText(gcpProjectId)) {
            builder.setProjectId(gcpProjectId);
        } else if (creds instanceof ServiceAccountCredentials sac && sac.getProjectId() != null) {
            builder.setProjectId(sac.getProjectId());
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        return FirebaseApp.initializeApp(builder.build());
    }

    // 3) Storage cũng dùng đúng credentials bean (không lấy từ FirebaseOptions)
    @Bean
    @ConditionalOnProperty(value = "storage.provider", havingValue = "firebase")
    public Storage gcsStorage(GoogleCredentials creds) {
        StorageOptions.Builder sb = StorageOptions.newBuilder().setCredentials(creds);

        // Ưu tiên projectId cấu hình; nếu không có, lấy từ service account nếu có
        if (StringUtils.hasText(gcpProjectId)) {
            sb.setProjectId(gcpProjectId);
        } else if (creds instanceof ServiceAccountCredentials sac && sac.getProjectId() != null) {
            sb.setProjectId(sac.getProjectId());
        }
        return sb.build().getService();
    }
}
