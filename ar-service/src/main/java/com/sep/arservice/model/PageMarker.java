package com.sep.arservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "page_markers")
public class PageMarker implements Serializable {
    @Id
    @Column(name = "page_marker_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pageMarkerId;

    @Column(name = "page_id", length = 50)
    private String pageId;

    @Column(name = "marker_id", length = 50)
    private String markerId;
}