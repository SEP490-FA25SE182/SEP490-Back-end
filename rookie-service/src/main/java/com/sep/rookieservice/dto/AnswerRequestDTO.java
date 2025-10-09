package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

@Data
public class AnswerRequestDTO {
    private String content;
    private Boolean isCorrect;
    private String questionId;
    private IsActived isActived;
}
