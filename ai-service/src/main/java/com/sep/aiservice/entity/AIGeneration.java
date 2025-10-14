package com.sep.aiservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.aiservice.enums.AIGenerationEnum;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.enums.StylePreset;
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
import java.util.List;

@Data
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "prompt", length = 1000)
    private String prompt;

    @Column(name = "negative_prompt", length = 1000)
    private String negativePrompt;

    @Column(name = "duration_ms")
    private double durationMs;

    @Column(name = "status")
    private Byte status = AIGenerationEnum.PENDING.getStatus();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", length = 20)
    private GenerationMode mode = GenerationMode.TEXT_TO_IMAGE;

    @Column(name = "aspect_ratio", length = 10)
    private String aspectRatio;  // ví dụ "3:2"

    @Column(name = "strength")
    private Double strength;

    @Column(name = "seed")
    private Long seed;

    @Column(name = "cfg_scale")
    private Double cfgScale;

    @Enumerated(EnumType.STRING)
    @Column(name = "style_preset", length = 30)
    private StylePreset stylePreset;

    @Column(name = "accept_header", length = 40)
    private String acceptHeader; // "image/*" | "application/json"

    @Column(name = "input_image_url", length = 500)
    private String inputImageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "aiGeneration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AIGenerationTarget> aiGenerationTargets;
}