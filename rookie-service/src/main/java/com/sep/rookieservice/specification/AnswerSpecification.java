package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Answer;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AnswerSpecification {

    public static Specification<Answer> buildSpecification(
            String keyword,
            String questionId,
            Boolean isCorrect,
            IsActived isActived
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%"));
            }

            if (questionId != null && !questionId.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("questionId"), questionId));
            }

            if (isCorrect != null) {
                predicates.add(cb.equal(root.get("isCorrect"), isCorrect));
            }

            if (isActived != null) {
                predicates.add(cb.equal(root.get("isActived"), isActived));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
