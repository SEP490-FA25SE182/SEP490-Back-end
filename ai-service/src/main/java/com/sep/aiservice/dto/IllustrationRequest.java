package com.sep.aiservice.dto;

import com.sep.aiservice.enums.IsActived;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class IllustrationRequest {

    private String imageUrl;

    @Size(max = 50)
    private String style;

    @Size(max = 10)
    @Pattern(regexp = "^(?i)(png|jpg|jpeg|webp|gif)$",
            message = "Format must be one of: png/jpg/jpeg/webp/gif")
    private String format;

    @Min(value = 1, message = "Width must be >= 1")
    private Integer width;

    @Min(value = 1, message = "Height must be >= 1")
    private Integer height;

    private String title;

    private IsActived isActived;

    private String userId;
}
