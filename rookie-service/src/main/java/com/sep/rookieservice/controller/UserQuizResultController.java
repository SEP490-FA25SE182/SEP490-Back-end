package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.QuizSubmitRequest;
import com.sep.rookieservice.dto.UserQuizResultRequest;
import com.sep.rookieservice.dto.UserQuizResultResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.UserQuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/books/user-quiz-results")
@RequiredArgsConstructor
public class UserQuizResultController {

    private final UserQuizResultService service;

    @GetMapping
    public Page<UserQuizResultResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String quizId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Boolean isComplete,
            @RequestParam(required = false) Boolean isReward,
            @RequestParam(required = false) IsActived isActived
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split("-");
                String prop = parts[0].trim();
                Sort.Direction dir = Sort.Direction.ASC;
                if (parts.length > 1) {
                    try {
                        dir = Sort.Direction.fromString(parts[1].trim());
                    } catch (IllegalArgumentException ignore) {}
                }
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return service.search(quizId, userId, isComplete, isReward, isActived, pageable);
    }

    @PostMapping
    public UserQuizResultResponse create(@RequestBody UserQuizResultRequest dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public UserQuizResultResponse update(@PathVariable String id, @RequestBody UserQuizResultRequest dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public UserQuizResultResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/submit")
    public UserQuizResultResponse submitQuiz(@RequestBody QuizSubmitRequest request) {
        return service.createForUser(request);
    }
}

