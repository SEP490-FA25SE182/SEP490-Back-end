package com.sep.arservice.repository;

import com.sep.arservice.model.ARScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ARSceneRepository extends JpaRepository<ARScene, String> {
    Optional<ARScene> findTopByMarkerIdAndStatusOrderByCreatedAtDesc(String markerId, String status);
}