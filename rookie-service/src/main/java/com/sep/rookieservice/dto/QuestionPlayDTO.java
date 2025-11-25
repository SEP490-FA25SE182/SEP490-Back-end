package com.sep.rookieservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionPlayDTO {
    private String questionId;
    private String content;
    private Integer score;
    private Integer answerCount;
    private List<AnswerPlayDTO> answers;
}