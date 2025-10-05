package com.sep.aiservice.entity;

import com.sep.aiservice.enums.IsActived;
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
@Table(name = "illustrations")
public class Illustration implements Serializable {
    @Id
    @Column(name = "illustration_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String illustrationId;

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Column(name = "style", length = 50)
    private String style;

    @Column(name = "format", length = 10)
    private String format;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;
}