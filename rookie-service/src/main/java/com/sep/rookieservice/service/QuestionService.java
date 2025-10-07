package com.sep.rookieservice.service;
import com.sep.rookieservice.dto.QuestionRequestDTO;
import com.sep.rookieservice.dto.QuestionResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
    QuestionResponseDTO create(QuestionRequestDTO dto);
    QuestionResponseDTO update(String id, QuestionRequestDTO dto);
    QuestionResponseDTO getById(String id);
    void delete(String id);
    Page<QuestionResponseDTO> search(String keyword, String quizId, IsActived isActived, Pageable pageable);
}