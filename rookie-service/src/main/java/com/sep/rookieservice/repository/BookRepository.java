package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    long countByIsActived(IsActived isActived);

    List<Book> findTop10ByOrderByCreatedAtDesc();

    List<Book> findTop10ByOrderByUpdatedAtDesc();

    long countByCreatedAtBetween(Instant from, Instant to);

    @Query("SELECT b FROM Book b WHERE b.createdAt >= :since ORDER BY b.createdAt DESC")
    List<Book> findFeaturedBooksSince(Instant since);
}
