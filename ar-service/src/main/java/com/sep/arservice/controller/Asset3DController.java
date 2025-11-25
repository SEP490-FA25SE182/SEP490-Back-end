package com.sep.arservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.arservice.dto.Asset3DGenerateRequest;
import com.sep.arservice.dto.Asset3DResponse;
import com.sep.arservice.dto.Asset3DUploadRequest;
import com.sep.arservice.service.Asset3DService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/asset3d")
@RequiredArgsConstructor
@Validated
public class Asset3DController {

    private final Asset3DService service;

    @GetMapping
    public List<Asset3DResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Asset3DResponse getOne(@PathVariable @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id){ return service.getById(id); }

    // Upload trực tiếp 1 file 3D → Firebase → Asset3D
    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Asset3DResponse upload(@RequestPart("file") MultipartFile file,
                                  @RequestPart("meta") String metaJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Asset3DUploadRequest meta = mapper.readValue(metaJson, Asset3DUploadRequest.class);
        return service.upload(file, meta);
    }

    // Gọi Meshy sinh model → tải về → Firebase → Asset3D (blocking theo thiết kế hiện tại)
    @PostMapping("/generate")
    public Asset3DResponse generate(@RequestBody @Valid Asset3DGenerateRequest req) {
        return service.generate(req);
    }

    @DeleteMapping("/{id}") public void softDelete(@PathVariable @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id){
        service.softDelete(id);
    }

    // Search chung
    @GetMapping("/search")
    public Page<Asset3DResponse> search(
            @RequestParam(required=false)
            @Size(max=50) String markerId,
            @RequestParam(required=false)
            @Size(max=50) String userId,
            @RequestParam(required=false)
            @Size(max=10)  String format,
            @ParameterObject
            @PageableDefault(size=20) Pageable pageable){
        return service.search(markerId, userId, format, pageable);
    }

    // Endpoint Unity: lấy theo markerCode
    @GetMapping("/by-marker-code/{code}")
    public Page<Asset3DResponse> byMarkerCode(
            @PathVariable("code") @Size(max=50) String markerCode,
            @ParameterObject @PageableDefault(size=10,
                    sort="createdAt", direction= Sort.Direction.DESC) Pageable pageable){
        return service.searchByMarkerCode(markerCode, pageable);
    }

    // Lấy N mới nhất theo markerId
    @GetMapping("/latest")
    public List<Asset3DResponse> latest(@RequestParam @Size(max=50) String markerId,
                                        @RequestParam(defaultValue="3") @Min(1) @Max(20) int limit){
        return service.latestByMarker(markerId, limit);
    }
}

