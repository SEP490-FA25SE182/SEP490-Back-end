package com.sep.rookieservice.repository;

import com.sep.rookieservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
}
