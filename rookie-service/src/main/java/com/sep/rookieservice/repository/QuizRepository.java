package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String>, JpaSpecificationExecutor<Quiz> {
    @EntityGraph(attributePaths = {
            "questions",
            "questions.answers"
    })
    Optional<Quiz> findByQuizId(String quizId);
}
