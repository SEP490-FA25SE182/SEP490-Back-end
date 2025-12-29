package com.sep.aiservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
@Table(name = "ai_chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String sessionId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Nationalized
    @Column(nullable = false)
    private String userMessage;

    @Nationalized
    @Column(nullable = false)
    private String aiResponse;

    private Instant createdAt = Instant.now();
}
