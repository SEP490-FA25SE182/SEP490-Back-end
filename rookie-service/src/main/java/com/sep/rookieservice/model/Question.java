package com.sep.rookieservice.model;

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
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @Column(name = "question_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String questionId;

    @Column(name = "score")
    private int score;

    @Column(name = "content", length = 250)
    private String content;

    @Column(name = "updated_at", updatable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "quiz_id", length = 50, insertable = false, updatable = false)
    private String quizId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Answer> answers;
}