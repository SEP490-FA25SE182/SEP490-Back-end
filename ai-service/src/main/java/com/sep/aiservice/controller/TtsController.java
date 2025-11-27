package com.sep.aiservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.dto.AudioUploadRequest;
import com.sep.aiservice.dto.TtsGenerateRequest;
import com.sep.aiservice.service.TextToSpeechService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/rookie/audios/tts")
@RequiredArgsConstructor
public class TtsController {
    private final TextToSpeechService ttsService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public AudioResponse generate(@Valid @RequestBody TtsGenerateRequest req,
                                  @RequestHeader("X-User-Id") String userId) {
        return ttsService.synthesize(req, userId);
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AudioResponse upload(@RequestPart("file") MultipartFile file,
                                @RequestPart("meta") String metaJson,
                                @RequestHeader("X-User-Id") String userId) throws IOException {

        AudioUploadRequest meta = objectMapper.readValue(metaJson, AudioUploadRequest.class);
        return ttsService.uploadAudio(file, meta, userId);
    }
}

