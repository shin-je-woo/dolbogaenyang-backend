package com.whatpl.chat.repository;

import com.whatpl.chat.domain.ChatRoom;

import java.util.Optional;

public interface ChatRoomQueryRepository {
    Optional<ChatRoom> findChatRoomWithAllById(Long id);
}
