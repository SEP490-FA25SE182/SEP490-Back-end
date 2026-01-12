package com.sep.aiservice.service.impl;

import com.sep.aiservice.config.ModerationProperties;
import com.sep.aiservice.dto.*;
import com.sep.aiservice.service.ForbiddenWordsService;
import com.sep.aiservice.service.ModerationAiService;
import com.sep.aiservice.service.ModerationService;
import com.sep.aiservice.service.PlagiarismService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final ModerationProperties props;
    private final ForbiddenWordsService forbiddenWordsService;
    private final PlagiarismService plagiarismService;
    private final ModerationAiService moderationAiService;

    @Override
    public ModerationScanResponseDTO scan(ModerationScanRequestDTO req) {
        String lang = normalizeLang(req.getLanguage());

        List<ForbiddenWordMatchDTO> forbidden = forbiddenWordsService.scan(req.getContent(), lang);
        List<PlagiarismHitDTO> hits = plagiarismService.scanAgainstActivePages(req.getContent(), req.getEntityId());

        double maxSim = hits.stream().mapToDouble(PlagiarismHitDTO::getSimilarity).max().orElse(0.0);
        boolean flag = maxSim >= props.getPlagiarism().getFlagThreshold();

        AiModerationResultDTO ai = moderationAiService.analyze(req.getContent(), forbidden.size(), maxSim);

        return ModerationScanResponseDTO.builder()
                .language(lang)
                .forbiddenCount(forbidden.size())
                .forbiddenMatches(forbidden)
                .maxSimilarity(maxSim)
                .plagiarismFlag(flag)
                .plagiarismHits(hits)
                .aiRiskLevel(ai.getRiskLevel())
                .aiAction(ai.getAction())
                .aiReasons(ai.getReasons())
                .build();
    }

    private String normalizeLang(String language) {
        if (language == null || language.isBlank()) return "vi";
        String l = language.trim().toLowerCase(Locale.ROOT);
        return l.startsWith("en") ? "en" : "vi";
    }
}