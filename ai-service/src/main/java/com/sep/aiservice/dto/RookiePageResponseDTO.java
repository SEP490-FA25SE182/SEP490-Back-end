package com.sep.aiservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RookiePageResponseDTO {
    private String pageId;
    private String chapterId;
    private Integer pageNumber;
    private String content;
    private String pageType;
}