package com.sep.arservice.repository;

import com.sep.arservice.model.Asset3D;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Asset3DRepository extends JpaRepository<Asset3D, String> {

    Page<Asset3D> findByMarker_MarkerCodeIgnoreCase(String markerCode, Pageable pageable);

    List<Asset3D> findByMarkerIdOrderByCreatedAtDesc(String markerId);
}
