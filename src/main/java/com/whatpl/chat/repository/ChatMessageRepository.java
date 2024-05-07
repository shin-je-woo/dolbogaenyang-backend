package com.whatpl.chat.repository;

import com.whatpl.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageQueryRepository {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update ChatMessage cm set cm.readAt = :readAt where cm.sender.id != :receiverId")
    void updateReadAtByReceiverId(long receiverId, LocalDateTime readAt);
}
