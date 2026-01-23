package com.sep.aiservice.service.impl;

import com.sep.aiservice.config.ModerationProperties;
import com.sep.aiservice.dto.*;
import com.sep.aiservice.dto.OnlinePlagiarismRequestDTO;
import com.sep.aiservice.dto.OnlinePlagiarismResultDTO;
import com.sep.aiservice.service.ForbiddenWordsService;
import com.sep.aiservice.service.ModerationAiService;
import com.sep.aiservice.service.ModerationService;
import com.sep.aiservice.service.PlagiarismService;
import com.sep.aiservice.service.OnlinePlagiarismService;
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
    private final OnlinePlagiarismService onlinePlagiarismService;
    private final ModerationAiService moderationAiService;


    @Override
    public ModerationScanResponseDTO scan(ModerationScanRequestDTO req) {

        String lang = normalizeLang(req.getLanguage());
        String content = req.getContent();

        //Forbidden words
        List<ForbiddenWordMatchDTO> forbidden =
                forbiddenWordsService.scan(content, lang);

        //Internal plagiarism (DB)
        List<PlagiarismHitDTO> internalHits =
                plagiarismService.scanAgainstActivePages(content, req.getEntityId());

        double internalMaxSim = internalHits.stream()
                .mapToDouble(PlagiarismHitDTO::getSimilarity)
                .max()
                .orElse(0.0);

        //Online plagiarism (Gemini)
        OnlinePlagiarismRequestDTO onlineReq = new OnlinePlagiarismRequestDTO();
        onlineReq.setContent(content);
        onlineReq.setLanguage(lang);
        onlineReq.setMaxSources(props.getPlagiarism().getTopK());
        OnlinePlagiarismResultDTO onlineResult =
                onlinePlagiarismService.scan(onlineReq);

        double onlineMaxSim =
                onlineResult != null ? onlineResult.getMaxSimilarity() : 0.0;

        //Merge similarity
        double finalMaxSimilarity = Math.max(internalMaxSim, onlineMaxSim);

        boolean plagiarismFlag =
                finalMaxSimilarity >= props.getPlagiarism().getFlagThreshold();

        //AI decision (final brain)
        AiModerationResultDTO ai =
                moderationAiService.analyze(
                        content,
                        forbidden.size(),
                        finalMaxSimilarity
                );

        //Response
        return ModerationScanResponseDTO.builder()
                .language(lang)
                .forbiddenCount(forbidden.size())
                .forbiddenMatches(forbidden)

                // Internal
                .plagiarismHits(internalHits)

                // Merged
                .maxSimilarity(finalMaxSimilarity)
                .plagiarismFlag(plagiarismFlag)

                // AI decision
                .aiRiskLevel(ai.getRiskLevel())
                .aiAction(ai.getAction())
                .aiReasons(ai.getReasons())

                // .onlineSources(onlineResult.getSources())
                .onlineSources(mapOnlineSources(onlineResult))


                .build();
    }

    private String normalizeLang(String language) {
        if (language == null || language.isBlank()) return "vi";
        String l = language.trim().toLowerCase(Locale.ROOT);
        return l.startsWith("en") ? "en" : "vi";
    }

    private List<OnlinePlagiarismSourceDTO> mapOnlineSources(
            OnlinePlagiarismResultDTO onlineResult
    ) {
        if (onlineResult == null || onlineResult.getSources() == null) {
            return List.of();
        }

        return onlineResult.getSources().stream().map(src -> {
            OnlinePlagiarismSourceDTO dto = new OnlinePlagiarismSourceDTO();
            dto.setTitle(src.getTitle());
            dto.setUrl(src.getUrl());
            dto.setSimilarity(src.getSimilarity());
            return dto;
        }).toList();
    }
}
