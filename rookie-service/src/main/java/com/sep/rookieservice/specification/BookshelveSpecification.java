package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Bookshelve;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class BookshelveSpecification {

    private BookshelveSpecification() {}

    /**
     * Build specification for Bookshelve search.
     */
    public static Specification<Bookshelve> buildSpecification(
            String q,
            String userId,
            IsActived isActived
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (q != null && !q.trim().isEmpty()) {
                String likePattern = "%" + q.trim().toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("bookshelveName")), likePattern));
            }

            if (userId != null && !userId.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("userId"), userId.trim()));
            }

            if (isActived != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isActived"), isActived));
            }

            return predicate;
        };
    }
}
