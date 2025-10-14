package com.sep.aiservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.aiservice.enums.IsActived;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@Data
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Column(name = "ai_generation_id", length = 50)
    private String aiGenerationId;

    @Column(name = "target_ref_id", length = 50)
    private String targetRefId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_generation_id", referencedColumnName = "ai_generation_id", insertable = false, updatable = false)
    private AIGeneration aiGeneration;
}