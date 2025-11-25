package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class AnswerPlayDTO {
    private String answerId;
    private String content;
    private Boolean isCorrect;
}