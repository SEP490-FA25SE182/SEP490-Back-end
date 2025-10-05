package com.sep.aiservice.dto;

import com.sep.aiservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class IllustrationResponse {
    private String illustrationId;
    private String imageUrl;
    private String style;
    private String format;
    private Integer width;
    private Integer height;
    private String title;
    private Instant updatedAt;
    private IsActived isActived;
}
