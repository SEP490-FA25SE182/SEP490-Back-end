package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.NotificationRequestDTO;
import com.sep.rookieservice.dto.NotificationResponseDTO;
import com.sep.rookieservice.entity.Notification;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.NotificationMapper;
import com.sep.rookieservice.repository.NotificationRepository;
import com.sep.rookieservice.service.NotificationService;
import com.sep.rookieservice.specification.NotificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final NotificationMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationResponseDTO create(NotificationRequestDTO dto) {
        Notification entity = mapper.toEntity(dto);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setIsRead(false);

        Notification saved = repo.save(entity);
        NotificationResponseDTO response = mapper.toDto(saved);

        //Real-time WebSocket push
        messagingTemplate.convertAndSend("/topic/notifications/" + saved.getUserId(), response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getById(String id) {
        Notification found = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapper.toDto(found);
    }

    @Override
    public NotificationResponseDTO update(String id, NotificationRequestDTO dto) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        mapper.updateEntityFromDto(dto, existing);
        existing.setUpdatedAt(Instant.now());

        Notification saved = repo.save(existing);
        NotificationResponseDTO response = mapper.toDto(saved);

        //Optional: broadcast update event
        messagingTemplate.convertAndSend("/topic/notifications/" + saved.getUserId(), response);

        return response;
    }

    @Override
    public void softDelete(String id) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        existing.setIsActived(IsActived.INACTIVE);
        existing.setUpdatedAt(Instant.now());
        repo.save(existing);
    }

    //NEW: Mark as Read
    @Override
    public NotificationResponseDTO markAsRead(String id) {
        Notification notification = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        notification.setIsRead(true);
        notification.setUpdatedAt(Instant.now());

        Notification saved = repo.save(notification);
        NotificationResponseDTO response = mapper.toDto(saved);

        //Send real-time update to the user's notification topic
        messagingTemplate.convertAndSend("/topic/notifications/" + saved.getUserId(), response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> search(
            String q, String userId, String bookId, String orderId,
            IsActived isActived, Pageable pageable
    ) {
        Specification<Notification> spec = NotificationSpecification.build(q, userId, bookId, orderId, isActived);
        return repo.findAll(spec, pageable).map(mapper::toDto);
    }
}
