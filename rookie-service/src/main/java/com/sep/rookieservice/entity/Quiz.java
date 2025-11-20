package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.IsActived;
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
@Table(name = "quizs")
public class Quiz implements Serializable {
    @Id
    @Column(name = "quiz_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String quizId;

    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "attempt_count")
    private int attemptCount;

    @Column(name = "question_count")
    private int questionCount;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "chapter_id", length = 50)
    private String chapterId;


    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", referencedColumnName = "chapter_id", insertable = false, updatable = false)
    private Chapter chapter;


    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions;

    @JsonIgnore
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserQuizResult> userQuizResults;
}