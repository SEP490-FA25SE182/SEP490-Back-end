package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Bookshelve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookshelveRepository extends JpaRepository<Bookshelve, String>, JpaSpecificationExecutor<Bookshelve> {
}
