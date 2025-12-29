package com.sep.arservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.arservice.enums.IsActived;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(
        name="markers",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_book_tag", columnNames={"book_id","tag_family","tag_id"})
        }
)
public class Marker implements Serializable {
    @Id
    @Column(name = "marker_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String markerId;

    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name="marker_code", length=50, unique = true)
    private String markerCode;

    @Column(name = "marker_type", length = 50)
    private String markerType;

    @Column(name="physical_width_m") //bề ngang thực tế của marker khi in (ví dụ 0.10 = 10cm)
    private double physicalWidthM = 1;

    @Lob
    @Column(name="printable_pdf_url") //đường dẫn file PDF marker để tải/in.
    private String printablePdfUrl;

    @Column(name="user_id", length=50)
    private String userId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @Column(name = "book_id", length = 50)
    private String bookId;

    // AprilTag family (e.g. tag36h11)
    @Column(name = "tag_family", length = 50)
    private String tagFamily;

    // AprilTag numeric id
    @Column(name = "tag_id")
    private Integer tagId;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "marker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Asset3D> asset3Ds;

    @JsonIgnore
    @OneToMany(mappedBy = "marker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ARScene> arScenes;
}