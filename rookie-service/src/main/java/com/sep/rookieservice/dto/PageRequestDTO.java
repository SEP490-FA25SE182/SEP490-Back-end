package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageRequestDTO {

    @NotNull
    private Integer pageNumber;

    @NotBlank
    private String content;

    @NotBlank
    private String chapterId;

    private IsActived isActived;
}
