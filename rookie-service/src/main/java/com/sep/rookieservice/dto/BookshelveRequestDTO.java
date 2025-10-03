package com.sep.rookieservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookshelveRequestDTO {
    @NotBlank
    @Size(max = 50)
    private String bookshelveName;

    @Size(max = 250)
    private String decription;

    @NotBlank
    private String userId;
}
