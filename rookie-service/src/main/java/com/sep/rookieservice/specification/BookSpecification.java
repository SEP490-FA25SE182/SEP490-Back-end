package com.sep.rookieservice.specification;

import com.sep.rookieservice.model.Book;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {

    private BookSpecification() {}

    /**
     * Build a specification to search by q (bookName, decription),
     * and optional filters authorId, publicationStatus, progressStatus, isActived.
     */
    public static Specification<Book> buildSpecification(
            String q,
            String authorId,
            Byte publicationStatus,
            Byte progressStatus,
            IsActived isActived
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (q != null && !q.trim().isEmpty()) {
                String likePattern = "%" + q.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("bookName")), likePattern);
                Predicate descLike = cb.like(cb.lower(root.get("decription")), likePattern);
                predicate = cb.and(predicate, cb.or(nameLike, descLike));
            }

            if (authorId != null && !authorId.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("authorId"), authorId.trim()));
            }

            if (publicationStatus != null) {
                predicate = cb.and(predicate, cb.equal(root.get("publicationStatus"), publicationStatus));
            }

            if (progressStatus != null) {
                predicate = cb.and(predicate, cb.equal(root.get("progressStatus"), progressStatus));
            }

            if (isActived != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isActived"), isActived));
            }

            return predicate;
        };
    }
}
