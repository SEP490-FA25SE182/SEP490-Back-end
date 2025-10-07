package com.sep.rookieservice.service.impl;
import com.sep.rookieservice.dto.QuestionRequestDTO;
import com.sep.rookieservice.dto.QuestionResponseDTO;
import com.sep.rookieservice.entity.Question;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.QuestionMapper;
import com.sep.rookieservice.repository.QuestionRepository;
import com.sep.rookieservice.service.QuestionService;
import com.sep.rookieservice.specification.QuestionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository repo;
    private final QuestionMapper mapper;

    @Override
    @Transactional
    public QuestionResponseDTO create(QuestionRequestDTO dto) {
        Question entity = mapper.toNewEntity(dto);
        Question saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public QuestionResponseDTO update(String id, QuestionRequestDTO dto) {
        Question existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        mapper.updateEntityFromDto(dto, existing);
        Question updated = repo.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getById(String id) {
        Question entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Question entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        repo.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> search(String keyword, String quizId, IsActived isActived, Pageable pageable) {
        var spec = QuestionSpecification.buildSpecification(keyword, quizId, isActived);
        return repo.findAll(spec, pageable).map(mapper::toDto);
    }
}