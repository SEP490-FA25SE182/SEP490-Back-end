package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.PageRequestDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.entity.Page;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.PageMapper;
import com.sep.rookieservice.repository.ChapterRepository;
import com.sep.rookieservice.repository.PageRepository;
import com.sep.rookieservice.service.PageService;
import com.sep.rookieservice.specification.PageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class PageServiceImpl implements PageService {

    private final PageRepository repo;
    private final PageMapper mapper;
    private final ChapterRepository chapterRepository;

    @Override
    public PageResponseDTO create(PageRequestDTO dto) {
        chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + dto.getChapterId()));

        Page entity = mapper.toEntity(dto);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        Page saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO getById(String id) {
        Page page = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + id));
        return mapper.toDto(page);
    }

    @Override
    public PageResponseDTO update(String id, PageRequestDTO dto) {
        Page existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + id));

        if (dto.getChapterId() != null) {
            chapterRepository.findById(dto.getChapterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + dto.getChapterId()));
        }

        Page updated = mapper.toEntity(dto, existing);
        updated.setUpdatedAt(Instant.now());

        return mapper.toDto(repo.save(updated));
    }

    @Override
    public void softDelete(String id) {
        Page existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + id));

        existing.setIsActived(IsActived.INACTIVE);
        existing.setUpdatedAt(Instant.now());

        repo.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<PageResponseDTO> search(
            String q,
            String chapterId,
            IsActived isActived,
            Pageable pageable
    ) {
        Specification<Page> spec = PageSpecification.buildSpecification(q, chapterId, isActived);
        org.springframework.data.domain.Page<Page> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }
}
