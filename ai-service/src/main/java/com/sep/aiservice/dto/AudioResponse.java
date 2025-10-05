package com.sep.aiservice.dto;

import com.sep.aiservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class AudioResponse {
    private String audioId;
    private String audioUrl;
    private String voice;
    private String format;
    private String language;
    private Double durationMs;
    private String title;
    private Instant updatedAt;
    private IsActived isActived;
}

