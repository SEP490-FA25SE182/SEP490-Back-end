package com.sep.rookieservice.specification;
import com.sep.rookieservice.entity.Question;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpecification {

    public static Specification<Question> buildSpecification(String keyword, String quizId, IsActived isActived) {
        Specification<Question> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%"));
        }

        if (quizId != null && !quizId.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("quizId"), quizId));
        }

        if (isActived != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActived"), isActived));
        }

        return spec;
    }
}