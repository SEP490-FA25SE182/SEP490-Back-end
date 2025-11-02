package com.sep.aiservice.controller;

import com.sep.aiservice.dto.GenerateIllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import com.sep.aiservice.service.IllustrationGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rookie/illustrations/generate")
@RequiredArgsConstructor
@Validated
public class IllustrationGenerateController {

    private final IllustrationGenerationService generationService;

    @PostMapping(value="/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IllustrationResponse generate(
            @RequestPart("meta") String metaJson,
            @RequestPart(value="controlImage", required=false) MultipartFile controlImage,
            @RequestHeader(value="X-User-Id", required=false) String userId
    ) throws Exception {
        GenerateIllustrationRequest meta =
                new com.fasterxml.jackson.databind.ObjectMapper().readValue(metaJson, GenerateIllustrationRequest.class);
        return generationService.generateSync(meta, controlImage, userId);
    }
}
