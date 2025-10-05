package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class PageResponseDTO {

    private String pageId;
    private String chapterId;
    private Integer pageNumber;
    private String content;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
