package com.sep.aiservice.service;

import com.sep.aiservice.dto.PageAudioRequest;
import com.sep.aiservice.dto.PageAudioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PageAudioService {
    List<PageAudioResponse> getAll();
    PageAudioResponse getById(String id);
    List<PageAudioResponse> create(List<PageAudioRequest> requests);
    PageAudioResponse update(String id, PageAudioRequest request);
    void deleteHard(String id);

    Page<PageAudioResponse> search(
            String pageId,
            String audioId,
            Pageable pageable
    );
}

