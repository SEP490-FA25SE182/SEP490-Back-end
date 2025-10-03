package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.BookshelveRequestDTO;
import com.sep.rookieservice.dto.BookshelveResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookshelveService {
    BookshelveResponseDTO create(BookshelveRequestDTO dto);
    BookshelveResponseDTO getById(String id);
    BookshelveResponseDTO update(String id, BookshelveRequestDTO dto);
    void softDelete(String id);
    Page<BookshelveResponseDTO> search(String q, String userId, IsActived isActived, Pageable pageable);
}