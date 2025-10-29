package com.sep.rookieservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class BookRequestDTO implements Serializable {

    @Size(max = 50)
    @NotNull
    private String bookName;

    @Size(max = 100)
    private String coverUrl;

    @Size(max = 250)
    private String decription;

    @Size(max = 50)
    private String authorId;

    private BigDecimal price;

    private Byte progressStatus;
    private Byte publicationStatus;

    private Instant publishedDate;
}
