package com.sep.storydiffusionservice.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "illustrations")
public class Illustration implements Serializable {
    @Id
    @Size(max = 50)
    private String illustrationId;

    @Size(max = 1000)
    @Field("prompt")
    private String prompt;

    @Size(max = 1000)
    @Field("image_url")
    private String imageUrl;

    @Size(max = 50)
    @Field("style")
    private String style;

    @Size(max = 10)
    @Field("generator")
    private String generator;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
}
