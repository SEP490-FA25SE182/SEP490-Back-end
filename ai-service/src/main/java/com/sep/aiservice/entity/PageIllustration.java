package com.sep.aiservice.entity;

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
@Table(name = "page_illustrations")
public class PageIllustration implements Serializable {
    @Id
    @Column(name = "page_illustration_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pageIllustrationId;

    @Column(name = "page_id", length = 50)
    private String pageId;

    @Column(name = "illustration_id", length = 50)
    private String illustrationId;
}