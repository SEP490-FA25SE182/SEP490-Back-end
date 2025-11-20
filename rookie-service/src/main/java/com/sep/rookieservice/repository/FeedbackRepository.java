package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Feedback;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String>, JpaSpecificationExecutor<Feedback> {
    boolean existsByUserIdAndBookIdAndIsActived(String userId, String bookId, IsActived isActived);
}
