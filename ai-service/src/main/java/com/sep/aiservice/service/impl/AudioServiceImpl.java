package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.AudioRequest;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.entity.Audio;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.mapper.AudioMapper;
import com.sep.aiservice.repository.AudioRepository;
import com.sep.aiservice.service.AudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class AudioServiceImpl implements AudioService {

    private final AudioRepository audioRepository;
    @Qualifier("audioMapper")
    private final AudioMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allAudios", key = "'all'")
    public List<AudioResponse> getAll() {
        return audioRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Audio", key = "#id")
    public AudioResponse getById(String id) {
        Audio a = audioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio not found: " + id));
        return mapper.toResponse(a);
    }

    @Override
    @Transactional(readOnly = true)
    public AudioResponse getByTitle(String title) {
        Audio a = audioRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Audio not found: " + title));
        return mapper.toResponse(a);
    }

    @Override
    @CacheEvict(value = {"allAudios", "Audio"}, allEntries = true)
    public List<AudioResponse> create(List<AudioRequest> requests) {
        List<Audio> entities = requests.stream().map(req -> {
            // Validate bắt buộc khi CREATE
            if (req.getAudioUrl() == null || req.getAudioUrl().isBlank())
                throw new IllegalArgumentException("audioUrl is required");
            if (req.getFormat() == null || req.getFormat().isBlank())
                throw new IllegalArgumentException("format is required");
            if (req.getLanguage() == null || req.getLanguage().isBlank())
                throw new IllegalArgumentException("language is required");
            if (req.getDurationMs() == null || req.getDurationMs() <= 0)
                throw new IllegalArgumentException("durationMs must be > 0");

            Audio a = new Audio();
            mapper.copyForCreate(req, a);

            if (a.getIsActived() == null) a.setIsActived(IsActived.ACTIVE);
            if (a.getCreatedAt() == null) a.setCreatedAt(Instant.now());
            a.setUpdatedAt(Instant.now());
            return a;
        }).toList();

        return audioRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allAudios", "Audio"}, allEntries = true)
    public AudioResponse update(String id, AudioRequest request) {
        Audio a = audioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio not found: " + id));

        mapper.copyForUpdate(request, a);
        a.setUpdatedAt(Instant.now());

        return mapper.toResponse(audioRepository.save(a));
    }

    @Override
    @CacheEvict(value = {"allAudios", "Audio"}, allEntries = true)
    public void softDelete(String id) {
        Audio a = audioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio not found: " + id));
        a.setIsActived(IsActived.INACTIVE);
        a.setUpdatedAt(Instant.now());
        audioRepository.save(a);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AudioResponse> search(String voice, String format, String language, String title,
                                      IsActived isActived, Pageable pageable) {
        String v = normalize(voice);
        String f = normalize(format);
        String l = normalize(language);
        String t = normalize(title);

        Audio probe = new Audio();
        if (v != null) probe.setVoice(v);
        if (f != null) probe.setFormat(f);
        if (l != null) probe.setLanguage(l);
        if (t != null) probe.setTitle(t);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("voice", m -> m.ignoreCase())
                .withMatcher("format", m -> m.ignoreCase())
                .withMatcher("language", m -> m.ignoreCase())
                .withMatcher("title", m -> m.ignoreCase().contains())
                .withIgnorePaths(
                        "audioId", "audioUrl", "durationMs",
                        "createdAt", "updatedAt"
                )
                .withIgnoreNullValues();

        Example<Audio> example = Example.of(probe, matcher);

        return audioRepository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
