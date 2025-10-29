package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.BookAnalyticsResponse;
import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {
    BookResponseDTO create(BookRequestDTO dto);
    BookResponseDTO getById(String id);
    BookResponseDTO update(String id, BookRequestDTO dto);
    void softDelete(String id);

    Page<BookResponseDTO> search(
            String q,
            String authorId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minQuantity,
            Byte publicationStatus,
            Byte progressStatus,
            IsActived isActived,
            String genreId,
            String bookshelfId,
            Pageable pageable
    );

    BookResponseDTO addGenresToBook(String bookId, List<String> genreIds);
    BookResponseDTO removeGenreFromBook(String bookId, String genreId);

    BookResponseDTO addBookToBookshelves(String bookId, List<String> shelfIds);
    BookResponseDTO removeBookFromBookshelf(String bookId, String shelfId);

    BookAnalyticsResponse getAnalytics(Integer monthsBack);
}
