package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.ChapterRequestDTO;
import com.sep.rookieservice.dto.ChapterResponseDTO;
import com.sep.rookieservice.entity.Chapter;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.ChapterMapper;
import com.sep.rookieservice.repository.BookRepository;
import com.sep.rookieservice.repository.ChapterRepository;
import com.sep.rookieservice.service.ChapterService;
import com.sep.rookieservice.specification.ChapterSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository repo;
    private final ChapterMapper mapper;
    private final BookRepository bookRepository;

    @Override
    public ChapterResponseDTO create(ChapterRequestDTO dto) {
        bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + dto.getBookId()));

        Chapter entity = mapper.toNewEntity(dto);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        Chapter saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterResponseDTO getById(String id) {
        Chapter chapter = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));
        return mapper.toDto(chapter);
    }

    @Override
    public ChapterResponseDTO update(String id, ChapterRequestDTO dto) {
        Chapter existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));

        if (dto.getBookId() != null) {
            bookRepository.findById(dto.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + dto.getBookId()));
        }

        Chapter updated = mapper.toEntity(dto, existing);
        updated.setUpdatedAt(Instant.now());

        return mapper.toDto(repo.save(updated));
    }

    @Override
    public void softDelete(String id) {
        Chapter existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));

        existing.setIsActived(IsActived.INACTIVE);
        existing.setUpdatedAt(Instant.now());
        repo.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChapterResponseDTO> search(
            String q,
            String bookId,
            Byte progressStatus,
            Byte publicationStatus,
            IsActived isActived,
            Pageable pageable
    ) {
        Specification<Chapter> spec =
                ChapterSpecification.buildSpecification(q, bookId, progressStatus, publicationStatus, isActived);

        Page<Chapter> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }
}
