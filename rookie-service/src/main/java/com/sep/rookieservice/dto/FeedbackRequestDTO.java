package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private String content;
    private byte rating;
    private String userId;
    private String bookId;
    private String orderDetailId;
}
