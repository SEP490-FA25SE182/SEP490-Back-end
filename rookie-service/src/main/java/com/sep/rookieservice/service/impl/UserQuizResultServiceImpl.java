package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.QuizSubmitRequest;
import com.sep.rookieservice.dto.UserQuizResultRequest;
import com.sep.rookieservice.dto.UserQuizResultResponse;
import com.sep.rookieservice.entity.*;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.UserQuizResultMapper;
import com.sep.rookieservice.repository.QuizRepository;
import com.sep.rookieservice.repository.UserQuizResultRepository;
import com.sep.rookieservice.repository.WalletRepository;
import com.sep.rookieservice.service.UserQuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuizResultServiceImpl implements UserQuizResultService {

    private final UserQuizResultRepository repo;
    private final UserQuizResultMapper mapper;
    private final QuizRepository quizRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public UserQuizResultResponse create(UserQuizResultRequest dto) {
        // validate đơn giản
        if (dto.getCoin() == null || dto.getCoin() < 0) {
            throw new IllegalArgumentException("Coin must be >= 0");
        }

        UserQuizResult entity = mapper.toNewEntity(dto);
        UserQuizResult saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserQuizResultResponse update(String id, UserQuizResultRequest dto) {
        UserQuizResult existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserQuizResult not found with id: " + id));

        if (dto.getCoin() != null && dto.getCoin() < 0) {
            throw new IllegalArgumentException("Coin must be >= 0");
        }

        mapper.updateEntityFromDto(dto, existing);
        existing.setUpdatedAt(Instant.now());
        UserQuizResult updated = repo.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserQuizResultResponse getById(String id) {
        UserQuizResult entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserQuizResult not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        UserQuizResult entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserQuizResult not found with id: " + id));
        repo.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserQuizResultResponse> search(
            String quizId,
            String userId,
            Boolean isComplete,
            Boolean isReward,
            IsActived isActived,
            Pageable pageable
    ) {
        UserQuizResult probe = new UserQuizResult();
        probe.setQuizId(quizId);
        probe.setUserId(userId);
        probe.setIsComplete(isComplete);
        probe.setIsReward(isReward);
        probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnorePaths(
                        "attemptCount",
                        "coin",
                        "correctCount",
                        "questionCount",
                        "score",
                        "createdAt",
                        "updatedAt"
                );

        Example<UserQuizResult> example = Example.of(probe, matcher);

        return repo.findAll(example, pageable)
                .map(mapper::toDto);

    }

    @Transactional
    @Override
    public UserQuizResultResponse createForUser(QuizSubmitRequest request) {
        // 1. Lấy quiz + questions + answers
        Quiz quiz = quizRepository.findByQuizId(request.getQuizId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Quiz not found with id: " + request.getQuizId()));

        // Map questionId -> Question
        Map<String, Question> questionMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getQuestionId, q -> q));

        int quizQuestionCount = quiz.getQuestionCount();

        // Map questionId -> list answerId user chọn
        Map<String, List<String>> userAnswerMap = request.getAnswers().stream()
                .collect(Collectors.toMap(
                        QuizSubmitRequest.QuestionAnswerRequest::getQuestionId,
                        QuizSubmitRequest.QuestionAnswerRequest::getAnswerIds
                ));

        int answeredQuestionCount = 0; // questionCount của UserQuizResult
        int correctCount = 0;
        int totalScore = 0;

        // 2. Duyệt theo các câu hỏi mà user đã trả lời
        for (Map.Entry<String, List<String>> entry : userAnswerMap.entrySet()) {
            String questionId = entry.getKey();
            List<String> userAnswerIds = entry.getValue();

            Question question = questionMap.get(questionId);
            if (question == null) {
                // câu hỏi không thuộc quiz này => bỏ qua
                continue;
            }

            answeredQuestionCount++;
            if (answeredQuestionCount > quizQuestionCount) {
                // không vượt quá questionCount trong quiz
                answeredQuestionCount = quizQuestionCount;
                break;
            }

            // Lấy tập đáp án đúng của câu hỏi
            Set<String> correctAnswerIds = question.getAnswers().stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                    .map(Answer::getAnswerId)
                    .collect(Collectors.toSet());

            Set<String> userAnswerIdSet = new HashSet<>(userAnswerIds != null ? userAnswerIds : List.of());

            // Điều kiện "đúng tuyệt đối":
            // - user phải chọn ít nhất 1 đáp án
            // - tập đáp án user chọn phải == tập đáp án đúng (không thiếu, không thừa)
            if (!userAnswerIdSet.isEmpty() && userAnswerIdSet.equals(correctAnswerIds)) {
                correctCount++;
                totalScore += question.getScore();
            }
        }

        // 3. Tính isComplete
        boolean isComplete = (answeredQuestionCount == quizQuestionCount);

        // 4. Tính attemptCount và isReward
        long existingCount = repo.countByQuizIdAndUserId(request.getQuizId(), request.getUserId());
        int attemptCount = (int) existingCount + 1;
        boolean isReward = (existingCount == 0); // chỉ lần đầu được thưởng

        // 5. Tạo UserQuizResult entity mới
        UserQuizResult result = new UserQuizResult();
        result.setQuizId(request.getQuizId());
        result.setUserId(request.getUserId());
        result.setScore(totalScore);
        result.setAttemptCount(attemptCount);
        result.setCorrectCount(correctCount);
        result.setQuestionCount(answeredQuestionCount);
        result.setIsComplete(isComplete);
        result.setIsReward(isReward);
        result.setCoin(isReward ? (totalScore * 5) : 0); // lưu coin nhận được trong lần submit này
        result.setIsActived(IsActived.ACTIVE);
        result.setCreatedAt(Instant.now());
        result.setUpdatedAt(Instant.now());

        // 6. Cộng coin vào ví nếu được reward
        if (isReward && totalScore > 0) {
            Wallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Wallet not found for userId: " + request.getUserId()));

            wallet.setCoin(wallet.getCoin() + (totalScore * 5));
            wallet.setUpdatedAt(Instant.now());
            walletRepository.save(wallet);
        }

        // 7. Lưu UserQuizResult
        UserQuizResult saved = repo.save(result);
        return mapper.toDto(saved);
    }
}

