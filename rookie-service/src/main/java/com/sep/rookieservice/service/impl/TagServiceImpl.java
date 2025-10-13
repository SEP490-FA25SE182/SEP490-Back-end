package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.TagRequest;
import com.sep.rookieservice.dto.TagResponse;
import com.sep.rookieservice.entity.Tag;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.mapper.TagMapper;
import com.sep.rookieservice.repository.TagRepository;
import com.sep.rookieservice.service.TagService;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository repository;
    @Qualifier("tagMapper")
    private final TagMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allTags", key = "'all'")
    public List<TagResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Tag", key = "#id")
    public TagResponse getById(String id) {
        Tag e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allTags", "Tag"}, allEntries = true)
    public List<TagResponse> create(List<TagRequest> requests) {
        List<Tag> entities = requests.stream().map(req -> {
            Tag e = new Tag();
            mapper.copyForCreate(req, e);
            if (e.getIsActived() == null) e.setIsActived(IsActived.ACTIVE);
            return e;
        }).toList();
        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allTags", "Tag"}, allEntries = true)
    public TagResponse update(String id, TagRequest request) {
        Tag e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + id));
        mapper.copyForUpdate(request, e);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allTags", "Tag"}, allEntries = true)
    public void softDelete(String id) {
        Tag e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagResponse> search(String name, IsActived isActived, Pageable pageable) {
        Tag probe = new Tag();
        if (name != null && !name.isBlank()) probe.setName(name.trim());
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("name", m -> m.ignoreCase().contains())
                .withIgnorePaths("tagId", "blogs")
                .withIgnoreNullValues();

        return repository.findAll(Example.of(probe, matcher), pageable)
                .map(mapper::toResponse);
    }
}
