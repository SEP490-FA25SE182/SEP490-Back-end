package com.sep.arservice.model;

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
@Table(name = "alignment_datas")
public class AlignmentData implements Serializable {
    @Id
    @Column(name = "alignment_data_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String alignmentDataId;

    @Column(name = "pose_matrix", length = 500)
    private String poseMatrix;

    @Column(name = "scale")
    private float scale;

    @Column(name = "confidence_score")
    private float confidenceScore;

    @Column(name = "rotation", length = 100)
    private String rotation;

    @Column(name = "translation", length = 100)
    private String translation;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Column(name = "marker_id", length = 50, insertable = false, updatable = false)
    private String markerId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marker_id", referencedColumnName = "marker_id", insertable = false, updatable = false)
    private Marker marker;
}