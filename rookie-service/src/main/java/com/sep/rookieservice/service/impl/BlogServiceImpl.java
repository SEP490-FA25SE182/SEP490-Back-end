package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BlogRequest;
import com.sep.rookieservice.dto.BlogResponse;
import com.sep.rookieservice.entity.Blog;
import com.sep.rookieservice.entity.Tag;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.UpdatedOrder;
import com.sep.rookieservice.mapper.BlogMapper;
import com.sep.rookieservice.repository.BlogRepository;
import com.sep.rookieservice.repository.TagRepository;
import com.sep.rookieservice.service.BlogService;
import com.sep.rookieservice.specification.BlogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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
            Set<String> tagNames,
            Pageable pageable
    ) {
        Specification<Blog> spec = allOfNonNull(
                BlogSpecification.titleContains(normalize(title)),
                BlogSpecification.contentContains(normalize(content)),
                BlogSpecification.authorEq(normalize(authorId)),
                BlogSpecification.bookEq(normalize(bookId)),
                BlogSpecification.activedEq(isActived),
                BlogSpecification.hasAnyTagNames(tagNames)
        );

        Page<Blog> page = repository.findAll(spec, pageable);
        List<BlogResponse> mapped = page.getContent().stream().map(mapper::toResponse).toList();
        return new PageImpl<>(mapped, pageable, page.getTotalElements());
    }

    @SafeVarargs
    private final Specification<Blog> allOfNonNull(Specification<Blog>... specs) {
        var list = Arrays.stream(specs).filter(Objects::nonNull).toList();
        return list.isEmpty() ? null : Specification.allOf(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogResponse> filterByUpdated(UpdatedOrder order, Pageable pageable) {
        Sort sort = (order == UpdatedOrder.OLDEST)
                ? Sort.by(Sort.Order.asc("updatedAt"))
                : Sort.by(Sort.Order.desc("updatedAt"));

        Pageable effective = pageable.getSort().isUnsorted()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort)
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort.and(pageable.getSort()));

        Page<Blog> page = repository.findAll(effective);
        List<BlogResponse> mapped = page.getContent().stream().map(mapper::toResponse).toList();
        return new PageImpl<>(mapped, effective, page.getTotalElements());
    }

    private String normalize(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
