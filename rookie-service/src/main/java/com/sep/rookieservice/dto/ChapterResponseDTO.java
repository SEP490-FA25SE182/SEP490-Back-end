package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class ChapterResponseDTO {
    private String chapterId;
    private String chapterName;
    private int chapterNumber;
    private String decription;
    private String review;
    private Instant publishedDate;
    private Byte progressStatus;
    private String bookId;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
