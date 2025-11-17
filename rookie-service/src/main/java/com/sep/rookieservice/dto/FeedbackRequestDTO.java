package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.FeedbackStatus;
import lombok.Data;

import java.util.List;

@Data
public class FeedbackRequestDTO {
    private String content;
    private byte rating;
    private String userId;
    private String bookId;
    private String orderDetailId;

    private List<String> imageUrls;
    private FeedbackStatus status;
}
