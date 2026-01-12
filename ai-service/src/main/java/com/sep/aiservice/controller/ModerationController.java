package com.sep.aiservice.controller;

import com.sep.aiservice.dto.ModerationScanRequestDTO;
import com.sep.aiservice.dto.ModerationScanResponseDTO;
import com.sep.aiservice.service.ModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @PostMapping("/scan")
    public ModerationScanResponseDTO scan(@Valid @RequestBody ModerationScanRequestDTO req) {
        return moderationService.scan(req);
    }
}