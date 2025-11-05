package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Comment;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {

    public static Specification<Comment> buildSpecification(String q, String blogId, String userId, IsActived isActived) {
        return Specification.allOf(
                likeContent(q),
                byBlogId(blogId),
                byUserId(userId),
                byIsActived(isActived)
        );
    }

    private static Specification<Comment> likeContent(String q) {
        return (root, query, cb) -> q == null || q.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("content")), "%" + q.toLowerCase() + "%");
    }

    private static Specification<Comment> byBlogId(String blogId) {
        return (root, query, cb) -> blogId == null ? cb.conjunction() : cb.equal(root.get("blogId"), blogId);
    }

    private static Specification<Comment> byUserId(String userId) {
        return (root, query, cb) -> userId == null ? cb.conjunction() : cb.equal(root.get("userId"), userId);
    }

    private static Specification<Comment> byIsActived(IsActived isActived) {
        return (root, query, cb) -> isActived == null ? cb.conjunction() : cb.equal(root.get("isActived"), isActived);
    }

    public static Specification<Comment> forPublicByBlogId(String blogId) {
        return Specification.allOf(
                byBlogId(blogId),
                byIsActived(IsActived.ACTIVE),
                isPublishedTrue()
        );
    }

    private static Specification<Comment> isPublishedTrue() {
        return (root, query, cb) -> cb.isTrue(root.get("isPublished"));
    }
}
