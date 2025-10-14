package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Notification;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecification {

    public static Specification<Notification> build(String q, String userId, String bookId, String orderId, IsActived isActived) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (q != null && !q.isBlank()) {
                String likePattern = "%" + q.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("message")), likePattern),
                        cb.like(cb.lower(root.get("title")), likePattern)
                ));
            }

            if (userId != null && !userId.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("userId"), userId));
            }

            if (bookId != null && !bookId.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("bookId"), bookId));
            }

            if (orderId != null && !orderId.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("orderId"), orderId));
            }

            if (isActived != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isActived"), isActived));
            }

            return predicate;
        };
    }
}
