package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.CommentRequestDTO;
import com.sep.rookieservice.dto.CommentResponseDTO;
import com.sep.rookieservice.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDTO dto, Comment existing) {
        if (existing == null) existing = new Comment();

        existing.setContent(dto.getContent());
        existing.setIsPublished(dto.getIsPublished() != null ? dto.getIsPublished() : true);
        existing.setIsActived(dto.getIsActived());
        existing.setUserId(dto.getUserId());
        existing.setBlogId(dto.getBlogId());
        existing.setUpdatedAt(java.time.Instant.now());

        return existing;
    }

    public CommentResponseDTO toDto(Comment entity) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setCommentId(entity.getCommentId());
        dto.setContent(entity.getContent());
        dto.setIsPublished(entity.getIsPublished());
        dto.setUserId(entity.getUserId());
        dto.setBlogId(entity.getBlogId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setIsActived(entity.getIsActived());
        return dto;
    }
}
