package com.sep.aiservice.service.impl;

import com.sep.aiservice.config.ModerationProperties;
import com.sep.aiservice.dto.ForbiddenWordMatchDTO;
import com.sep.aiservice.service.ForbiddenWordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ForbiddenWordsServiceImpl implements ForbiddenWordsService {

    private final ModerationProperties props;
    private final ResourceLoader resourceLoader;

    private volatile List<String> cachedVi;
    private volatile List<String> cachedEn;

    @Override
    public List<ForbiddenWordMatchDTO> scan(String content, String language) {
        if (content == null || content.isBlank()) return List.of();

        List<String> words = getForbiddenWords(language);
        if (words.isEmpty()) return List.of();

        String lower = content.toLowerCase(Locale.ROOT);
        List<ForbiddenWordMatchDTO> matches = new ArrayList<>();

        for (String w : words) {
            String ww = w == null ? "" : w.trim().toLowerCase(Locale.ROOT);
            if (ww.isEmpty()) continue;

            Pattern p = Pattern.compile(Pattern.quote(ww), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(lower);

            while (m.find()) {
                int start = m.start();
                int end = m.end();
                matches.add(ForbiddenWordMatchDTO.builder()
                        .word(ww)
                        .start(start)
                        .end(end)
                        .context(extractContext(content, start, end, 40))
                        .build());
            }
        }

        matches.sort(Comparator.comparingInt(ForbiddenWordMatchDTO::getStart));
        return matches;
    }

    private String extractContext(String text, int start, int end, int radius) {
        int s = Math.max(0, start - radius);
        int e = Math.min(text.length(), end + radius);
        return text.substring(s, e).replaceAll("\\s+", " ").trim();
    }

    private List<String> getForbiddenWords(String language) {
        String lang = (language == null || language.isBlank()) ? "vi" : language.trim().toLowerCase(Locale.ROOT);
        return lang.startsWith("en") ? getEn() : getVi();
    }

    private List<String> getVi() {
        List<String> v = cachedVi;
        if (v != null) return v;
        synchronized (this) {
            if (cachedVi == null) cachedVi = loadWords(props.getForbiddenWordsViResource());
            return cachedVi;
        }
    }

    private List<String> getEn() {
        List<String> v = cachedEn;
        if (v != null) return v;
        synchronized (this) {
            if (cachedEn == null) cachedEn = loadWords(props.getForbiddenWordsEnResource());
            return cachedEn;
        }
    }

    private List<String> loadWords(String location) {
        try {
            Resource r = resourceLoader.getResource(location);
            if (!r.exists()) return List.of();

            String raw = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            return Arrays.stream(raw.split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .filter(s -> !s.startsWith("#"))
                    .distinct()
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}