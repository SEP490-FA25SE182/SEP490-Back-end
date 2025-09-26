package com.sep.aiservice.repository;

import com.sep.aiservice.model.Illustration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IllustrationRepository extends JpaRepository<Illustration, String> {
    Optional<Illustration> findById(String illustrationId);
}