package com.sep.storydiffusionservice.dto;

import java.time.Instant;

public class IllustrationDto {
    private String illustrationId;
    private String prompt;
    private String imageUrl;
    private String style;
    private String generator;
    private Instant createdAt;
}
