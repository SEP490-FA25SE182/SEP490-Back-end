package com.sep.arservice.service;

import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final MarkerRepository markerRepository;

    @Cacheable(value = "allMarkers", key = "'all'")
    public List<Marker> getAllMarkers() {
        System.out.println("⏳ Querying DB...");
        return markerRepository.findAll();
    }
}