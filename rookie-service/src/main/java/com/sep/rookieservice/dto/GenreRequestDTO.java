package com.sep.rookieservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreRequestDTO {
    @NotBlank
    private String genreName;
    private String description;
}
