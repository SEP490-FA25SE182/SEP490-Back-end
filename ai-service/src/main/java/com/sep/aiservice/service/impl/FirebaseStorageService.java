package com.sep.aiservice.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sep.aiservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            return upload(fileName, in, contentType, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String upload(String fileName, InputStream inputStream, String contentType, long contentLength) throws IOException {
        // Chuẩn hóa key
        String objectName = fileName.replace("\\", "/");
        // Thêm token để lấy link public dạng tokenized (không cần ACL public)
        String downloadToken = UUID.randomUUID().toString();
        Map<String, String> meta = Map.of("firebaseStorageDownloadTokens", downloadToken);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, objectName))
                .setContentType(contentType)
                .setMetadata(meta)
                .build();

        storage.create(blobInfo, inputStream.readAllBytes());
        // https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{objectNameEncoded}?alt=media&token={downloadToken}
        String encoded = URLEncoder.encode(objectName, StandardCharsets.UTF_8);
        return String.format("%s/v0/b/%s/o/%s?alt=media&token=%s",
                publicBaseUrl, bucket, encoded, downloadToken);
    }
}

