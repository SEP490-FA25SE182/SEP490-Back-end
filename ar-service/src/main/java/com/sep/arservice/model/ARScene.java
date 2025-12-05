package com.sep.arservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.enums.PublicationEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ar_scenes")
public class ARScene implements Serializable {
    @Id
    @Column(name = "scene_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sceneId;

    @NotNull
    @Column(name="marker_id", length=50)
    private String markerId;

    @Column(name="name", length=100)
    private String name;

    @Nationalized
    @Column(name="description", length=500)
    private String description;

    @Column(name="version")
    private int version = 1;

    @Column(name="status", length=10)
    private String status = PublicationEnum.DRAFT.name();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marker_id", referencedColumnName = "marker_id", insertable = false, updatable = false)
    private Marker marker;
}

