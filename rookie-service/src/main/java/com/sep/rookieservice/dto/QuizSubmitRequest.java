package com.sep.rookieservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotNull
    private String quizId;

    @NotNull
    private String userId;

    /**
     * Danh sách câu trả lời của user:
     * - questionId: id câu hỏi
     * - answerIds: các đáp án user chọn cho câu đó
     */
    @NotNull
    private List<QuestionAnswerRequest> answers;

    @Data
    public static class QuestionAnswerRequest {
        private String questionId;
        private List<String> answerIds;
    }
}

