package com.sep.aiservice.service;

import com.sep.aiservice.dto.PageIllustrationRequest;
import com.sep.aiservice.dto.PageIllustrationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PageIllustrationService {
    List<PageIllustrationResponse> getAll();
    PageIllustrationResponse getById(String id);
    List<PageIllustrationResponse> create(List<PageIllustrationRequest> requests);
    PageIllustrationResponse update(String id, PageIllustrationRequest request);
    void deleteHard(String id);

    Page<PageIllustrationResponse> search(
            String pageId,
            String illustrationId,
            Pageable pageable
    );
}

