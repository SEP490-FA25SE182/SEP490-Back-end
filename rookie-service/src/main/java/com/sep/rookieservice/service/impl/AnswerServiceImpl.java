package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.AnswerRequestDTO;
import com.sep.rookieservice.dto.AnswerResponseDTO;
import com.sep.rookieservice.entity.Answer;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.AnswerMapper;
import com.sep.rookieservice.repository.AnswerRepository;
import com.sep.rookieservice.service.AnswerService;
import com.sep.rookieservice.specification.AnswerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository repo;
    private final AnswerMapper mapper;

    @Override
    @Transactional
    public AnswerResponseDTO create(AnswerRequestDTO dto) {
        Answer entity = mapper.toEntity(dto);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        Answer saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public AnswerResponseDTO update(String id, AnswerRequestDTO dto) {
        Answer existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));

        mapper.updateEntityFromDto(dto, existing);
        Answer updated = repo.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponseDTO getById(String id) {
        Answer entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Answer entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
        repo.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnswerResponseDTO> search(String keyword, String questionId, Boolean isCorrect, IsActived isActived, Pageable pageable) {
        var spec = AnswerSpecification.buildSpecification(keyword, questionId, isCorrect, isActived);
        return repo.findAll(spec, pageable).map(mapper::toDto);
    }
}
