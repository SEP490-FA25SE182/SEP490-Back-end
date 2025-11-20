package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.FeedbackStatus;
import lombok.Data;

@Data
public class UpdateFeedbackStatusRequest {
    private FeedbackStatus status;
}
