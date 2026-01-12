package com.sep.aiservice.service;

import com.sep.aiservice.dto.PlagiarismHitDTO;

import java.util.List;

public interface PlagiarismService {
    List<PlagiarismHitDTO> scanAgainstActivePages(String content, String excludePageId);
}