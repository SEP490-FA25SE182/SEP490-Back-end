package com.sep.arservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MarkerResponse {
    private String markerId;
    private String markerCode;
    private String markerType;
    private String imageUrl;
    private Instant createdAt;
}
