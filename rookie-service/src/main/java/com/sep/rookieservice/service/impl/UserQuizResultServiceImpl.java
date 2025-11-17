package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.UserQuizResultRequest;
import com.sep.rookieservice.dto.UserQuizResultResponse;
import com.sep.rookieservice.entity.UserQuizResult;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.UserQuizResultMapper;
import com.sep.rookieservice.repository.UserQuizResultRepository;
import com.sep.rookieservice.service.UserQuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserQuizResultServiceImpl implements UserQuizResultService {

    private final UserQuizResultRepository repo;
    private final UserQuizResultMapper mapper;

    @Override
    @Transactional
    public UserQuizResultResponse create(UserQuizResultRequest dto) {
        // validate đơn giản
        if (dto.getCoin() == null || dto.getCoin() < 0) {
            throw new IllegalArgumentException("Coin must be >= 0");
        }

        UserQuizResult entity = mapper.toNewEntity(dto);
        // createdAt / updatedAt đã default trong entity
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
                .withIgnoreNullValues();

        Example<UserQuizResult> example = Example.of(probe, matcher);

        return repo.findAll(example, pageable)
                .map(mapper::toDto);
    }
}

