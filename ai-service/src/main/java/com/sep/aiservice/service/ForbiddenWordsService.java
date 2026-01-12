package com.sep.aiservice.service;

import com.sep.aiservice.dto.ForbiddenWordMatchDTO;

import java.util.List;

public interface ForbiddenWordsService {
    List<ForbiddenWordMatchDTO> scan(String content, String language);
}