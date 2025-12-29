package com.sep.arservice.controller;

import com.sep.arservice.dto.CreateAprilTagMarkerRequest;
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
    public void softDelete(@PathVariable @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id){ service.softDelete(id); }

    @GetMapping("/search")
    public Page<MarkerResponse> search(
            @RequestParam(required = false)
            @Size(max = 50) String markerCode,
            @RequestParam(required = false)
            @Size(max = 50) String markerType,
            @RequestParam(required = false)
            @Size(max = 50) String bookId,
            @RequestParam(required = false)
            @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String pageId,
            @RequestParam(required=false)
            @Size(max=50) String userId,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(markerCode, markerType, bookId, pageId, userId, pageable);
    }


    @PostMapping("/pages/{pageId}")
    public MarkerResponse createForPage(
            @PathVariable
            @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String pageId,
            @Valid @RequestBody MarkerRequest req
    ) {
        return service.createWithPage(pageId, req);
    }

    @PostMapping("/{markerId}/pages/{pageId}")
    public MarkerResponse attachPage(
            @PathVariable
            @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String markerId,
            @PathVariable
            @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String pageId
    ) {
        return service.attachPage(markerId, pageId);
    }

    @PostMapping("/apriltag")
    public MarkerResponse createAprilTag(@RequestBody @Valid CreateAprilTagMarkerRequest req) {
        return service.createAprilTag(req);
    }
}
