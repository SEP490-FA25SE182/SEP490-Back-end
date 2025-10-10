package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogImageRequest {
    @NotBlank
    @Size(max = 500)
    private String imageUrl;

    @Size(max = 255)
    private String altText;

    @Min(0)
    private Integer position;

    @NotBlank
    @Size(max = 50)
    private String blogId;

    private IsActived isActived;
}
