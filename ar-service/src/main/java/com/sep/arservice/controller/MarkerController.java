package com.sep.arservice.controller;

import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/markers")
@RequiredArgsConstructor
public class MarkerController {
    private final MarkerService markerService;
    private final MarkerRepository markerRepository;

    @GetMapping
    public List<Marker> getMarkers() {
        return markerService.getAllMarkers();
    }

    @PostMapping
    @CacheEvict(value = "allMarkers", allEntries = true)
    public Marker createMarker(@RequestBody Marker marker) {
        return markerRepository.save(marker);
    }

}