package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.QuizPlayDTO;
import com.sep.rookieservice.dto.QuizRequestDTO;
import com.sep.rookieservice.dto.QuizResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuizService {
    QuizResponseDTO create(QuizRequestDTO dto);
    QuizResponseDTO update(String id, QuizRequestDTO dto);
    QuizResponseDTO getById(String id);
    void delete(String id);
    Page<QuizResponseDTO> search(String q, String chapterId, IsActived isActived, Pageable pageable);
    QuizPlayDTO getPlayData(String id);
}
