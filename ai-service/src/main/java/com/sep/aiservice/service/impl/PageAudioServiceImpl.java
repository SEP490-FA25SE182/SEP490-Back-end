package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.PageAudioRequest;
import com.sep.aiservice.dto.PageAudioResponse;
import com.sep.aiservice.entity.PageAudio;
import com.sep.aiservice.mapper.PageAudioMapper;
import com.sep.aiservice.repository.PageAudioRepository;
import com.sep.aiservice.service.PageAudioService;
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
public class PageAudioServiceImpl implements PageAudioService {

    private final PageAudioRepository repository;
    @Qualifier("pageAudioMapper")
    private final PageAudioMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PageAudioResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageAudioResponse getById(String id) {
        PageAudio e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PageAudio not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    public List<PageAudioResponse> create(List<PageAudioRequest> requests) {
        List<PageAudio> entities = requests.stream().map(req -> {
            PageAudio e = new PageAudio();
            mapper.copyForCreate(req, e);
            return e;
        }).toList();
        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    public PageAudioResponse update(String id, PageAudioRequest request) {
        PageAudio e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PageAudio not found: " + id));
        mapper.copyForUpdate(request, e);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    public void deleteHard(String id) {
        PageAudio e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PageAudio not found: " + id));
        repository.delete(e); // Xóa cứng
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PageAudioResponse> search(String pageId, String audioId, Pageable pageable) {
        PageAudio probe = new PageAudio();
        if (pageId != null && !pageId.isBlank()) probe.setPageId(pageId.trim());
        if (audioId != null && !audioId.isBlank()) probe.setAudioId(audioId.trim());

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("pageId",  m -> m.ignoreCase())
                .withMatcher("audioId", m -> m.ignoreCase())
                .withIgnorePaths("pageAudioId")
                .withIgnoreNullValues();

        return repository.findAll(Example.of(probe, matcher), pageable)
                .map(mapper::toResponse);
    }
}

