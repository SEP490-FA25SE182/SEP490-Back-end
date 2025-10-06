package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChapterRepository extends JpaRepository<Chapter, String>, JpaSpecificationExecutor<Chapter> {
}