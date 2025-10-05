package com.sep.aiservice.service;

import com.sep.aiservice.dto.AudioRequest;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AudioService {
    List<AudioResponse> getAll();
    AudioResponse getById(String id);
    AudioResponse getByTitle(String title);
    List<AudioResponse> create(List<AudioRequest> requests);
    AudioResponse update(String id, AudioRequest request);
    void softDelete(String id);
    Page<AudioResponse> search(String voice, String format, String language, String title,
                               IsActived isActived, Pageable pageable);
}
