package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.BookshelveRequestDTO;
import com.sep.rookieservice.dto.BookshelveResponseDTO;
import com.sep.rookieservice.entity.Bookshelve;
import org.springframework.stereotype.Component;

@Component
public class BookshelveMapper {

    public Bookshelve toEntity(BookshelveRequestDTO dto, Bookshelve existing) {
        Bookshelve b = existing == null ? new Bookshelve() : existing;

        if (dto.getBookshelveName() != null) b.setBookshelveName(dto.getBookshelveName());
        if (dto.getDecription() != null) b.setDecription(dto.getDecription());
        if (dto.getUserId() != null) b.setUserId(dto.getUserId());

        return b;
    }

    public BookshelveResponseDTO toDto(Bookshelve b) {
        BookshelveResponseDTO dto = new BookshelveResponseDTO();
        dto.setBookshelveId(b.getBookshelveId());
        dto.setBookshelveName(b.getBookshelveName());
        dto.setDecription(b.getDecription());
        dto.setUserId(b.getUserId());
        dto.setIsActived(b.getIsActived());
        dto.setCreatedAt(b.getCreatedAt());
        dto.setUpdatedAt(b.getUpdatedAt());
        return dto;
    }
}