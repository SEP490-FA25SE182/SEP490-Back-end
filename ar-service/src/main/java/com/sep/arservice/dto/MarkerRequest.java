package com.sep.arservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkerRequest {
    @NotBlank
    @Size(max=50)
    private String markerCode;

    @Size(max=50)
    private String markerType;

    private String imageUrl;

    @DecimalMin(value = "0.001", message = "physicalWidthM must be > 0")
    private Double physicalWidthM; // optional

    private String printablePdfUrl;

    private String userId;
}
