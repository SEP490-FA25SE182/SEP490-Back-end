package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.BlogImageRequest;
import com.sep.rookieservice.dto.BlogImageResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogImageService {
    List<BlogImageResponse> getAll();
    BlogImageResponse getById(String id);
    List<BlogImageResponse> create(List<BlogImageRequest> requests);
    BlogImageResponse update(String id, BlogImageRequest request);
    void softDelete(String id);
    Page<BlogImageResponse> search(String blogId, String altText, IsActived isActived, Pageable pageable);
}
