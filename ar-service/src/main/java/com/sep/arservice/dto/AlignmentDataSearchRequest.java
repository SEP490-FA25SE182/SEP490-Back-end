package com.sep.arservice.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class AlignmentDataSearchRequest {
    private String markerId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant from;   // inclusive

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant to;     // inclusive
}
