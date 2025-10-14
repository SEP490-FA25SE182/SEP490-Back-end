package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.GenreRequestDTO;
import com.sep.rookieservice.dto.GenreResponseDTO;
import com.sep.rookieservice.entity.Genre;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.GenreMapper;
import com.sep.rookieservice.repository.GenreRepository;
import com.sep.rookieservice.service.GenreService;
import com.sep.rookieservice.specification.GenreSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class GenreServiceImpl implements GenreService {

    private final GenreRepository repo;
    private final GenreMapper mapper;

    @Autowired
    public GenreServiceImpl(GenreRepository repo, GenreMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public GenreResponseDTO create(GenreRequestDTO dto) {
        Genre genre = mapper.toEntity(dto, null);
        genre.setCreatedAt(Instant.now());
        Genre saved = repo.save(genre);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDTO getById(String id) {
        Genre genre = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
        return mapper.toDto(genre);
    }

    @Override
    public GenreResponseDTO update(String id, GenreRequestDTO dto) {
        Genre existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
        Genre updated = mapper.toEntity(dto, existing);
        Genre saved = repo.save(updated);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        Genre existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
        repo.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreResponseDTO> search(String keyword, Pageable pageable) {
        Specification<Genre> spec = GenreSpecification.buildSpecification(keyword);
        Page<Genre> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }
}
