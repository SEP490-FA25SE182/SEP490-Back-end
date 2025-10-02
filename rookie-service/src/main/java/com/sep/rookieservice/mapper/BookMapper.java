package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequestDTO dto, Book existing) {
        Book b = existing == null ? new Book() : existing;

        if (dto.getBookName() != null) b.setBookName(dto.getBookName());
        if (dto.getCoverUrl() != null) b.setCoverUrl(dto.getCoverUrl());
        if (dto.getDecription() != null) b.setDecription(dto.getDecription());
        if (dto.getAuthorId() != null) b.setAuthorId(dto.getAuthorId());
        if (dto.getProgressStatus() != null) b.setProgressStatus(dto.getProgressStatus());
        if (dto.getPublicationStatus() != null) b.setPublicationStatus(dto.getPublicationStatus());
        if (dto.getPublishedDate() != null) b.setPublishedDate(dto.getPublishedDate());

        return b;
    }

    public BookResponseDTO toDto(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setBookId(book.getBookId());
        dto.setBookName(book.getBookName());
        dto.setCoverUrl(book.getCoverUrl());
        dto.setDecription(book.getDecription());
        dto.setAuthorId(book.getAuthorId());
        dto.setProgressStatus(book.getProgressStatus());
        dto.setPublicationStatus(book.getPublicationStatus());
        dto.setIsActived(book.getIsActived());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        dto.setPublishedDate(book.getPublishedDate());
        return dto;
    }
}
