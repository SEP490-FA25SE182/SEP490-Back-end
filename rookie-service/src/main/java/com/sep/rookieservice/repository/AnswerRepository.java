package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String>, JpaSpecificationExecutor<Answer> {
}
