package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.BookAnalyticsResponse;
import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.BookMapper;
import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.repository.BookRepository;
import com.sep.rookieservice.service.BookService;
import com.sep.rookieservice.specification.BookSpecification;
import jakarta.persistence.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public BookAnalyticsResponse getAnalytics(Integer monthsBack) {
        int months = (monthsBack == null || monthsBack < 1 || monthsBack > 36) ? 12 : monthsBack;

        long totalBooks = repo.count();

        // Count by active status
        List<BookAnalyticsResponse.ActiveCount> byIsActived = Arrays.stream(IsActived.values())
                .map(st -> {
                    long c = repo.countByIsActived(st);
                    BookAnalyticsResponse.ActiveCount ac = new BookAnalyticsResponse.ActiveCount();
                    ac.setStatus(st.name());
                    ac.setCount(c);
                    return ac;
                })
                .toList();

        // Recent and featured books
        List<BookAnalyticsResponse.RecentBook> newest = repo.findTop10ByOrderByCreatedAtDesc().stream()
                .map(b -> {
                    BookAnalyticsResponse.RecentBook rb = new BookAnalyticsResponse.RecentBook();
                    rb.setBookId(b.getBookId());
                    rb.setBookName(b.getBookName());
                    rb.setAuthorName(b.getAuthor() != null ? b.getAuthor().getFullName() : null);
                    rb.setCreatedAt(b.getCreatedAt());
                    rb.setUpdatedAt(b.getUpdatedAt());
                    return rb;
                })
                .toList();

        List<BookAnalyticsResponse.RecentBook> recentUpdates = repo.findTop10ByOrderByUpdatedAtDesc().stream()
                .map(b -> {
                    BookAnalyticsResponse.RecentBook rb = new BookAnalyticsResponse.RecentBook();
                    rb.setBookId(b.getBookId());
                    rb.setBookName(b.getBookName());
                    rb.setAuthorName(b.getAuthor() != null ? b.getAuthor().getFullName() : null);
                    rb.setCreatedAt(b.getCreatedAt());
                    rb.setUpdatedAt(b.getUpdatedAt());
                    return rb;
                })
                .toList();

        // Featured books in recent months
        Instant since = Instant.now().minus(Duration.ofDays(30L * months));
        var featuredBooks = repo.findFeaturedBooksSince(since);

        // Monthly created trend
        ZoneId tz = ZoneOffset.UTC;
        LocalDate startMonth = LocalDate.now(tz).withDayOfMonth(1).minusMonths(months - 1);

        List<BookAnalyticsResponse.MonthlyCreated> monthly = new ArrayList<>(months);
        for (int i = 0; i < months; i++) {
            LocalDate mStart = startMonth.plusMonths(i);
            LocalDate mEnd = mStart.plusMonths(1);

            Instant from = mStart.atStartOfDay(tz).toInstant();
            Instant to = mEnd.atStartOfDay(tz).toInstant();

            long c = repo.countByCreatedAtBetween(from, to);

            BookAnalyticsResponse.MonthlyCreated mc = new BookAnalyticsResponse.MonthlyCreated();
            mc.setYear(mStart.getYear());
            mc.setMonth(mStart.getMonthValue());
            mc.setCount(c);
            monthly.add(mc);
        }

        // Build response
        BookAnalyticsResponse resp = new BookAnalyticsResponse();
        resp.setTotalBooks(totalBooks);
        resp.setByIsActived(byIsActived);
        resp.setNewestBooks(newest);
        resp.setRecentlyUpdatedBooks(recentUpdates);
        resp.setMonthlyCreated(monthly);
        return resp;
    }
}
