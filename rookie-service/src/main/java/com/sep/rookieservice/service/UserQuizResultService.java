package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.QuizSubmitRequest;
import com.sep.rookieservice.dto.UserQuizResultRequest;
import com.sep.rookieservice.dto.UserQuizResultResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserQuizResultService {
    UserQuizResultResponse create(UserQuizResultRequest dto);
    UserQuizResultResponse update(String id, UserQuizResultRequest dto);
    UserQuizResultResponse getById(String id);
    void delete(String id);

    Page<UserQuizResultResponse> search(
            String quizId,
            String userId,
            Boolean isComplete,
            Boolean isReward,
            IsActived isActived,
            Pageable pageable
    );

    UserQuizResultResponse createForUser(QuizSubmitRequest request);
}

