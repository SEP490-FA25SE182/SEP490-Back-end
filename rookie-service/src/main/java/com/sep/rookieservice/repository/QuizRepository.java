package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuizRepository extends JpaRepository<Quiz, String>, JpaSpecificationExecutor<Quiz> {
}
