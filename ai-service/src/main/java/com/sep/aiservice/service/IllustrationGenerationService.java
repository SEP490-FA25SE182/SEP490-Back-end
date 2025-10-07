package com.sep.aiservice.service;

import com.sep.aiservice.dto.GenerateIllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface IllustrationGenerationService {
    IllustrationResponse generateSync(GenerateIllustrationRequest req,
                                      @Nullable MultipartFile controlImage,
                                      @Nullable String userId);
}
