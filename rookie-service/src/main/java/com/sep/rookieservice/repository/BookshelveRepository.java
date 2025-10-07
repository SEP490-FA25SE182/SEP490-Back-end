package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Bookshelve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookshelveRepository extends JpaRepository<Bookshelve, String>, JpaSpecificationExecutor<Bookshelve> {
}
