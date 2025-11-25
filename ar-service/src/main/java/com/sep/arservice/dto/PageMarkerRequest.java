package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PageMarkerRequest {
    @NotBlank
    @Size(max=50)
    private String pageId;

    @NotBlank
    @Size(max=50)
    private String markerId;
}
