package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.CommentRequestDTO;
import com.sep.rookieservice.dto.CommentResponseDTO;
import com.sep.rookieservice.entity.Comment;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.CommentMapper;
import com.sep.rookieservice.repository.CommentRepository;
import com.sep.rookieservice.service.CommentService;
import com.sep.rookieservice.specification.CommentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repo;
    private final CommentMapper mapper;

    @Override
    @Transactional
    public CommentResponseDTO create(CommentRequestDTO dto) {
        Comment entity = mapper.toEntity(dto, null);
        Comment saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public CommentResponseDTO update(String id, CommentRequestDTO dto) {
        Comment existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        Comment updated = mapper.toEntity(dto, existing);
        Comment saved = repo.save(updated);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO getById(String id) {
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        entity.setIsActived(IsActived.INACTIVE);
        repo.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> search(String q, String blogId, String userId, IsActived isActived, Pageable pageable) {
        Specification<Comment> spec = CommentSpecification.buildSpecification(q, blogId, userId, isActived);
        Page<Comment> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByBlogId(String blogId, boolean onlyPublished) {
        if (blogId == null || blogId.isBlank()) {
            throw new IllegalArgumentException("blogId is required");
        }
        if (onlyPublished) {
            return repo.countByBlogIdAndIsActivedAndIsPublished(blogId, IsActived.ACTIVE, true);
        }
        return repo.countByBlogIdAndIsActived(blogId, IsActived.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getByBlogId(String blogId) {
        if (blogId == null || blogId.isBlank()) {
            return List.of();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        List<Comment> entities = repo.findAll(
                CommentSpecification.forPublicByBlogId(blogId),
                sort
        );
        return entities.stream()
                .map(mapper::toDto)
                .toList();
    }
}
