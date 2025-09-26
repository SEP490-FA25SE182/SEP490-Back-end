package com.sep.arservice.repository;

import com.sep.arservice.model.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, String> {
    Optional<Marker> findById(String markerId);
}

