package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponseDTO {
    private String sessionId;
    private String answer;
}
