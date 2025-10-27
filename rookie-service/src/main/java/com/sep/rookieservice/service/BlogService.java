package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.BlogRequest;
import com.sep.rookieservice.dto.BlogResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.UpdatedOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface BlogService {
    List<BlogResponse> getAll();
    BlogResponse getById(String id);
    List<BlogResponse> create(List<BlogRequest> requests);
    BlogResponse update(String id, BlogRequest request);
    void softDelete(String id);
    Page<BlogResponse> search(
            String title,
            String content,
            String authorId,
            String bookId,
            IsActived isActived,
            Set<String> tagNames,
            Pageable pageable
    );
    Page<BlogResponse> filterByUpdated(UpdatedOrder order, Pageable pageable);
}
