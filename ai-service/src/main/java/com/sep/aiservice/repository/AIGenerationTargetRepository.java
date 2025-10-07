package com.sep.aiservice.repository;

import com.sep.aiservice.entity.AIGenerationTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIGenerationTargetRepository extends JpaRepository<AIGenerationTarget, String> {

}
