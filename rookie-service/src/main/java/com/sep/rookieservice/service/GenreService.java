package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.GenreRequestDTO;
import com.sep.rookieservice.dto.GenreResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {
    GenreResponseDTO create(GenreRequestDTO dto);
    List<GenreResponseDTO> createAll(List<GenreRequestDTO> dtos);
    GenreResponseDTO update(String id, GenreRequestDTO dto);
    GenreResponseDTO getById(String id);
    void delete(String id);
    Page<GenreResponseDTO> search(String keyword, Pageable pageable);
}
