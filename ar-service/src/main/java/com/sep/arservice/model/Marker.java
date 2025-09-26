package com.sep.arservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "markers")
public class Marker implements Serializable {
    @Id
    @Column(name = "marker_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String markerId;

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Column(name = "marker_code", length = 50)
    private String markerCode;

    @Column(name = "marker_type", length = 50)
    private String markerType;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "marker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlignmentData> alignmentDatas;

    @JsonIgnore
    @OneToMany(mappedBy = "marker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Asset3D> asset3Ds;
}