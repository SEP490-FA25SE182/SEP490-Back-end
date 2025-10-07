package com.sep.aiservice.repository;

import com.sep.aiservice.entity.AIGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIGenerationRepository extends JpaRepository<AIGeneration, String> {

}
