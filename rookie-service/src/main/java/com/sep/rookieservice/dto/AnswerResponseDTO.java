package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class AnswerResponseDTO {
    private String answerId;
    private String content;
    private Boolean isCorrect;
    private String questionId;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
