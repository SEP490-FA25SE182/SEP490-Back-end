package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    long countByIsActived(IsActived isActived);

    long countByCreatedAtBetween(Instant from, Instant to);

    List<Book> findTop10ByOrderByCreatedAtDesc();

    List<Book> findTop10ByOrderByUpdatedAtDesc();

    @Query("SELECT b FROM Book b WHERE b.isActived = 'ACTIVE' AND b.createdAt >= :since ORDER BY b.createdAt DESC")
    List<Book> findFeaturedBooksSince(Instant since);
}
