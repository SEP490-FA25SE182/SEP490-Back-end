package com.sep.arservice.service;

import com.sep.arservice.dto.MarkerRequest;
import com.sep.arservice.dto.MarkerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarkerService {
    List<MarkerResponse> getAll();
    MarkerResponse getById(String id);
    MarkerResponse create(MarkerRequest req);
    MarkerResponse update(String id, MarkerRequest req);
    void softDelete(String id);
    Page<MarkerResponse> search(String markerCode, String markerType, String pageId, String userId, Pageable pageable);
    MarkerResponse createWithPage(String pageId, MarkerRequest req);
    MarkerResponse attachPage(String markerId, String pageId);
}

