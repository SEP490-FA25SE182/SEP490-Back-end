package com.sep.aiservice.service;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    /**
     * Lưu từ mảng byte (API của Illustration).
     */
    String save(String fileName, byte[] bytes, String contentType);

    /**
     * Upload từ InputStream.
     * @param fileName      đường dẫn đích (ví dụ "audios/xxx.wav")
     * @param inputStream   dữ liệu nguồn
     * @param contentType   ví dụ "audio/wav"
     * @param contentLength tổng số bytes (không biết có thể truyền -1)
     * @return public URL hoặc storage key
     */
    String upload(String fileName, InputStream inputStream, String contentType, long contentLength) throws IOException;

}


