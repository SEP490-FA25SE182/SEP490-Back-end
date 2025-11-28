package com.sep.aiservice.dto;

import com.sep.aiservice.enums.IsActived;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AudioRequest {

    private String audioUrl;

    @Size(max = 50)
    private String voice;

    @Size(max = 10)
    @Pattern(regexp = "^(?i)(mp3|wav|ogg|m4a|flac)$",
            message = "Format must be one of: mp3/wav/ogg/m4a/flac")
    private String format;

    @Size(max = 10)
    // ví dụ: en, en-US, vi, ja-JP
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$",
            message = "Language must be like 'en', 'en-US', 'vi'")
    private String language;

    @Positive(message = "durationMs must be > 0")
    private Double durationMs;

    private String title;

    private IsActived isActived;

    private String userId;
}
