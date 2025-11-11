package com.sep.arservice.dto;

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

    @Size(max=300)
    private String imageUrl;
}
