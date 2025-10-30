package com.sep.aiservice.repository;

import com.sep.aiservice.entity.PageIllustration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageIllustrationRepository extends JpaRepository<PageIllustration, String> {
}

