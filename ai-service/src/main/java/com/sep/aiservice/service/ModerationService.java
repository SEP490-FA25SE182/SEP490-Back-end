package com.sep.aiservice.service;

import com.sep.aiservice.dto.ModerationScanRequestDTO;
import com.sep.aiservice.dto.ModerationScanResponseDTO;

public interface ModerationService {
    ModerationScanResponseDTO scan(ModerationScanRequestDTO req);
}