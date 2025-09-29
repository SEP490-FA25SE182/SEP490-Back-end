package com.sep.rookieservice.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
public class BookRequestDTO {
    @Size(max = 50)
    private String bookName;

    @Size(max = 100)
    private String coverUrl;

    @Size(max = 250)
    private String decription;

    @Size(max = 50)
    private String authorId;

    @Size(max = 50)
    private String bookshelveId;

    // Use Byte for enums stored as byte in entity (nullable)
    private Byte progressStatus;
    private Byte publicationStatus;

    private Instant publishedDate;
}
