package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.FeedbackRequestDTO;
import com.sep.rookieservice.dto.FeedbackResponseDTO;
import com.sep.rookieservice.entity.Feedback;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.FeedbackMapper;
import com.sep.rookieservice.repository.FeedbackRepository;
import com.sep.rookieservice.service.FeedbackService;
import com.sep.rookieservice.specification.FeedbackSpecification;
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
        Feedback entity = mapper.toNewEntity(dto);
        if (entity.getIsActived() == null) {
            entity.setIsActived(IsActived.ACTIVE);
        }
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
            Pageable pageable
    ) {
        Specification<Feedback> spec = FeedbackSpecification.buildSpecification(q, bookId, userId, isActived);
        Page<Feedback> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }

}
