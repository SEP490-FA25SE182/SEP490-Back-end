package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Feedback;
import com.sep.rookieservice.enums.FeedbackStatus;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public final class FeedbackSpecification {

    public static Specification<Feedback> buildSpecification(
            String keyword,
            String bookId,
            String userId,
            IsActived isActived,
            FeedbackStatus status
    ) {

        Specification<Feedback> spec = Specification.allOf();

        // Search by content
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%"));
        }

        // Filter by book
        if (bookId != null && !bookId.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("bookId"), bookId));
        }

        // Filter by user
        if (userId != null && !userId.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        }

        // Filter by isActived
        if (isActived != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActived"), isActived));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return spec;
    }
}
