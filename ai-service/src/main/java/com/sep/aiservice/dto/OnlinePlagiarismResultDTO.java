package com.sep.aiservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class OnlinePlagiarismResultDTO {

    private boolean plagiarismFlag;
    private double maxSimilarity;
    private List<SourceMatchDTO> sources;

    @Data
    public static class SourceMatchDTO {
        private String url;
        private String title;
        private double similarity;
        private String matchedSnippet;
    }
}
