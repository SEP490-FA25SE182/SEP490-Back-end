package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
    private String userId;
    private String bookId;
    private String orderId;
    private String message;
    private String title;
    private Boolean isRead;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
