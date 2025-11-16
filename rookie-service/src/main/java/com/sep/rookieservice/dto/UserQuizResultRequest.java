package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

@Data
public class UserQuizResultRequest {
    private Integer score;
    private Integer number;
    private Boolean isComplete;
    private Boolean isReward;
    private Integer coin;
    private IsActived isActived;
    private String quizId;
    private String userId;
}
