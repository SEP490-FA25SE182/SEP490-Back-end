package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BlogRequest;
import com.sep.rookieservice.dto.BlogResponse;
import com.sep.rookieservice.entity.Blog;
import com.sep.rookieservice.entity.Tag;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.mapper.BlogMapper;
import com.sep.rookieservice.repository.BlogRepository;
import com.sep.rookieservice.repository.TagRepository;
import com.sep.rookieservice.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogServiceImpl implements BlogService {

    private final BlogRepository repository;
    private final TagRepository tagRepository;

    @Qualifier("blogMapper")
    private final BlogMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allBlogs", key = "'all'")
    public List<BlogResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Blog", key = "#id")
    public BlogResponse getById(String id) {
        Blog e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allBlogs", "Blog"}, allEntries = true)
    public List<BlogResponse> create(List<BlogRequest> requests) {
        List<Blog> entities = new ArrayList<>();
        for (BlogRequest req : requests) {
            if (req.getAuthorId() == null || req.getAuthorId().isBlank())
                throw new IllegalArgumentException("authorId is required");
            if (req.getTitle() == null || req.getTitle().isBlank())
                throw new IllegalArgumentException("title is required");

            Blog e = new Blog();
            mapper.copyForCreate(req, e);
            if (e.getIsActived() == null) e.setIsActived(IsActived.ACTIVE);
            if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
            e.setUpdatedAt(Instant.now());

            if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
                Set<Tag> tags = new HashSet<>(tagRepository.findAllById(req.getTagIds()));
                e.setTags(tags);
            }

            entities.add(e);
        }
        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allBlogs", "Blog"}, allEntries = true)
    public BlogResponse update(String id, BlogRequest request) {
        Blog e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found: " + id));

        mapper.copyForUpdate(request, e);
        e.setUpdatedAt(Instant.now());

        if (request.getTagIds() != null) {
            Set<Tag> tags = request.getTagIds().isEmpty()
                    ? new HashSet<>()
                    : new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            e.setTags(tags);
        }

        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allBlogs", "Blog"}, allEntries = true)
    public void softDelete(String id) {
        Blog e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        e.setUpdatedAt(Instant.now());
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogResponse> search(
            String title,
            String content,
            String authorId,
            String bookId,
            IsActived isActived,
            Set<String> tagIds,
            Set<String> tagNames,
            Pageable pageable
    ) {
        String t = normalize(title);
        String c = normalize(content);
        String a = normalize(authorId);
        String b = normalize(bookId);

        Blog probe = new Blog();
        if (t != null) probe.setTitle(t);
        if (c != null) probe.setContent(c);
        if (a != null) probe.setAuthorId(a);
        if (b != null) probe.setBookId(b);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("title", m -> m.ignoreCase().contains())
                .withMatcher("content", m -> m.ignoreCase().contains())
                .withMatcher("authorId", m -> m.ignoreCase())
                .withMatcher("bookId", m -> m.ignoreCase())
                .withIgnorePaths(
                        "blogId", "createdAt", "updatedAt",
                        "user", "book", "images", "tags"
                )
                .withIgnoreNullValues();

        List<Blog> base = repository.findAll(Example.of(probe, matcher));

        // Lọc theo tags trong memory
        boolean filterByIds = tagIds != null && !tagIds.isEmpty();
        boolean filterByNames = tagNames != null && !tagNames.isEmpty();

        List<Blog> filtered = base.stream()
                .filter(blog -> {
                    if (!filterByIds && !filterByNames) return true;
                    Set<String> bIds = blog.getTags() == null ? Set.of()
                            : blog.getTags().stream().map(Tag::getTagId).collect(Collectors.toSet());
                    Set<String> bNames = blog.getTags() == null ? Set.of()
                            : blog.getTags().stream().map(Tag::getName).map(s -> s == null ? null : s.toLowerCase()).filter(Objects::nonNull).collect(Collectors.toSet());

                    boolean okIds = !filterByIds || bIds.containsAll(tagIds);
                    boolean okNames = !filterByNames || bNames.containsAll(
                            tagNames.stream().filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet())
                    );
                    return okIds && okNames;
                })
                .toList();

        // Manual paging
        int total = filtered.size();
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<BlogResponse> contentPage = filtered.subList(from, to).stream().map(mapper::toResponse).toList();

        return new PageImpl<>(contentPage, pageable, total);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
