package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.ChapterEnum;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.PublicationEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "chapters",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"book_id", "chapter_number"})
        }
)
public class Chapter implements Serializable {
    @Id
    @Column(name = "chapter_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String chapterId;

    @Nationalized
    @Column(name = "chapter_name", length = 50)
    private String chapterName;

    @Column(name = "chapter_number")
    private int chapterNumber;

    @Nationalized
    @Lob
    @Column(name = "decription")
    private String decription;

    @Nationalized
    @Column(name = "review", length = 250)
    private String review;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "published_date")
    private Instant publishedDate;

    @Column(name = "progress_status")
    private Byte progressStatus = ChapterEnum.IN_REVIEW.getStatus();

    @NotNull
    @Column(name = "book_id", length = 50)
    private String bookId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", insertable = false, updatable = false)
    private Book book;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Page> pages;

    @JsonIgnore
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Quiz> quizzes;
}