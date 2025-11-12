package com.sep.arservice.repository;

import com.sep.arservice.enums.IsActived;
import com.sep.arservice.model.Marker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, String> {
    Optional<Marker> findByMarkerCodeIgnoreCaseAndIsActived(String markerCode, IsActived isActived);

    boolean existsByMarkerCodeIgnoreCaseAndIsActived(String markerCode, IsActived isActived);

    List<Marker> findAllByIsActived(IsActived isActived);
}

