package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.QuizRequestDTO;
import com.sep.rookieservice.dto.QuizResponseDTO;
import com.sep.rookieservice.entity.Quiz;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.QuizMapper;
import com.sep.rookieservice.repository.QuizRepository;
import com.sep.rookieservice.service.QuizService;
import com.sep.rookieservice.specification.QuizSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository repo;
    private final QuizMapper mapper;

    @Override
    @Transactional
    public QuizResponseDTO create(QuizRequestDTO dto) {
        Quiz entity = mapper.toNewEntity(dto);
        Quiz saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public QuizResponseDTO update(String id, QuizRequestDTO dto) {
        Quiz existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        mapper.updateEntityFromDto(dto, existing);
        Quiz updated = repo.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getById(String id) {
        Quiz entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Quiz entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        repo.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizResponseDTO> search(String q, String chapterId, IsActived isActived, Pageable pageable) {
        var spec = QuizSpecification.buildSpecification(q, chapterId, isActived);
        return repo.findAll(spec, pageable).map(mapper::toDto);
    }
}