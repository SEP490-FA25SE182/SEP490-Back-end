package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class UserQuizResultResponse {
    private String resultId;
    private Integer score;
    private Integer number;
    private Boolean isComplete;
    private Boolean isReward;
    private Integer coin;
    private IsActived isActived;
    private String quizId;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;
}
