package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Page;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class PageSpecification {

    private PageSpecification() {}

    public static Specification<Page> buildSpecification(
            String q,
            String chapterId,
            String pageType,   // <--- thêm
            IsActived isActived
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (q != null && !q.trim().isEmpty()) {
                String likePattern = "%" + q.trim().toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("content")), likePattern));
            }

            if (chapterId != null && !chapterId.trim().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("chapterId"), chapterId.trim()));
            }

            // filter theo pageType (so sánh equals, ignore case)
            if (pageType != null && !pageType.trim().isEmpty()) {
                String pt = pageType.trim().toLowerCase();
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(root.get("pageType")), pt));
            }

            if (isActived != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("isActived"), isActived));
            }

            return predicate;
        };
    }
}
