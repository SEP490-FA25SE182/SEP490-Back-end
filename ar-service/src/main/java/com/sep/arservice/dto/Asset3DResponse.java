package com.sep.arservice.dto;

import com.sep.arservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class Asset3DResponse {
    private String asset3DId;
    private String markerId;
    private String userId;
    private String assetUrl;
    private String thumbUrl;
    private String source;
    private String prompt;
    private String fileName;
    private String format;
    private int polycount;
    private long fileSize;
    private Float scale;
    private Instant createdAt;
    private IsActived isActived;
}
