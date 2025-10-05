package com.sep.aiservice.service;

import com.sep.aiservice.dto.IllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import com.sep.aiservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IllustrationService {
    List<IllustrationResponse> getAll();
    IllustrationResponse getById(String id);
    IllustrationResponse getByTitle(String title);
    List<IllustrationResponse> create(List<IllustrationRequest> requests);
    IllustrationResponse update(String id, IllustrationRequest request);
    void softDelete(String id);
    Page<IllustrationResponse> search(String style, String format, String title, IsActived isActived, Pageable pageable);
}
