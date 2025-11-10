package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.PageIllustrationRequest;
import com.sep.aiservice.dto.PageIllustrationResponse;
import com.sep.aiservice.entity.PageIllustration;
import com.sep.aiservice.mapper.PageIllustrationMapper;
import com.sep.aiservice.repository.PageIllustrationRepository;
import com.sep.aiservice.service.PageIllustrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PageIllustrationServiceImpl implements PageIllustrationService {

    private final PageIllustrationRepository repository;
    @Qualifier("pageIllustrationMapper")
    private final PageIllustrationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PageIllustrationResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageIllustrationResponse getById(String id) {
        PageIllustration e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PageIllustration not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    public List<PageIllustrationResponse> create(List<PageIllustrationRequest> requests) {
        List<PageIllustration> entities = requests.stream().map(req -> {
            PageIllustration e = new PageIllustration();
            mapper.copyForCreate(req, e);
            return e;
        }).toList();

        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    public PageIllustrationResponse update(String id, PageIllustrationRequest request) {
        PageIllustration e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PageIllustration not found: " + id));
        mapper.copyForUpdate(request, e);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    public void deleteHard(String id) {
        PageIllustration e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PageIllustration not found: " + id));
        repository.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PageIllustrationResponse> search(String pageId, String illustrationId, Pageable pageable) {
        PageIllustration probe = new PageIllustration();
        if (pageId != null && !pageId.isBlank()) probe.setPageId(pageId.trim());
        if (illustrationId != null && !illustrationId.isBlank()) probe.setIllustrationId(illustrationId.trim());

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("pageId", m -> m.ignoreCase())
                .withMatcher("illustrationId", m -> m.ignoreCase())
                .withIgnorePaths("pageIllustrationId")
                .withIgnoreNullValues();

        return repository.findAll(Example.of(probe, matcher), pageable)
                .map(mapper::toResponse);
    }
}

