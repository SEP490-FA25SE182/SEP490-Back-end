package com.sep.aiservice.service.impl;

import com.sep.aiservice.config.ModerationProperties;
import com.sep.aiservice.dto.PagedResponseDTO;
import com.sep.aiservice.dto.PlagiarismHitDTO;
import com.sep.aiservice.dto.RookiePageResponseDTO;
import com.sep.aiservice.gateway.RookiePageGateway;
import com.sep.aiservice.service.PlagiarismService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlagiarismServiceImpl implements PlagiarismService {

    private final ModerationProperties props;
    private final RookiePageGateway rookiePageGateway;

    @Override
    public List<PlagiarismHitDTO> scanAgainstActivePages(String content, String excludePageId) {
        if (content == null || content.isBlank()) return List.of();

        int maxCandidates = props.getPlagiarism().getMaxCandidates();
        int shingleSize = props.getPlagiarism().getShingleSize();
        int topK = props.getPlagiarism().getTopK();
        int capChars = props.getPlagiarism().getMaxContentCharsPerCandidate();

        Set<Integer> source = shinglesHashSet(content, shingleSize);
        if (source.isEmpty()) return List.of();

        List<RookiePageResponseDTO> candidates = new ArrayList<>();
        int page = 0;
        int size = Math.min(200, maxCandidates);

        while (candidates.size() < maxCandidates) {
            PagedResponseDTO<RookiePageResponseDTO> res = rookiePageGateway.listPages(
                    page, size,
                    null,
                    null, null, null,
                    "ACTIVE"
            );
            if (res == null || res.getContent() == null || res.getContent().isEmpty()) break;

            for (RookiePageResponseDTO dto : res.getContent()) {
                if (dto == null) continue;
                if (excludePageId != null && excludePageId.equals(dto.getPageId())) continue;

                String cand = dto.getContent();
                if (cand == null || cand.isBlank()) continue;
                if (looksLikeImage(cand)) continue;

                if (cand.length() > capChars) cand = cand.substring(0, capChars);
                dto.setContent(cand);

                candidates.add(dto);
                if (candidates.size() >= maxCandidates) break;
            }

            page++;
            if (res.getTotalPages() > 0 && page >= res.getTotalPages()) break;
        }

        PriorityQueue<PlagiarismHitDTO> pq = new PriorityQueue<>(Comparator.comparingDouble(PlagiarismHitDTO::getSimilarity));

        for (RookiePageResponseDTO cand : candidates) {
            Set<Integer> target = shinglesHashSet(cand.getContent(), shingleSize);
            if (target.isEmpty()) continue;

            double sim = jaccard(source, target);
            if (sim <= 0) continue;

            String snippet = findSnippet(content, cand.getContent(), shingleSize);

            pq.offer(PlagiarismHitDTO.builder()
                    .sourceType("PAGE")
                    .sourceId(cand.getPageId())
                    .similarity(sim)
                    .snippet(snippet)
                    .build());

            if (pq.size() > topK) pq.poll();
        }

        List<PlagiarismHitDTO> out = new ArrayList<>(pq);
        out.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
        return out;
    }

    private boolean looksLikeImage(String content) {
        String c = content.toLowerCase(Locale.ROOT);
        return c.contains("firebasestorage.googleapis.com") || c.startsWith("gs://");
    }

    private Set<Integer> shinglesHashSet(String text, int k) {
        List<String> tokens = tokenize(text);
        if (tokens.size() < k) return Set.of();

        Set<Integer> set = new HashSet<>(Math.max(64, tokens.size()));
        for (int i = 0; i + k <= tokens.size(); i++) {
            String phrase = String.join(" ", tokens.subList(i, i + k));
            set.add(phrase.hashCode());
        }
        return set;
    }

    private List<String> tokenize(String text) {
        String norm = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}\\s]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (norm.isBlank()) return List.of();
        return Arrays.asList(norm.split(" "));
    }

    private double jaccard(Set<Integer> a, Set<Integer> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        Set<Integer> small = a.size() <= b.size() ? a : b;
        Set<Integer> big = a.size() <= b.size() ? b : a;

        int inter = 0;
        for (Integer x : small) if (big.contains(x)) inter++;

        int uni = a.size() + b.size() - inter;
        return uni == 0 ? 0.0 : (double) inter / (double) uni;
    }

    private String findSnippet(String a, String b, int k) {
        List<String> ta = tokenize(a);
        List<String> tb = tokenize(b);
        if (ta.size() < k || tb.size() < k) return null;

        Set<Integer> hb = new HashSet<>();
        Map<Integer, String> phraseByHash = new HashMap<>();

        for (int i = 0; i + k <= tb.size(); i++) {
            String phrase = String.join(" ", tb.subList(i, i + k));
            int h = phrase.hashCode();
            hb.add(h);
            phraseByHash.putIfAbsent(h, phrase);
        }

        for (int i = 0; i + k <= ta.size(); i++) {
            String phrase = String.join(" ", ta.subList(i, i + k));
            int h = phrase.hashCode();
            if (hb.contains(h)) return phraseByHash.getOrDefault(h, phrase);
        }

        return null;
    }
}