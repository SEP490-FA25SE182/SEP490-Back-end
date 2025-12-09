package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Chapter;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ChapterSpecification {

    private ChapterSpecification() {}

    public static Specification<Chapter> buildSpecification(
            String q,
            String bookId,
            Byte progressStatus,
            IsActived isActived
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (q != null && !q.trim().isEmpty()) {
                String likePattern = "%" + q.trim().toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("chapterName")), likePattern));
            }

            if (bookId != null && !bookId.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("bookId"), bookId.trim()));
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
