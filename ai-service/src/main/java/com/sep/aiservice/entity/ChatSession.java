package com.sep.aiservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.aiservice.enums.IsActived;
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
@Table(name = "chat_sessions")
public class ChatSession implements Serializable {
    @Id
    @Column(name = "chat_session_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String chatSessionId;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "user_id", length = 50)
    private String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages;
}