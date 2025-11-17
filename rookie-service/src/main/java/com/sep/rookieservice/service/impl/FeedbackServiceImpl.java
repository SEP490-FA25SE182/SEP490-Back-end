package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.FeedbackRequestDTO;
import com.sep.rookieservice.dto.FeedbackResponseDTO;
import com.sep.rookieservice.entity.Feedback;
import com.sep.rookieservice.enums.FeedbackStatus;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.FeedbackMapper;
import com.sep.rookieservice.repository.FeedbackRepository;
import com.sep.rookieservice.service.FeedbackService;
import com.sep.rookieservice.specification.FeedbackSpecification;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository repo;
    private final FeedbackMapper mapper;

    @Override
    @Transactional
    public FeedbackResponseDTO create(FeedbackRequestDTO dto) {

        boolean exists = repo.existsByUserIdAndBookIdAndIsActived(
                dto.getUserId(),
                dto.getBookId(),
                IsActived.ACTIVE
        );

        if (exists) {
            throw new BadRequestException("User đã tạo feedback ACTIVE cho book này.");
        }

        Feedback entity = mapper.toNewEntity(dto);
        if (entity.getIsActived() == null) {
            entity.setIsActived(IsActived.ACTIVE);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(FeedbackStatus.PENDING);
        }

        entity.setIsActived(IsActived.ACTIVE);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        Feedback saved = repo.save(entity);
        return mapper.toDto(saved);
    }


    @Override
    @Transactional
    public FeedbackResponseDTO update(String id, FeedbackRequestDTO dto) {
        Feedback existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        if (!existing.getUserId().equals(dto.getUserId()) ||
                !existing.getBookId().equals(dto.getBookId())) {
            throw new BadRequestException("Không được thay đổi User hoặc Book của Feedback.");
        }

        mapper.updateEntityFromDto(dto, existing);

        existing.setUpdatedAt(Instant.now());

        Feedback updated = repo.save(existing);
        return mapper.toDto(updated);
    }


    @Override
    @Transactional(readOnly = true)
    public FeedbackResponseDTO getById(String id) {
        Feedback entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Feedback entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        entity.setIsActived(IsActived.INACTIVE);
        repo.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackResponseDTO> search(
            String q,
            String bookId,
            String userId,
            IsActived isActived,
            FeedbackStatus status,
            Pageable pageable
    ) {
        Specification<Feedback> spec = FeedbackSpecification.buildSpecification(q, bookId, userId, isActived, status);
        Page<Feedback> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    @Transactional
    public FeedbackResponseDTO updateStatus(String id, FeedbackStatus status) {
        Feedback entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        entity.setStatus(status);
        entity.setUpdatedAt(Instant.now());

        Feedback saved = repo.save(entity);
        return mapper.toDto(saved);
    }
}
