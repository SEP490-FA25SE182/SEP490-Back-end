package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.BookMapper;
import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.repository.BookRepository;
import com.sep.rookieservice.service.BookService;
import com.sep.rookieservice.specification.BookSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository repo;
    private final BookMapper mapper;

    @Autowired
    public BookServiceImpl(BookRepository repo, BookMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public BookResponseDTO create(BookRequestDTO dto) {
        Book book = mapper.toEntity(dto, null);
        book.setCreatedAt(Instant.now());
        book.setUpdatedAt(Instant.now());
        // default isActived already set in entity
        Book saved = repo.save(book);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getById(String id) {
        Book book = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapper.toDto(book);
    }

    @Override
    public BookResponseDTO update(String id, BookRequestDTO dto) {
        Book existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        Book updated = mapper.toEntity(dto, existing);
        updated.setUpdatedAt(Instant.now());
        Book saved = repo.save(updated);
        return mapper.toDto(saved);
    }

    @Override
    public void softDelete(String id) {
        Book existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        existing.setIsActived(IsActived.INACTIVE); // uses enum from your codebase
        existing.setUpdatedAt(Instant.now());
        repo.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDTO> search(
            String q,
            String authorId,
            Byte publicationStatus,
            Byte progressStatus,
            IsActived isActived,
            Pageable pageable
    ) {
        Specification<Book> spec = BookSpecification.buildSpecification(q, authorId, publicationStatus, progressStatus, isActived);
        Page<Book> page = repo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }
}
