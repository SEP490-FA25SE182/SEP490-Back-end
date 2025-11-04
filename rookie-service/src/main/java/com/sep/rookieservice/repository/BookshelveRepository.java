package com.sep.rookieservice.repository;

import org.springframework.data.domain.Page;
import com.sep.rookieservice.entity.Bookshelve;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface BookshelveRepository extends JpaRepository<Bookshelve, String>, JpaSpecificationExecutor<Bookshelve> {

    Page<Bookshelve> findByUserIdAndIsActived(String userId, IsActived isActived, Pageable pageable);
}
