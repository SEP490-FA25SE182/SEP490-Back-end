package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Comment;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>, JpaSpecificationExecutor<Comment> {

    long countByBlogIdAndIsActived(String blogId, IsActived isActived);

    long countByBlogIdAndIsActivedAndIsPublished(String blogId, IsActived isActived, Boolean isPublished);
}
