package com.sep.aiservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.aiservice.dto.OnlinePlagiarismRequestDTO;
import com.sep.aiservice.dto.OnlinePlagiarismResultDTO;
import com.sep.aiservice.gateway.GeminiClient;
import com.sep.aiservice.service.OnlinePlagiarismService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnlinePlagiarismServiceImpl implements OnlinePlagiarismService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Value("${moderation.plagiarism.flag-threshold}")
    private double flagThreshold;

    @Value("${moderation.ai.max-input-chars}")
    private int maxInputChars;

    @Override
    public OnlinePlagiarismResultDTO scan(OnlinePlagiarismRequestDTO request) {
        try {
            String content = trimContent(request.getContent());
            String prompt = buildPrompt(content, request);

            String raw = geminiClient.generateContent(prompt, true);

            OnlinePlagiarismResultDTO result = parseResponse(raw);

            if (result != null) {
                result.setPlagiarismFlag(
                        result.getMaxSimilarity() >= flagThreshold
                );
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Online plagiarism scan failed", e);
        }
    }

    private String trimContent(String content) {
        if (content == null) return "";
        return content.length() > maxInputChars
                ? content.substring(0, maxInputChars)
                : content;
    }

    private String buildPrompt(String content, OnlinePlagiarismRequestDTO req) {
        return """
        You are an AI system for ONLINE plagiarism detection.

        RULES:
        - Search public internet sources using Google Search tool.
        - Compare semantic meaning, structure, and phrasing.
        - Ignore internal databases.
        - Only return sources with similarity >= 0.2.
        - Similarity scale: 0.0 to 1.0.

        LANGUAGE: %s
        MAX SOURCES: %d

        CONTENT:
        ---
        %s
        ---

        RETURN STRICT JSON ONLY:
        {
          "plagiarismFlag": boolean,
          "maxSimilarity": number,
          "sources": [
            {
              "url": string,
              "title": string,
              "similarity": number,
              "matchedSnippet": string
            }
          ]
        }
        """
                .formatted(
                        req.getLanguage(),
                        req.getMaxSources(),
                        content
                );
    }

    private OnlinePlagiarismResultDTO parseResponse(String raw) {
        try {
            int start = raw.indexOf('{');
            int end = raw.lastIndexOf('}');

            if (start < 0 || end < 0) {
                throw new IllegalArgumentException("Invalid Gemini response");
            }

            String json = raw.substring(start, end + 1);

            return objectMapper.readValue(
                    json,
                    OnlinePlagiarismResultDTO.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini JSON", e);
        }
    }
}
