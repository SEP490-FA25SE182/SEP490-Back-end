package com.sep.arservice.controller;

import com.sep.arservice.dto.ARSceneRequest;
import com.sep.arservice.dto.ARSceneResponse;
import com.sep.arservice.dto.ARSceneWithItemsResponse;
import com.sep.arservice.service.ARSceneService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/ar-scenes")
@RequiredArgsConstructor
@Validated
public class ARSceneController {

    private final ARSceneService service;

    @PostMapping
    public ARSceneResponse create(@Valid @RequestBody ARSceneRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ARSceneResponse update(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id,
                                  @Valid @RequestBody ARSceneRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public ARSceneResponse getOne(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteHard(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) {
        service.deleteHard(id);
    }

    @GetMapping("/search")
    public Page<ARSceneResponse> search(
            @RequestParam(required = false)
            @Size(max = 50) String markerId,
            @RequestParam(required = false)
            @Size(max = 20) String status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(markerId, status, pageable);
    }

    // Unity lấy scene theo markerCode (PUBLISHED mới nhất)
    @GetMapping("/by-marker-code/{code}")
    public ARSceneWithItemsResponse byMarkerCode(@PathVariable("code") @Size(max=50) String markerCode) {
        return service.getPublishedByMarkerCode(markerCode);
    }

    // Unity lấy scene theo markerId (PUBLISHED mới nhất)
    @GetMapping("/by-marker-id/{markerId}")
    public ARSceneWithItemsResponse byMarkerId(
            @PathVariable("markerId")
            @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String markerId) {
        return service.getPublishedByMarkerId(markerId);
    }

}
