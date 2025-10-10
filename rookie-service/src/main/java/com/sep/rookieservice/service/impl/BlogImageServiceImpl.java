package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BlogImageRequest;
import com.sep.rookieservice.dto.BlogImageResponse;
import com.sep.rookieservice.entity.BlogImage;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.mapper.BlogImageMapper;
import com.sep.rookieservice.repository.BlogImageRepository;
import com.sep.rookieservice.service.BlogImageService;
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
public class BlogImageServiceImpl implements BlogImageService {

    private final BlogImageRepository repository;
    @Qualifier("blogImageMapper")
    private final BlogImageMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allBlogImages", key = "'all'")
    public List<BlogImageResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "BlogImage", key = "#id")
    public BlogImageResponse getById(String id) {
        BlogImage e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("BlogImage not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allBlogImages", "BlogImage"}, allEntries = true)
    public List<BlogImageResponse> create(List<BlogImageRequest> requests) {
        List<BlogImage> entities = requests.stream().map(req -> {
            if (req.getBlogId() == null || req.getBlogId().isBlank())
                throw new IllegalArgumentException("blogId is required");
            BlogImage e = new BlogImage();
            mapper.copyForCreate(req, e);
            if (e.getPosition() == null) e.setPosition(0);
            return e;
        }).toList();
        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allBlogImages", "BlogImage"}, allEntries = true)
    public BlogImageResponse update(String id, BlogImageRequest request) {
        BlogImage e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("BlogImage not found: " + id));
        mapper.copyForUpdate(request, e);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allBlogImages", "BlogImage"}, allEntries = true)
    public void softDelete(String id) {
        BlogImage e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("BlogImage not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogImageResponse> search(String blogId, String altText, IsActived isActived, Pageable pageable) {
        BlogImage probe = new BlogImage();
        if (blogId != null && !blogId.isBlank()) probe.setBlogId(blogId.trim());
        if (altText != null && !altText.isBlank()) probe.setAltText(altText.trim());
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("altText", m -> m.ignoreCase().contains())
                .withMatcher("blogId", m -> m.ignoreCase())
                .withIgnorePaths("blogImageId", "imageUrl", "position", "blog")
                .withIgnoreNullValues();

        return repository.findAll(Example.of(probe, matcher), pageable)
                .map(mapper::toResponse);
    }
}
