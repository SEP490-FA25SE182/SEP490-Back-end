package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Genre;
import org.springframework.data.jpa.domain.Specification;

public class GenreSpecification {
    public static Specification<Genre> buildSpecification(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();

            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("genreName")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }
}
