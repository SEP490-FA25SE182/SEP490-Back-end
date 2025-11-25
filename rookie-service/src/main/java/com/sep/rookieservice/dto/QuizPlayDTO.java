package com.sep.rookieservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizPlayDTO {
    private String quizId;
    private String title;
    private Integer totalScore;
    private Integer questionCount;
    private List<QuestionPlayDTO> questions;
}
