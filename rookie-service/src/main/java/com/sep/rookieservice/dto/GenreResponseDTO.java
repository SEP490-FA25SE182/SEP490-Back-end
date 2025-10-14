package com.sep.rookieservice.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class GenreResponseDTO {
    private String genreId;
    private String genreName;
    private String description;
    private Instant createdAt;
}
