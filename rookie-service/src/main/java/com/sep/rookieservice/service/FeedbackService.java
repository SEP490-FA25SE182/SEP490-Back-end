package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.FeedbackRequestDTO;
import com.sep.rookieservice.dto.FeedbackResponseDTO;
import com.sep.rookieservice.enums.FeedbackStatus;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    FeedbackResponseDTO create(FeedbackRequestDTO dto);
    FeedbackResponseDTO update(String id, FeedbackRequestDTO dto);
    FeedbackResponseDTO getById(String id);
    void delete(String id);
    Page<FeedbackResponseDTO> search(String keyword, String bookId, String userId, IsActived isActived, FeedbackStatus status, Pageable pageable);
    FeedbackResponseDTO updateStatus(String id, FeedbackStatus status);
}
