package com.whatpl.chat.repository;

import com.whatpl.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @EntityGraph(attributePaths = {"project", "applicant"})
    Optional<ChatRoom> findChatRoomWithAllById(Long id);
}
