package com.sep.arservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.arservice.enums.PublicationEnum;
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
@Table(name = "ar_scene_items")
public class ARSceneItem implements Serializable {
    @Id
    @Column(name = "item_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String itemId;

    @NotNull
    @Column(name="scene_id", length=50)
    private String sceneId;

    @Column(name="asset3D_id", length=50)
    private String asset3DId;

    @Column(name="order_index")
    private int orderIndex = 1;

    @Column(name="pos_x") //tọa độ local theo marker
    private float posX;

    @Column(name="pos_y")
    private float posY;

    @Column(name="pos_z")
    private float posZ;

    @Column(name="rot_x") //Euler
    private float rotX;

    @Column(name="rot_y")
    private float rotY;

    @Column(name="rot_z")
    private float rotZ;

    @Column(name="scale_x")
    private float scaleX;

    @Column(name="scale_y")
    private float scaleY;

    @Column(name="scale_z")
    private float scaleZ;

    @Column(name="behavior_json", length=2000) //optional: tap to animate/open link
    private String behaviorJson;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marker_id", referencedColumnName = "marker_id", insertable = false, updatable = false)
    private Marker marker;
}
