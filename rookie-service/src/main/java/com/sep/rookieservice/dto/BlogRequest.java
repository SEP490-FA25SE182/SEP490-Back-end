package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class BlogRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 10000)
    private String content;

    @Size(max = 50)
    private String authorId;

    @Size(max = 50)
    private String bookId;

    private IsActived isActived;

    private Set<String> tagIds;
}
