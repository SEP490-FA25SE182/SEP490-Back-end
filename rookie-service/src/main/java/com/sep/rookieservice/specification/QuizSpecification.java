package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Quiz;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public final class QuizSpecification {

    public static Specification<Quiz> buildSpecification(String keyword, String chapterId, IsActived isActived) {
        Specification<Quiz> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
        }

        if (chapterId != null && !chapterId.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("chapterId"), chapterId));
        }

        if (isActived != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActived"), isActived));
        }

        return spec;
    }
}
