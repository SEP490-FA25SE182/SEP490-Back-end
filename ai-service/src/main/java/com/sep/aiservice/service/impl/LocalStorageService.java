package com.sep.aiservice.service.impl;

import com.sep.aiservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    @Value("${storage.base-dir}") private String baseDir;
    @Value("${storage.public-base-url}") private String publicBaseUrl;

    @Override
    public String save(String fileName, byte[] bytes, String contentType) {
        try {
            Files.createDirectories(Path.of(baseDir));
            Path p = Path.of(baseDir, fileName);
            Files.write(p, bytes);
            return publicBaseUrl + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Save image failed", e);
        }
    }
}

