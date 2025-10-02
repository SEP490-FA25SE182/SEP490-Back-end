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
@Table(name = "feedbacks")
public class Feedback implements Serializable {
    @Id
    @Column(name = "feedback_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String feedbackId;

    @Column(name = "content", length = 250)
    private String content;

    @Column(name = "rating")
    private byte rating;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "user_id", length = 50, insertable = false, updatable = false)
    private String userId;

    @NotNull
    @Column(name = "book_id", length = 50, insertable = false, updatable = false)
    private String bookId;

    @NotNull
    @Column(name = "order_detail_id", length = 50, insertable = false, updatable = false)
    private String orderDetailId;

    // ManytoOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", insertable = false, updatable = false)
    private Book book;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", referencedColumnName = "order_detail_id", insertable = false, updatable = false)
    private OrderDetail orderDetail;
}