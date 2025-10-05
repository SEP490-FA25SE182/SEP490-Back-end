package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.IllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import com.sep.aiservice.entity.Illustration;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.mapper.IllustrationMapper;
import com.sep.aiservice.repository.IllustrationRepository;
import com.sep.aiservice.service.IllustrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IllustrationServiceImpl implements IllustrationService {

    private final IllustrationRepository illustrationRepository;
    @Qualifier("illustrationMapper")
    private final IllustrationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allIllustrations", key = "'all'")
    public List<IllustrationResponse> getAll() {
        return illustrationRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Illustration", key = "#id")
    public IllustrationResponse getById(String id) {
        Illustration i = illustrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Illustration not found: " + id));
        return mapper.toResponse(i);
    }

    @Override
    @Transactional(readOnly = true)
    public IllustrationResponse getByTitle(String title) {
        Illustration i = illustrationRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Illustration not found: " + title));
        return mapper.toResponse(i);
    }

    @Override
    @CacheEvict(value = {"allIllustrations", "Illustration"}, allEntries = true)
    public List<IllustrationResponse> create(List<IllustrationRequest> requests) {
        List<Illustration> entities = requests.stream().map(req -> {
            // Validate những field bắt buộc khi CREATE
            if (req.getImageUrl() == null || req.getImageUrl().isBlank())
                throw new IllegalArgumentException("imageUrl is required");
            if (req.getFormat() == null || req.getFormat().isBlank())
                throw new IllegalArgumentException("format is required");
            if (req.getWidth() == null || req.getWidth() < 1)
                throw new IllegalArgumentException("width must be >= 1");
            if (req.getHeight() == null || req.getHeight() < 1)
                throw new IllegalArgumentException("height must be >= 1");

            Illustration i = new Illustration();
            mapper.copyForCreate(req, i);

            if (i.getIsActived() == null) i.setIsActived(IsActived.ACTIVE);
            if (i.getCreatedAt() == null) i.setCreatedAt(Instant.now());
            i.setUpdatedAt(Instant.now());

            return i;
        }).toList();

        return illustrationRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allIllustrations", "Illustration"}, allEntries = true)
    public IllustrationResponse update(String id, IllustrationRequest request) {
        Illustration i = illustrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Illustration not found: " + id));

        mapper.copyForUpdate(request, i);
        i.setUpdatedAt(Instant.now());

        return mapper.toResponse(illustrationRepository.save(i));
    }

    @Override
    @CacheEvict(value = {"allIllustrations", "Illustration"}, allEntries = true)
    public void softDelete(String id) {
        Illustration i = illustrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Illustration not found: " + id));
        i.setIsActived(IsActived.INACTIVE);
        i.setUpdatedAt(Instant.now());
        illustrationRepository.save(i);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IllustrationResponse> search(String style, String format, String title,
                                             IsActived isActived, Pageable pageable) {
        String s = normalize(style);
        String f = normalize(format);
        String t = normalize(title);

        Illustration probe = new Illustration();
        if (s != null) probe.setStyle(s);
        if (f != null) probe.setFormat(f);
        if (t != null) probe.setTitle(t);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("style", m -> m.ignoreCase())
                .withMatcher("format", m -> m.ignoreCase())
                .withMatcher("title", m -> m.ignoreCase().contains())
                .withIgnorePaths(
                        "illustrationId", "imageUrl", "width", "height",
                        "createdAt", "updatedAt"
                )
                .withIgnoreNullValues();

        Example<Illustration> example = Example.of(probe, matcher);

        return illustrationRepository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}