package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForbiddenWordMatchDTO {
    private String word;
    private int start;
    private int end;
    private String context;
}