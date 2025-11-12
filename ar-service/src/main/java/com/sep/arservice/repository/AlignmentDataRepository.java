package com.sep.arservice.repository;

import com.sep.arservice.model.AlignmentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlignmentDataRepository extends JpaRepository<AlignmentData, String> {

    List<AlignmentData> findByMarkerIdOrderByCreatedAtDesc(String markerId);

    Optional<AlignmentData> findTopByMarkerIdOrderByCreatedAtDesc(String markerId);

    List<AlignmentData> findByMarkerIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String markerId, Instant from, Instant to
    );
}
