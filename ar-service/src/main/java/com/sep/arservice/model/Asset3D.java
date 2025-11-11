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
@Table(name = "asset3Ds")
public class Asset3D implements Serializable {
    @Id
    @Column(name = "asset3D_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String asset3DId;

    @Column(name="asset_url", length=1000)
    private String assetUrl;

    @Column(name="thumb_url", length=1000)  // ảnh preview
    private String thumbUrl;

    @Column(name="source", length=50) // MESHY / UPLOAD / OTHER
    private String source;

    @Column(name = "prompt", length = 500)
    private String prompt;

    @Column(name="file_name", length=200)
    private String fileName;

    @Column(name = "format", length = 10) // GLB/FBX/OBJ
    private String format;

    @Column(name = "polycount")
    private int polycount;

    @Column(name="file_size")
    private long fileSize;

    @Column(name="scale")
    private Float scale; // scale mặc định khi render

    @Column(name="user_id", length=50)
    private String userId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Column(name = "marker_id", length = 50)
    private String markerId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marker_id", referencedColumnName = "marker_id", insertable = false, updatable = false)
    private Marker marker;
}