package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.ChapterRequestDTO;
import com.sep.rookieservice.dto.ChapterResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChapterService {
    ChapterResponseDTO create(ChapterRequestDTO dto);
    ChapterResponseDTO getById(String id);
    ChapterResponseDTO update(String id, ChapterRequestDTO dto);
    void softDelete(String id);

    Page<ChapterResponseDTO> search(
            String q,
            String bookId,
            Byte progressStatus,
            Byte publicationStatus,
            IsActived isActived,
            Pageable pageable
    );
}
