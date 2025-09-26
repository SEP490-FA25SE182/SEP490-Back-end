package com.sep.aiservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.aiservice.enums.AIGenerationEnum;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.enums.MessageEnum;
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
@Table(name = "messages")
public class Message implements Serializable {
    @Id
    @Column(name = "message_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String messageId;

    @Column(name = "role", length = 10)
    private String role;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", updatable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "status")
    private Byte status = MessageEnum.SENDING.getStatus();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "chat_session_id", length = 50, insertable = false, updatable = false)
    private String chatSessionId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id", referencedColumnName = "chat_session_id", insertable = false, updatable = false)
    private ChatSession chatSession;
}