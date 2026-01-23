package com.sep.aiservice.dto;

import lombok.Data;

@Data
public class OnlinePlagiarismSourceDTO {

    private String title;
    private String url;
    private double similarity;
}
