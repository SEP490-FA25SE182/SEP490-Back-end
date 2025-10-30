package com.sep.aiservice.repository;

import com.sep.aiservice.entity.PageAudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageAudioRepository extends JpaRepository<PageAudio, String> {
}

