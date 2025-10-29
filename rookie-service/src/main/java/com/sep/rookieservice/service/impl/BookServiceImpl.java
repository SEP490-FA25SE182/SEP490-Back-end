package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BookAnalyticsResponse;
import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.entity.Bookshelve;
import com.sep.rookieservice.entity.Genre;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.BookMapper;
import com.sep.rookieservice.repository.BookRepository;
import com.sep.rookieservice.repository.BookshelveRepository;
import com.sep.rookieservice.repository.GenreRepository;
import com.sep.rookieservice.service.BookService;
import com.sep.rookieservice.specification.BookSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository repo;
    private final BookMapper mapper;

    private final GenreRepository genreRepo;
    private final BookshelveRepository shelfRepo;

    @Autowired
    public BookServiceImpl(BookRepository repo,
                           BookMapper mapper,
                           GenreRepository genreRepo,
                           BookshelveRepository shelfRepo) {
        this.repo = repo;
        this.mapper = mapper;
        this.genreRepo = genreRepo;
        this.shelfRepo = shelfRepo;
    }

    @Override
    public BookResponseDTO create(BookRequestDTO dto) {
        Book entity = mapper.toEntity(dto, null);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        Book saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getById(String id) {
        Book book = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapper.toDto(book);
    }

    @Override
    public BookResponseDTO update(String id, BookRequestDTO dto) {
        Book existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        Book updated = mapper.toEntity(dto, existing);
        updated.setUpdatedAt(Instant.now());
        Book saved = repo.save(updated);
        return mapper.toDto(saved);
    }

    @Override
    public void softDelete(String id) {
        Book existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        existing.setIsActived(IsActived.INACTIVE);
        existing.setUpdatedAt(Instant.now());
        repo.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDTO> search(
            String q,
            String authorId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Byte publicationStatus,
            Byte progressStatus,
            IsActived isActived,
            String genreId,
            String bookshelfId,
            Pageable pageable
    ) {
        Specification<Book> spec = BookSpecification.buildSpecification(q, authorId, publicationStatus, progressStatus, isActived, minPrice, maxPrice);

        if (genreId != null && !genreId.isEmpty()) {
            // check membership in collection 'genres'
            spec = spec.and((root, query, cb) ->
                    cb.isMember(genreRepo.getReferenceById(genreId), root.get("genres"))
            );
        }

        if (bookshelfId != null && !bookshelfId.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.isMember(shelfRepo.getReferenceById(bookshelfId), root.get("bookshelves"))
            );
        }

        return repo.findAll(spec, pageable).map(mapper::toDto);
    }

    @Override
    public BookResponseDTO addGenresToBook(String bookId, List<String> genreIds) {
        Book book = repo.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        List<Genre> genres = genreRepo.findAllById(genreIds);
        if (book.getGenres() == null) book.setGenres(new ArrayList<>());
        book.getGenres().addAll(genres);
        Book saved = repo.save(book);
        return mapper.toDto(saved);
    }

    @Override
    public BookResponseDTO removeGenreFromBook(String bookId, String genreId) {
        Book book = repo.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (book.getGenres() != null) {
            book.getGenres().removeIf(g -> genreId.equals(g.getGenreId()));
        }
        Book saved = repo.save(book);
        return mapper.toDto(saved);
    }

    @Override
    public BookResponseDTO addBookToBookshelves(String bookId, List<String> shelfIds) {
        Book book = repo.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        List<Bookshelve> shelves = shelfRepo.findAllById(shelfIds);
        if (book.getBookshelves() == null) book.setBookshelves(new ArrayList<>());
        book.getBookshelves().addAll(shelves);
        Book saved = repo.save(book);
        return mapper.toDto(saved);
    }

    @Override
    public BookResponseDTO removeBookFromBookshelf(String bookId, String shelfId) {
        Book book = repo.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (book.getBookshelves() != null) {
            book.getBookshelves().removeIf(s -> shelfId.equals(s.getBookshelveId()) || shelfId.equals(s.getBookshelveId()));
            // above tries both possible getter names depending on your Bookshelve entity naming
        }
        Book saved = repo.save(book);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "BookAnalytics", key = "#monthsBack != null ? #monthsBack : 12")
    public BookAnalyticsResponse getAnalytics(Integer monthsBack) {
        int months = (monthsBack == null || monthsBack < 1 || monthsBack > 36) ? 12 : monthsBack;

        long totalBooks = repo.count();

        List<BookAnalyticsResponse.ActiveCount> byIsActived = Arrays.stream(IsActived.values())
                .map(st -> {
                    long c = repo.countByIsActived(st);
                    var ac = new BookAnalyticsResponse.ActiveCount();
                    ac.setStatus(st.name());
                    ac.setCount(c);
                    return ac;
                }).toList();

        var newestBooks = repo.findTop10ByOrderByCreatedAtDesc().stream().map(b -> {
            var rb = new BookAnalyticsResponse.RecentBook();
            rb.setBookId(b.getBookId());
            rb.setBookName(b.getBookName());
            rb.setAuthorName(b.getAuthor() != null ? b.getAuthor().getFullName() : null);
            rb.setCreatedAt(b.getCreatedAt());
            rb.setUpdatedAt(b.getUpdatedAt());
            return rb;
        }).toList();

        var recentlyUpdated = repo.findTop10ByOrderByUpdatedAtDesc().stream().map(b -> {
            var rb = new BookAnalyticsResponse.RecentBook();
            rb.setBookId(b.getBookId());
            rb.setBookName(b.getBookName());
            rb.setAuthorName(b.getAuthor() != null ? b.getAuthor().getFullName() : null);
            rb.setCreatedAt(b.getCreatedAt());
            rb.setUpdatedAt(b.getUpdatedAt());
            return rb;
        }).toList();


        ZoneId tz = ZoneOffset.UTC;
        LocalDate startMonth = LocalDate.now(tz).withDayOfMonth(1).minusMonths(months - 1);
        List<BookAnalyticsResponse.MonthlyCreated> monthly = new ArrayList<>();

        for (int i = 0; i < months; i++) {
            LocalDate mStart = startMonth.plusMonths(i);
            LocalDate mEnd = mStart.plusMonths(1);
            Instant from = mStart.atStartOfDay(tz).toInstant();
            Instant to = mEnd.atStartOfDay(tz).toInstant();

            long count = repo.countByCreatedAtBetween(from, to);
            var mc = new BookAnalyticsResponse.MonthlyCreated();
            mc.setYear(mStart.getYear());
            mc.setMonth(mStart.getMonthValue());
            mc.setCount(count);
            monthly.add(mc);
        }

        BookAnalyticsResponse resp = new BookAnalyticsResponse();
        resp.setTotalBooks(totalBooks);
        resp.setByIsActived(byIsActived);
        resp.setNewestBooks(newestBooks);
        resp.setRecentlyUpdatedBooks(recentlyUpdated);
        resp.setMonthlyCreated(monthly);

        return resp;
    }
}
