package com.sep.aiservice.controller;

import com.sep.aiservice.dto.AudioRequest;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.service.AudioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/audios")
@RequiredArgsConstructor
@Validated
public class AudioController {

    private final AudioService audioService;

    @GetMapping
    public List<AudioResponse> getAudios() {
        return audioService.getAll();
    }

    @GetMapping("/{id}")
    public AudioResponse getAudio(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id) {
        return audioService.getById(id);
    }

    // GET
    @GetMapping("/title/{title}")
    public AudioResponse getByTitle(
            @PathVariable @Size(max = 50) String title) {
        return audioService.getByTitle(title);
    }

    // CREATE
    @PostMapping
    public List<AudioResponse> createAudios(@RequestBody @Valid List<AudioRequest> requests) {
        return audioService.create(requests);
    }

    // UPDATE
    @PutMapping("/{id}")
    public AudioResponse updateAudio(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid AudioRequest request) {
        return audioService.update(id, request);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public void deleteAudio(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        audioService.softDelete(id);
    }

    // SEARCH
    @GetMapping("/search")
    public Page<AudioResponse> search(
            @RequestParam(required = false) @Size(max = 50) String voice,
            @RequestParam(required = false) @Size(max = 10) String format,
            @RequestParam(required = false) @Size(max = 10) String language,
            @RequestParam(required = false) @Size(max = 50) String title,
            @RequestParam(required = false) IsActived isActived,
            @RequestParam(required = false) @Size(max = 50) String userId,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return audioService.search(voice, format, language, title, isActived, userId, pageable);
    }
}
