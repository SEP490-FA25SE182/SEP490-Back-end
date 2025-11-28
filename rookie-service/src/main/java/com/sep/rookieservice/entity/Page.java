package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.IsActived;
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
@Table(
        name = "pages",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"chapter_id", "page_number"})
        }
)
public class Page implements Serializable {
    @Id
    @Column(name = "page_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pageId;

    @Column(name = "page_number")
    private int pageNumber;

    @Lob
    @Nationalized
    @Column(name = "content")
    private String content;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "page_type", length = 10)
    private String pageType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "chapter_id", length = 50)
    private String chapterId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", referencedColumnName = "chapter_id", insertable = false, updatable = false)
    private Chapter chapter;
}
