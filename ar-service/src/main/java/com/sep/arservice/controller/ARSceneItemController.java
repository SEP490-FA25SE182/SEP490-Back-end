package com.sep.arservice.controller;

import com.sep.arservice.dto.ARSceneItemRequest;
import com.sep.arservice.dto.ARSceneItemResponse;
import com.sep.arservice.service.ARSceneItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/ar-scenes/ar-scene-items")
@RequiredArgsConstructor
@Validated
public class ARSceneItemController {

    private final ARSceneItemService service;

    @PostMapping
    public List<ARSceneItemResponse> createBatch(@RequestBody @Valid List<ARSceneItemRequest> reqs) {
        return service.createBatch(reqs);
    }

    @PutMapping("/{id}")
    public ARSceneItemResponse update(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id,
                                      @Valid @RequestBody ARSceneItemRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public ARSceneItemResponse getOne(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteHard(@PathVariable("id") @Pattern(regexp="^[0-9a-fA-F\\-]{36}$") String id) {
        service.deleteHard(id);
    }

    @GetMapping("/search")
    public Page<ARSceneItemResponse> search(
            @RequestParam(required = false)
            @Size(max = 50) String sceneId,
            @RequestParam(required = false)
            @Size(max = 50) String asset3dId,
            @ParameterObject
            @PageableDefault(size = 50, sort = "orderIndex", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.search(sceneId, asset3dId, pageable);
    }
}
