package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;
import java.time.Instant;

@Data
public class QuizRequestDTO {
    private String title;
    private int totalScore;
    private int number;
    private String chapterId;
    private IsActived isActived;
}

