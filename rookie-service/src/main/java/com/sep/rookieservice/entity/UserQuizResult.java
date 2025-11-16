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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_quiz_results")
public class UserQuizResult implements Serializable {
    @Id
    @Column(name = "result_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String resultId;

    @Column(name = "score")
    private int score;

    @Column(name = "number")
    private int number;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "is_complete")
    private Boolean isComplete;

    @Column(name = "is_reward")
    private Boolean isReward;

    @NotNull
    @Column(name = "coin")
    private int coin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "quiz_id", length = 50)
    private String quizId;

    @NotNull
    @Column(name = "user_id", length = 50)
    private String userId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

}