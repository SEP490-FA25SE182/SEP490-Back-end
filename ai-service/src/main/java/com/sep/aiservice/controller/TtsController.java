package com.sep.aiservice.controller;

import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.dto.TtsGenerateRequest;
import com.sep.aiservice.service.TextToSpeechService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/audios/tts")
@RequiredArgsConstructor
public class TtsController {
    private final TextToSpeechService ttsService;

    @PostMapping
    public AudioResponse generate(@Valid @RequestBody TtsGenerateRequest req,
                                  @RequestHeader("X-User-Id") String userId) {
        return ttsService.synthesize(req, userId);
    }
}

