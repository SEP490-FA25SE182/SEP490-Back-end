package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BookshelveRequestDTO;
import com.sep.rookieservice.dto.BookshelveResponseDTO;
import com.sep.rookieservice.entity.Bookshelve;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.BookshelveMapper;
import com.sep.rookieservice.repository.BookshelveRepository;
import com.sep.rookieservice.repository.UserRepository;
import com.sep.rookieservice.service.BookshelveService;
import com.sep.rookieservice.specification.BookshelveSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class BookshelveServiceImpl implements BookshelveService {

    private final BookshelveRepository repo;
    private final BookshelveMapper mapper;
    private final UserRepository userRepository;

    @Override
    public BookshelveResponseDTO create(BookshelveRequestDTO dto) {
        userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Bookshelve entity = mapper.toEntity(dto, null);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        Bookshelve saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookshelveResponseDTO getById(String id) {
        Bookshelve b = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bookshelve not found with id: " + id));
        return mapper.toDto(b);
    }

    @Override
    public BookshelveResponseDTO update(String id, BookshelveRequestDTO dto) {
        Bookshelve existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bookshelve not found with id: " + id));

        if (dto.getUserId() != null) {
            userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
        }

        Bookshelve updated = mapper.toEntity(dto, existing);
        updated.setUpdatedAt(Instant.now());

        return mapper.toDto(repo.save(updated));
    }

    @Override
    public void softDelete(String id) {
        Bookshelve existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bookshelve not found with id: " + id));

        existing.setIsActived(IsActived.INACTIVE);
        existing.setUpdatedAt(Instant.now());
        repo.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookshelveResponseDTO> search(String q, String userId, IsActived isActived, Pageable pageable) {
        Specification<Bookshelve> spec = BookshelveSpecification.buildSpecification(q, userId, isActived);
        Page<Bookshelve> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }
}
