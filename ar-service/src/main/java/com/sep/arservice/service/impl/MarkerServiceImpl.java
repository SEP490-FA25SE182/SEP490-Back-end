package com.sep.arservice.service.impl;

import com.sep.arservice.dto.MarkerRequest;
import com.sep.arservice.dto.MarkerResponse;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.mapper.MarkerMapper;
import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkerServiceImpl implements MarkerService {
    private final MarkerRepository repo;
    private final MarkerMapper mapper;

    @Override @Transactional(readOnly = true)
    public List<MarkerResponse> getAll() {
        return repo.findAllByIsActived(IsActived.ACTIVE)
                .stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public MarkerResponse getById(String id) {
        Marker e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + id));
        if (e.getIsActived() != IsActived.ACTIVE) {
            throw new RuntimeException("Marker is inactive: " + id);
        }
        return mapper.toResponse(e);
    }

    @Override
    public MarkerResponse create(MarkerRequest req) {
        if (repo.existsByMarkerCodeIgnoreCaseAndIsActived(req.getMarkerCode(), IsActived.ACTIVE)) {
            throw new IllegalArgumentException("markerCode duplicated");
        }
        Marker e = new Marker();
        mapper.copyForCreate(req, e);
        e.setIsActived(IsActived.ACTIVE);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public MarkerResponse update(String id, MarkerRequest req) {
        Marker e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + id));
        if (e.getIsActived() != IsActived.ACTIVE) {
            throw new RuntimeException("Marker is inactive: " + id);
        }
        mapper.copyForUpdate(req, e);
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void softDelete(String id) {
        Marker e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        e.setUpdatedAt(Instant.now());
        repo.save(e);
    }

    @Override @Transactional(readOnly = true)
    public Page<MarkerResponse> search(String markerCode, String markerType, Pageable pageable) {
        Marker probe = new Marker();
        if (markerCode!=null && !markerCode.isBlank()) probe.setMarkerCode(markerCode.trim());
        if (markerType!=null && !markerType.isBlank()) probe.setMarkerType(markerType.trim());
        probe.setIsActived(IsActived.ACTIVE);

        ExampleMatcher m = ExampleMatcher.matchingAll()
                .withMatcher("markerCode", mm -> mm.ignoreCase().contains())
                .withMatcher("markerType", mm -> mm.ignoreCase())
                .withIgnoreNullValues();

        return repo.findAll(Example.of(probe, m), pageable).map(mapper::toResponse);
    }
}

