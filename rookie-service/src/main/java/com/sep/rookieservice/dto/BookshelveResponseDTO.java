package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class BookshelveResponseDTO {
    private String bookshelveId;
    private String bookshelveName;
    private String decription;
    private String userId;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
