package com.sep.aiservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.aiservice.config.ModerationProperties;
import com.sep.aiservice.dto.AiModerationResultDTO;
import com.sep.aiservice.service.ModerationAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ModerationAiServiceImpl implements ModerationAiService {

    private final ModerationProperties props;
    private final ObjectMapper objectMapper;

    @Qualifier("geminiWebClient")
    private final WebClient geminiClient;

    @Override
    public AiModerationResultDTO analyze(String content, int forbiddenCount, double maxSimilarity) {
        if (!props.getAi().isEnabled()) {
            return unknown();
        }

        String clipped = clip(content, props.getAi().getMaxInputChars());

        String prompt = """
                Bạn là hệ thống hỗ trợ MODERATOR kiểm duyệt nội dung sách.
                Chỉ trả về JSON HỢP LỆ (không markdown, không giải thích) theo schema:

                {
                  "riskLevel": "LOW|MEDIUM|HIGH",
                  "action": "APPROVE|REVIEW|REJECT",
                  "reasons": ["..."]
                }

                Số liệu:
                - forbiddenCount: %d
                - maxSimilarity: %.4f

                Nội dung:
                %s
                """.formatted(forbiddenCount, maxSimilarity, clipped);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = geminiClient.post()
                    .uri(props.getAi().getModelEndpoint())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String text = extractGeminiText(result);
            if (text == null || text.isBlank()) return unknown();

            String json = extractJsonObject(text);
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = objectMapper.readValue(json, Map.class);

            String risk = str(parsed.get("riskLevel"));
            String action = str(parsed.get("action"));
            List<String> reasons = toStringList(parsed.get("reasons"));

            if (risk == null || action == null) return unknown();

            return AiModerationResultDTO.builder()
                    .riskLevel(risk)
                    .action(action)
                    .reasons(reasons == null ? List.of() : reasons)
                    .build();

        } catch (Exception e) {
            return unknown();
        }
    }

    private AiModerationResultDTO unknown() {
        return AiModerationResultDTO.builder()
                .riskLevel("UNKNOWN")
                .action("UNKNOWN")
                .reasons(List.of())
                .build();
    }

    private String clip(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max);
    }

    private String extractGeminiText(Map<String, Object> result) {
        if (result == null) return null;
        try {
            Object candidates = result.get("candidates");
            if (!(candidates instanceof List<?> cList) || cList.isEmpty()) return null;

            Object c0 = cList.get(0);
            if (!(c0 instanceof Map<?, ?> c0m)) return null;

            Object content = c0m.get("content");
            if (!(content instanceof Map<?, ?> cm)) return null;

            Object parts = cm.get("parts");
            if (!(parts instanceof List<?> pList) || pList.isEmpty()) return null;

            Object p0 = pList.get(0);
            if (!(p0 instanceof Map<?, ?> p0m)) return null;

            Object text = p0m.get("text");
            return text != null ? text.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractJsonObject(String s) {
        int a = s.indexOf('{');
        int b = s.lastIndexOf('}');
        if (a >= 0 && b > a) return s.substring(a, b + 1);
        return s;
    }

    private String str(Object o) {
        if (o == null) return null;
        String v = o.toString().trim();
        return v.isBlank() ? null : v;
    }

    private List<String> toStringList(Object o) {
        if (!(o instanceof List<?> list)) return null;
        List<String> out = new ArrayList<>();
        for (Object x : list) if (x != null) out.add(x.toString());
        return out;
    }
}