package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.*;
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

import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public QuizPlayDTO getPlayData(String id) {
        Quiz quiz = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        QuizPlayDTO dto = new QuizPlayDTO();
        dto.setQuizId(quiz.getQuizId());
        dto.setTitle(quiz.getTitle());
        dto.setTotalScore(quiz.getTotalScore());
        dto.setQuestionCount(quiz.getQuestionCount());

        var questionList = quiz.getQuestions(); // dựa vào @OneToMany
        if (questionList != null) {
            dto.setQuestions(
                    questionList.stream().map(q -> {
                        QuestionPlayDTO qdto = new QuestionPlayDTO();
                        qdto.setQuestionId(q.getQuestionId());
                        qdto.setContent(q.getContent());
                        qdto.setScore(q.getScore());
                        qdto.setAnswerCount(q.getAnswerCount());

                        var answers = q.getAnswers();
                        if (answers != null) {
                            qdto.setAnswers(
                                    answers.stream().map(a -> {
                                        AnswerPlayDTO adto = new AnswerPlayDTO();
                                        adto.setAnswerId(a.getAnswerId());
                                        adto.setContent(a.getContent());
                                        adto.setIsCorrect(a.getIsCorrect());
                                        return adto;
                                    }).collect(Collectors.toList())
                            );
                        }
                        return qdto;
                    }).collect(Collectors.toList())
            );
        }

        return dto;
    }

}