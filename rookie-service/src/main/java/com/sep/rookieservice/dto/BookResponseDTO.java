package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class BookResponseDTO implements Serializable {
    private String bookId;
    private String bookName;
    private String coverUrl;
    private String decription;
    private String authorId;

    private BigDecimal price;

    private Integer quantity;

    private Byte progressStatus;
    private Byte publicationStatus;
    private IsActived isActived;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedDate;
    private String review;
}
