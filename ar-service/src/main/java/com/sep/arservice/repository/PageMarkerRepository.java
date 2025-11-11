package com.sep.arservice.repository;

import com.sep.arservice.model.PageMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageMarkerRepository extends JpaRepository<PageMarker, String> {
    List<PageMarker> findAllByPageId(String pageId);
}
