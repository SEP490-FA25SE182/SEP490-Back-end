package com.sep.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TtsGenerateRequest {
    @NotBlank
    private String text;          // nội dung đọc
    @NotBlank private String voiceName;     // ví dụ: "Kore" (từ danh sách prebuilt voices)
    private String title;                   // để lưu Audio.title
    private String language;                // ví dụ "en"
    private String format = "wav";          // "wav" | "pcm" | "mp3" (output là .wav)
    private String model = "gemini-2.5-flash-preview-tts"; // theo docs AI Studio
}
