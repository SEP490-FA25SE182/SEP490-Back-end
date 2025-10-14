package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.GenreRequestDTO;
import com.sep.rookieservice.dto.GenreResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenreService {
    GenreResponseDTO create(GenreRequestDTO dto);
    GenreResponseDTO update(String id, GenreRequestDTO dto);
    GenreResponseDTO getById(String id);
    void delete(String id);
    Page<GenreResponseDTO> search(String keyword, Pageable pageable);
}
