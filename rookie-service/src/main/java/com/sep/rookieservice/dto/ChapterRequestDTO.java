package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class ChapterRequestDTO {
    private String chapterName;
    private int chapterNumber;
    private String decription;
    private String bookId;
    private IsActived isActived = IsActived.ACTIVE;
}
