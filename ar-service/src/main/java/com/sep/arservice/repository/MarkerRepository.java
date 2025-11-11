package com.sep.arservice.repository;

import com.sep.arservice.model.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, String> {
    Optional<Marker> findByMarkerCodeIgnoreCase(String markerCode);
    boolean existsByMarkerCodeIgnoreCase(String markerCode);
}

