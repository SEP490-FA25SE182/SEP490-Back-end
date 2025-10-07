package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
}
