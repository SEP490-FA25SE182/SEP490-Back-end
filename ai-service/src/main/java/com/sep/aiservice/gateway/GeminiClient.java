package com.sep.aiservice.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate;

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${moderation.ai.model-endpoint}")
    private String modelEndpoint;

    public String generateContent(String prompt, boolean enableSearch) {
        try {
            Map<String, Object> payload = new HashMap<>();

            payload.put("contents", List.of(
                    Map.of("parts", List.of(
                            Map.of("text", prompt)
                    ))
            ));

            if (enableSearch) {
                payload.put("tools", List.of(
                        Map.of("google_search", Map.of())
                ));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(payload, headers);

            String url = baseUrl + modelEndpoint + "?key=" + apiKey;

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return extractText(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Gemini API call failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> body) {
        if (body == null) return "";

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) body.get("candidates");

        if (candidates == null || candidates.isEmpty()) return "";

        Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) return "";

        Object text = parts.get(0).get("text");
        return text == null ? "" : text.toString();
    }
}
