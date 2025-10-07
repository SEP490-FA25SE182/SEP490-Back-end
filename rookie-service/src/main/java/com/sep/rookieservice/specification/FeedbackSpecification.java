package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Feedback;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public final class FeedbackSpecification {

    public static Specification<Feedback> buildSpecification(String keyword, String bookId, String userId, IsActived isActived) {
        Specification<Feedback> spec = Specification.allOf();

        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%"));
        }

        if (bookId != null && !bookId.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("bookId"), bookId));
        }

        if (userId != null && !userId.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        }

        if (isActived != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActived"), isActived));
        }

        return spec;
    }
}
