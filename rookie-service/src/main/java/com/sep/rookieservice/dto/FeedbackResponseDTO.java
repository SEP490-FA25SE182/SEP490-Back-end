package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;
import java.time.Instant;

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
}
