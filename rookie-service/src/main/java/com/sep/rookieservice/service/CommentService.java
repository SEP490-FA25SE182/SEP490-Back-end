package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CommentRequestDTO;
import com.sep.rookieservice.dto.CommentResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentResponseDTO create(CommentRequestDTO dto);
    CommentResponseDTO update(String id, CommentRequestDTO dto);
    CommentResponseDTO getById(String id);
    void delete(String id);
    Page<CommentResponseDTO> search(String q, String blogId, String userId, IsActived isActived, Pageable pageable);
    long countByBlogId(String blogId, boolean onlyPublished);
    List<CommentResponseDTO> getByBlogId(String blogId);
}
