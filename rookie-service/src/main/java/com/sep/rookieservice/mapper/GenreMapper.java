package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.GenreRequestDTO;
import com.sep.rookieservice.dto.GenreResponseDTO;
import com.sep.rookieservice.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public Genre toEntity(GenreRequestDTO dto, Genre existing) {
        if (existing == null) existing = new Genre();
        existing.setGenreName(dto.getGenreName());
        existing.setDescription(dto.getDescription());
        return existing;
    }

    public GenreResponseDTO toDto(Genre entity) {
        GenreResponseDTO dto = new GenreResponseDTO();
        dto.setGenreId(entity.getGenreId());
        dto.setGenreName(entity.getGenreName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
