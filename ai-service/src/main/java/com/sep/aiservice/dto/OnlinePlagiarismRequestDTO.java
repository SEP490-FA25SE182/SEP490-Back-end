package com.sep.aiservice.dto;

import lombok.Data;

@Data
public class OnlinePlagiarismRequestDTO {
    private String content;
    private String language;
    private int maxSources = 5;
}