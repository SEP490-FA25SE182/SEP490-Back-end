package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class BookResponseDTO {
    private String bookId;
    private String bookName;
    private String coverUrl;
    private String decription;
    private String authorId;
    private String bookshelveId;
    private Byte progressStatus;
    private Byte publicationStatus;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedDate;
}
