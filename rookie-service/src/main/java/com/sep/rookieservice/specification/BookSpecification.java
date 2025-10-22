package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {

    private BookSpecification() {}

    public static Specification<Book> buildSpecification(
            String q,
            String authorId,
            Byte publicationStatus,
            Byte progressStatus,
            IsActived isActived
    ) {
        return Specification.allOf(
                likeNameOrDescription(q),
                filterByAuthor(authorId),
                filterByPublication(publicationStatus),
                filterByProgress(progressStatus),
                filterByIsActived(isActived)
        );
    }

    private static Specification<Book> likeNameOrDescription(String q) {
        return (root, query, cb) -> {
            if (q == null || q.trim().isEmpty()) return cb.conjunction();
            String likePattern = "%" + q.trim().toLowerCase() + "%";
            Predicate nameLike = cb.like(cb.lower(root.get("bookName")), likePattern);
            Predicate descLike = cb.like(cb.lower(root.get("decription")), likePattern);
            return cb.or(nameLike, descLike);
        };
    }

    private static Specification<Book> filterByAuthor(String authorId) {
        return (root, query, cb) ->
                (authorId == null || authorId.isEmpty()) ? cb.conjunction()
                        : cb.equal(root.get("authorId"), authorId);
    }

    private static Specification<Book> filterByPublication(Byte status) {
        return (root, query, cb) ->
                (status == null) ? cb.conjunction() : cb.equal(root.get("publicationStatus"), status);
    }

    private static Specification<Book> filterByProgress(Byte progress) {
        return (root, query, cb) ->
                (progress == null) ? cb.conjunction() : cb.equal(root.get("progressStatus"), progress);
    }

    private static Specification<Book> filterByIsActived(IsActived isActived) {
        return (root, query, cb) ->
                (isActived == null) ? cb.conjunction() : cb.equal(root.get("isActived"), isActived);
    }
}
