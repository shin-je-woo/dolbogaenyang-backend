package com.whatpl.domain.chat.repository.room;

import com.whatpl.domain.chat.domain.ChatRoom;
import com.whatpl.domain.chat.dto.ChatRoomResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChatRoomQueryRepository {
    Optional<ChatRoom> findChatRoomWithAllById(Long id);
    List<ChatRoomResponse> findChatRooms(Pageable pageable, Long memberId);
}
