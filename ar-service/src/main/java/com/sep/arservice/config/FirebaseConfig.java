package com.sep.arservice.config;

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
        String saFile = env.getProperty("firebase.service-account-file");

        // Cách 1: Dùng file JSON thật (khuyên dùng)
        if (StringUtils.hasText(saFile)) {
            return GoogleCredentials.fromStream(Files.newInputStream(Paths.get(saFile)));
        }

        // Cách 2: Fallback - đọc từ resources (nếu không có file)
        InputStream is = getClass().getClassLoader().getResourceAsStream("firebase-adminsdk.json");
        if (is != null) {
            return GoogleCredentials.fromStream(is);
        }

        // Cách 3: ADC
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
