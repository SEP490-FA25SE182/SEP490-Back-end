package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

@Data
public class UserQuizResultRequest {
    private Integer score;
    private int attemptCount;
    private int correctCount;
    private int questionCount;
    private Boolean isComplete;
    private Boolean isReward;
    private Integer coin;
    private IsActived isActived;
    private String quizId;
    private String userId;
}
