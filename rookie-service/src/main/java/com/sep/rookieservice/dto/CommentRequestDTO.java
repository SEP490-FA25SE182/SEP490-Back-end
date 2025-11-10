package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;
import java.time.Instant;

@Data
public class CommentRequestDTO {
    private String content;
    private String name;
    private Boolean isPublished;
    private String userId;
    private String blogId;
    private IsActived isActived = IsActived.ACTIVE;
}
