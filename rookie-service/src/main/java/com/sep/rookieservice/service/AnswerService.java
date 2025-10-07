package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.AnswerRequestDTO;
import com.sep.rookieservice.dto.AnswerResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnswerService {
    AnswerResponseDTO create(AnswerRequestDTO dto);
    AnswerResponseDTO update(String id, AnswerRequestDTO dto);
    void delete(String id);
    AnswerResponseDTO getById(String id);
    Page<AnswerResponseDTO> search(String keyword, String questionId, Boolean isCorrect, IsActived isActived, Pageable pageable);
}
