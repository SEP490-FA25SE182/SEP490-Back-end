package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.FeedbackStatus;
import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class FeedbackResponseDTO {
    private String feedbackId;
    private String content;
    private byte rating;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
    private String userId;
    private String bookId;
    private String orderDetailId;
    private List<String> imageUrls;
    private FeedbackStatus status;
}
