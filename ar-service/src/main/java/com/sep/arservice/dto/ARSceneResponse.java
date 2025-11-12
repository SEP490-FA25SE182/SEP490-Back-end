package com.sep.arservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ARSceneResponse {
    String sceneId;
    String markerId;
    String name;
    String description;
    Integer version;
    String status;
    Instant createdAt;
}
