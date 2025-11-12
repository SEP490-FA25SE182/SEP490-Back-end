package com.sep.arservice.controller;

import com.sep.arservice.dto.AlignmentDataRequest;
import com.sep.arservice.dto.AlignmentDataResponse;
import com.sep.arservice.dto.AlignmentDataSearchRequest;
import com.sep.arservice.service.AlignmentDataService;
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

import java.util.List;

@RestController
@RequestMapping("/api/rookie/alignment-datas")
@RequiredArgsConstructor
@Validated
public class AlignmentDataController {

    private final AlignmentDataService service;

    @PostMapping
    public AlignmentDataResponse create(@Valid @RequestBody AlignmentDataRequest req) {
        return service.create(req);
    }

    @PostMapping("/batch")
    public List<AlignmentDataResponse> createBatch(@Valid @RequestBody List<AlignmentDataRequest> reqs) {
        return service.createBatch(reqs);
    }

    @PutMapping("/{id}")
    public AlignmentDataResponse update(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id,
                                        @Valid @RequestBody AlignmentDataRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public AlignmentDataResponse getOne(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteHard(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) {
        service.deleteHard(id);
    }

    // Search: markerId + from/to
    @GetMapping("/search")
    public Page<AlignmentDataResponse> search(
            @ParameterObject AlignmentDataSearchRequest filter,
            @ParameterObject @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(filter, pageable);
    }

    // Lấy mới nhất theo markerId
    @GetMapping("/latest")
    public AlignmentDataResponse latestByMarkerId(@RequestParam @Size(max=50) String markerId) {
        return service.latestByMarkerId(markerId);
    }

    // Lấy mới nhất theo markerCode (tiện cho Unity/Flutter)
    @GetMapping("/latest/by-marker-code/{code}")
    public AlignmentDataResponse latestByMarkerCode(@PathVariable("code") @Size(max=50) String markerCode) {
        return service.latestByMarkerCode(markerCode);
    }
}
