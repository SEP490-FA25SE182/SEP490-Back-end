package com.sep.aiservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.aiservice.enums.AIGenerationEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_generations")
public class AIGeneration implements Serializable {
    @Id
    @Column(name = "ai_generation_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String aiGenerationId;

    @Column(name = "model_name", length = 50)
    private String modelName;

    @Column(name = "prompt", length = 500)
    private String prompt;

    @Column(name = "negative_prompt", length = 500)
    private String negativePrompt;

    @Column(name = "duration_ms")
    private double durationMs;

    @Column(name = "status")
    private Byte status = AIGenerationEnum.PENDING.getStatus();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "user_id", length = 50)
    private String userId;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "aiGeneration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AIGenerationTarget> aiGenerationTargets;
}