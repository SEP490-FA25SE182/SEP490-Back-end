package com.sep.aiservice.service;

public interface StorageService {
    String save(String fileName, byte[] bytes, String contentType);
}

