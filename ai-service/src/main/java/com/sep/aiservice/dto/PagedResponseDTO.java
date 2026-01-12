package com.sep.aiservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponseDTO<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
}