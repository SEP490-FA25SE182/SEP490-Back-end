package com.sep.arservice.service;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    /**
     * Lưu từ mảng byte (API của Meshy).
     */
    String save(String fileName, byte[] bytes, String contentType);

    /**
     * Upload từ InputStream.
     * @param fileName      đường dẫn đích
     * @param inputStream   dữ liệu nguồn
     * @param contentType   ví dụ "glb"
     * @param contentLength tổng số bytes (không biết có thể truyền -1)
     * @return public URL hoặc storage key
     */
    String upload(String fileName, InputStream inputStream, String contentType, long contentLength) throws IOException;

}
