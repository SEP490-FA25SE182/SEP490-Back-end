package com.sep.aiservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequestDTO {
    private String sessionId;
    private String message;
    private List<String> imageUrls;
    private List<String> fileUrls;
}
