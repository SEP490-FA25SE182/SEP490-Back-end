package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Blog;
import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.entity.Tag;
import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.criteria.*;
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

    public static Specification<Blog> hasAnyTagIds(Set<String> tagIds) {
        return (root, query, cb) -> {
            if (tagIds == null || tagIds.isEmpty()) return null;
            var tagsJoin = root.join("tags");   // ManyToMany
            query.distinct(true);
            CriteriaBuilder.In<String> in = cb.in(tagsJoin.get("tagId"));
            tagIds.stream().filter(Objects::nonNull).forEach(in::value);
            return in;
        };
    }

    public static Specification<Blog> isActiveOnly() {
        return (root, q, cb) -> cb.equal(root.get("isActived"), IsActived.ACTIVE);
    }

    public static Specification<Blog> userFacingSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String like = "%" + keyword.trim().toLowerCase() + "%";

            var user = root.join("user", JoinType.LEFT);
            var book = root.join("book", JoinType.LEFT);

            Subquery<Integer> tagSq = query.subquery(Integer.class);
            var correlatedBlog = tagSq.correlate(root);
            Join<Blog, Tag> t = correlatedBlog.join("tags", JoinType.INNER);
            tagSq.select(cb.literal(1))
                    .where(cb.like(cb.lower(t.get("name")), like));

            query.distinct(true);

            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("content")), like),

                    // Author
                    cb.like(cb.lower(user.get("fullName")), like),

                    // Book
                    cb.like(cb.lower(book.get("bookName")), like),

                    // Tag
                    cb.exists(tagSq)
            );
        };
    }
}

