package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;
import java.time.Instant;

@Data
public class QuizResponseDTO {
    private String quizId;
    private String title;
    private int totalScore;
    private int attemptCount;
    private int questionCount;
    private String chapterId;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
