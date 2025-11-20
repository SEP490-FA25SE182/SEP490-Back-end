package com.sep.aiservice.repository;

import com.sep.aiservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
