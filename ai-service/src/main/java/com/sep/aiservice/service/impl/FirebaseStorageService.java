package com.sep.aiservice.service.impl;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sep.aiservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(value = "storage.provider", havingValue = "firebase")
@RequiredArgsConstructor
public class FirebaseStorageService implements StorageService {

    private final Storage storage;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.public-base-url:https://firebasestorage.googleapis.com}")
    private String publicBaseUrl;

    @Value("${storage.image-prefix:illustrations}")
    private String imgPrefix;

    @Value("${storage.audio-prefix:audios}")
    private String audioPrefix;

    @Override
    public String save(String fileName, byte[] bytes, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes is null or empty");
        }
        try (InputStream in = new java.io.ByteArrayInputStream(bytes)) {
            return upload(fileName, in, contentType, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to Firebase Storage", e);
        }
    }

    @Override
    public String upload(String fileName, InputStream inputStream, String contentType, long contentLength) throws IOException {
        // Chuẩn hoá object name: luôn dùng dấu '/'
        String objectName = fileName.replace('\\', '/');

        // Token để lấy link public theo dạng Firebase (không cần ACL publicRead)
        String downloadToken = UUID.randomUUID().toString();
        Map<String, String> meta = Map.of("firebaseStorageDownloadTokens", downloadToken);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, objectName))
                .setContentType(contentType)
                .setMetadata(meta)
                .build();

        // === Streaming upload, không giữ toàn bộ vào RAM ===
        try (WriteChannel writer = storage.writer(blobInfo);
             InputStream in = inputStream;
             OutputStream os = Channels.newOutputStream(writer)) {
            in.transferTo(os); // Java 9+: copy stream -> GCS
        }

        // Trả về URL public theo format Firebase:
        // https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{encodedPath}?alt=media&token={downloadToken}
        String encodedPath = URLEncoder.encode(objectName, StandardCharsets.UTF_8);
        return String.format("%s/v0/b/%s/o/%s?alt=media&token=%s",
                publicBaseUrl, bucket, encodedPath, downloadToken);
    }
}
