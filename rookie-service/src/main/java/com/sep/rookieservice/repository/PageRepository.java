package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, String>, JpaSpecificationExecutor<Page> {

    boolean existsByChapterIdAndPageNumber(String chapterId, Integer pageNumber);
}
