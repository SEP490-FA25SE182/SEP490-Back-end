package com.sep.googlespeechservice.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audios")
public class Audio implements Serializable {
    @Id
    @Size(max = 50)
    private String audioId;

    @Size(max = 1000)
    @Field("text_input")
    private String textInput;

    @Size(max = 1000)
    @Field("audio_url")
    private String audioUrl;

    @Size(max = 50)
    @Field("asset_type")
    private String assetType;

    @Size(max = 100)
    @Field("voice")
    private String voice;

    @Size(max = 50)
    @Field("language")
    private String language;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
}