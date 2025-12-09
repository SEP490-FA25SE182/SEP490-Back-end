package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.ChapterRequestDTO;
import com.sep.rookieservice.dto.ChapterResponseDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.entity.Chapter;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.ChapterMapper;
import com.sep.rookieservice.mapper.PageMapper;
import com.sep.rookieservice.repository.BookRepository;
import com.sep.rookieservice.repository.ChapterRepository;
import com.sep.rookieservice.repository.PageRepository;
import com.sep.rookieservice.service.ChapterService;
import com.sep.rookieservice.specification.ChapterSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sep.rookieservice.entity.Page;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository repo;
    private final ChapterMapper mapper;
    private final BookRepository bookRepository;
    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

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
    public org.springframework.data.domain.Page<ChapterResponseDTO> search(
            String q,
            String bookId,
            Byte progressStatus,
            IsActived isActived,
            Pageable pageable
    ) {
        Specification<Chapter> spec =
                ChapterSpecification.buildSpecification(q, bookId, progressStatus, isActived);

        org.springframework.data.domain.Page<Chapter> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }


    @Override
    public List<PageResponseDTO> getPagesByChapterId(String chapterId) {
        repo.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found: " + chapterId));

        List<com.sep.rookieservice.entity.Page> pages =
                pageRepository.findByChapterIdOrderByPageNumberAsc(chapterId);

        return pages.stream()
                .map(pageMapper::toDto)
                .toList();
    }

}
