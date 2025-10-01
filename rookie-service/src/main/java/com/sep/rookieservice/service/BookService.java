package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDTO create(BookRequestDTO dto);
    BookResponseDTO getById(String id);
    BookResponseDTO update(String id, BookRequestDTO dto);
    void softDelete(String id);
    Page<BookResponseDTO> search(
            String q,
            String authorId,
            Byte publicationStatus,
            Byte progressStatus,
            IsActived isActived,
            Pageable pageable
    );
}