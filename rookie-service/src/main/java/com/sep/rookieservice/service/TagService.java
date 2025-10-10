package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.TagRequest;
import com.sep.rookieservice.dto.TagResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    List<TagResponse> getAll();
    TagResponse getById(String id);
    List<TagResponse> create(List<TagRequest> requests);
    TagResponse update(String id, TagRequest request);
    void softDelete(String id);
    Page<TagResponse> search(String name, IsActived isActived, Pageable pageable);
}
