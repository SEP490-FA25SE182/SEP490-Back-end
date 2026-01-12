package com.sep.aiservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "moderation")
public class ModerationProperties {

    private String forbiddenWordsViResource = "classpath:forbidden-words.vi.txt";
    private String forbiddenWordsEnResource = "classpath:forbidden-words.en.txt";

    private Plagiarism plagiarism = new Plagiarism();
    private Ai ai = new Ai();

    @Data
    public static class Plagiarism {
        private int shingleSize = 5;
        private int maxCandidates = 2000;
        private int topK = 5;
        private double flagThreshold = 0.35; // >= ngưỡng thì gắn cờ "nghi vấn"
        private int maxContentCharsPerCandidate = 8000; // tránh payload quá lớn
    }

    @Data
    public static class Ai {
        private boolean enabled = true;
        private String modelEndpoint = "/v1beta/models/gemini-2.0-flash:generateContent";
        private int maxInputChars = 12000;
    }
}