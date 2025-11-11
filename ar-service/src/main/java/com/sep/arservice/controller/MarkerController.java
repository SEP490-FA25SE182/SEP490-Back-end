package com.sep.arservice.controller;

import com.sep.arservice.dto.MarkerRequest;
import com.sep.arservice.dto.MarkerResponse;
import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.service.MarkerService;
import com.sep.arservice.service.impl.MarkerServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/markers")
@RequiredArgsConstructor
@Validated
public class MarkerController {
    private final MarkerService service;

    @GetMapping public List<MarkerResponse> getAll(){ return service.getAll(); }

    @GetMapping("/{id}")
    public MarkerResponse getOne(@PathVariable @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) { return service.getById(id); }

    @PostMapping public MarkerResponse create(@RequestBody @Valid MarkerRequest req){ return service.create(req); }

    @PutMapping("/{id}")
    public MarkerResponse update(@PathVariable @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id,
                                 @RequestBody @Valid MarkerRequest req){ return service.update(id, req); }

    @DeleteMapping("/{id}")
    public void deleteHard(@PathVariable @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id){ service.deleteHard(id); }

    @GetMapping("/search")
    public Page<MarkerResponse> search(
            @RequestParam(required=false)
            @Size(max=50) String markerCode,
            @RequestParam(required=false)
            @Size(max=50) String markerType,
            @ParameterObject
            @PageableDefault(size=20) Pageable pageable){
        return service.search(markerCode, markerType, pageable);
    }
}
