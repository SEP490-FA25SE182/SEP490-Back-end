package com.sep.aiservice.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AudioUploadRequest {

    @Size(max = 50)
    private String title;

    @Size(max = 10)
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$",
            message = "Language must be like 'en', 'en-US', 'vi'")
    private String language;
}


