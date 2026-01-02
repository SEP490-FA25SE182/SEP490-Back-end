package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ChatResponseDTO {
    private String sessionId;
    private String content;
    private Instant createdAt;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> fileUrls = new ArrayList<>();
    private String role;
}
