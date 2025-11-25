package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDTO {
    private String questionId;
    private String quizId;
    private String content;
    private int score;
    private int answerCount;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
