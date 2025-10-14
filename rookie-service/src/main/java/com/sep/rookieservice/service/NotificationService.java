package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.NotificationRequestDTO;
import com.sep.rookieservice.dto.NotificationResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponseDTO create(NotificationRequestDTO dto);
    NotificationResponseDTO getById(String id);
    NotificationResponseDTO update(String id, NotificationRequestDTO dto);
    void softDelete(String id);
    Page<NotificationResponseDTO> search(String q, String userId, String bookId, String orderId, IsActived isActived, Pageable pageable);
}
