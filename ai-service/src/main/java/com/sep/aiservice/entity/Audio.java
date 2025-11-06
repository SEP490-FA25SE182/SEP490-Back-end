package com.sep.aiservice.entity;

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
@Table(name = "audios")
public class Audio implements Serializable {
    @Id
    @Column(name = "audio_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String audioId;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "voice", length = 50)
    private String voice;

    @Column(name = "format", length = 10)
    private String format;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "duration_ms")
    private double durationMs;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "user_id", length = 50)
    private String userId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;
}