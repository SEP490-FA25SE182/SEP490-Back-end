package com.sep.aiservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
}