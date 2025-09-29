package com.sep.aiservice.model;

import jakarta.persistence.*;
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
@Table(name = "audios")
public class Audio implements Serializable {
    @Id
    @Column(name = "audio_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String audioId;

    @Column(name = "audio_url", length = 100)
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

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}