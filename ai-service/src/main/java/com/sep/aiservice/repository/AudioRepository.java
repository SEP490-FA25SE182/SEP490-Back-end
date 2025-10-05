package com.sep.aiservice.repository;

import com.sep.aiservice.entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AudioRepository extends JpaRepository<Audio, String> {
    Optional<Audio> findByTitle(String title);
}
