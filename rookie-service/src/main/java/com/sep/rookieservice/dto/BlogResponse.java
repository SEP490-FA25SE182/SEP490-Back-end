package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class BlogResponse {
    private String blogId;
    private String coverUrl;
    private String title;
    private String content;
    private String authorId;
    private String bookId;
    private Instant updatedAt;
    private IsActived isActived;
    private Set<String> tagIds;
    private Set<String> tagNames;
}
