package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;
import java.time.Instant;

@Data
public class NotificationResponseDTO {
    private String notificationId;
    private String userId;
    private String bookId;
    private String orderId;
    private String message;
    private String title;
    private Boolean isRead;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
