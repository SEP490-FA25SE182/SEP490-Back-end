package com.sep.aiservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_generation_targets")
public class AIGenerationTarget implements Serializable {
    @Id
    @Column(name = "ai_generation_target_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String aiGenerationTargetId;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Column(name = "ai_generation_id", length = 50, insertable = false, updatable = false)
    private String aiGenerationId;

    @Column(name = "target_ref_id", length = 50)
    private String targetRefId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_generation_id", referencedColumnName = "ai_generation_id", insertable = false, updatable = false)
    private AIGeneration aiGeneration;
}