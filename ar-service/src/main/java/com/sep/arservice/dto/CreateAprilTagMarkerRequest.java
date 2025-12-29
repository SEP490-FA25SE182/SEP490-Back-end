package com.sep.arservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAprilTagMarkerRequest {

    @NotBlank
    @Size(max = 50)
    private String bookId;

    @Size(max = 50)
    private String userId;

    @DecimalMin(value = "0.001", message = "physicalWidthM must be > 0")
    private Double physicalWidthM;

    @Size(max = 50)
    private String tagFamily; // (default tag36h11)

    @Size(max = 100)
    private String markerCode;
}


