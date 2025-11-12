package com.sep.arservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class MeshyStatusRes {
    String status;
    String preview_image;
    Integer face_count;
    String error;
    String id;                  // e.g. PENDING, IN_PROGRESS, SUCCEEDED, FAILED
    @JsonProperty("model_urls")
    Map<String, String> model_urls;       // e.g. { "glb": "https://...model.glb", "fbx": "..." }
}

