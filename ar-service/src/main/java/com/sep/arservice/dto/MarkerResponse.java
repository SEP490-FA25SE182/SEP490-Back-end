package com.sep.arservice.dto;

import com.sep.arservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class MarkerResponse {
    private String markerId;
    private String markerCode;
    private String markerType;
    private String imageUrl;
    private Double physicalWidthM;
    private String printablePdfUrl;
    private String userId;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
