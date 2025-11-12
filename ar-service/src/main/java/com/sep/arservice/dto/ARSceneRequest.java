package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ARSceneRequest {
    @NotBlank
    @Size(max=50)
    String markerId;

    @Size(max=100)
    String name;

    @Size(max=500)
    String description;

    Integer version;           // optional
    String status;             // DRAFT/PUBLISHED
}
