package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Blog;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class BlogSpecification {
    private BlogSpecification() {}

    public static Specification<Blog> titleContains(String title) {
        return (root, q, cb) -> title == null ? cb.conjunction()
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Blog> contentContains(String content) {
        return (root, q, cb) -> content == null ? cb.conjunction()
                : cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Blog> authorEq(String authorId) {
        return (root, q, cb) -> authorId == null ? cb.conjunction()
                : cb.equal(root.get("authorId"), authorId);
    }

    public static Specification<Blog> bookEq(String bookId) {
        return (root, q, cb) -> bookId == null ? cb.conjunction()
                : cb.equal(root.get("bookId"), bookId);
    }

    public static Specification<Blog> activedEq(IsActived isActived) {
        return (root, q, cb) -> isActived == null ? cb.conjunction()
                : cb.equal(root.get("isActived"), isActived);
    }

    public static Specification<Blog> hasAnyTagNames(Set<String> tagNames) {
        return (root, query, cb) -> {
            if (tagNames == null || tagNames.isEmpty()) return null;
            var lowered = tagNames.stream()
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            var tags = root.join("tags");
            query.distinct(true);
            return cb.lower(tags.get("name")).in(lowered);
        };
    }
}

