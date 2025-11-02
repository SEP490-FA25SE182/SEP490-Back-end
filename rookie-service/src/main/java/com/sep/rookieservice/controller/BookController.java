package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BookAnalyticsResponse;
import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/books")
public class BookController {

    private final BookService svc;

    @Autowired
    public BookController(BookService svc) {
        this.svc = svc;
    }

    @PostMapping
    public BookResponseDTO create(@Valid @RequestBody BookRequestDTO dto) {
        return svc.create(dto);
    }

    @GetMapping("/{id}")
    public BookResponseDTO getById(@PathVariable String id) {
        return svc.getById(id);
    }

    @PutMapping("/{id}")
    public BookResponseDTO update(@PathVariable String id, @Valid @RequestBody BookRequestDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.softDelete(id);
    }

    /**
     * Search & pagination endpoint.
     * Example:
     * /api/rookie/users/books?page=0&size=20&sort=createdAt,desc&q=magic
     */
    @GetMapping
    public Page<BookResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Byte publicationStatus,
            @RequestParam(required = false) Byte progressStatus,
            @RequestParam(required = false) IsActived isActived,
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) String bookshelfId
    ) {
        Sort sortObj = Sort.unsorted();

        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;

                String[] parts = s.split("-");
                String prop = parts[0].trim();
                Sort.Direction dir = Sort.Direction.ASC;

                if (parts.length > 1) {
                    try {
                        dir = Sort.Direction.valueOf(parts[1].trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        String dirStr = parts[1].trim().toUpperCase();
                        if (dirStr.equals("DESC")) {
                        dir = Sort.Direction.DESC;
                        }
                    }
                }
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return svc.search(q, authorId, minPrice, maxPrice, minQuantity, publicationStatus, progressStatus, isActived, genreId, bookshelfId, pageable);
    }


    @PostMapping("/{bookId}/genres")
    public BookResponseDTO addGenresToBook(
            @PathVariable String bookId,
            @RequestBody List<String> genreIds
    ) {
        return svc.addGenresToBook(bookId, genreIds);
    }

    @DeleteMapping("/{bookId}/genres/{genreId}")
    public BookResponseDTO removeGenreFromBook(
            @PathVariable String bookId,
            @PathVariable String genreId
    ) {
        return svc.removeGenreFromBook(bookId, genreId);
    }

    @PostMapping("/{bookId}/bookshelves")
    public BookResponseDTO addBookToBookshelves(
            @PathVariable String bookId,
            @RequestBody List<String> shelfIds
    ) {
        return svc.addBookToBookshelves(bookId, shelfIds);
    }

    @DeleteMapping("/{bookId}/bookshelves/{shelfId}")
    public BookResponseDTO removeBookFromBookshelf(
            @PathVariable String bookId,
            @PathVariable String shelfId
    ) {
        return svc.removeBookFromBookshelf(bookId, shelfId);
    }

    /**
     * Analytics endpoint for dashboard visualization
     * Example: /api/rookie/users/books/analytics?monthsBack=6
     */
    @GetMapping("/analytics")
    public BookAnalyticsResponse getBookAnalytics(@RequestParam(required = false) Integer monthsBack) {
        return svc.getAnalytics(monthsBack);
    }

    @PatchMapping("/{id}/progress-status")
    public BookResponseDTO updateProgressStatus(
            @PathVariable String id,
            @RequestParam Byte progressStatus
    ) {
        return svc.updateProgressStatus(id, progressStatus);
    }

    @PatchMapping("/{id}/publication-status")
    public BookResponseDTO updatePublicationStatus(
            @PathVariable String id,
            @RequestParam Byte publicationStatus
    ) {
        return svc.updatePublicationStatus(id, publicationStatus);
    }
}
