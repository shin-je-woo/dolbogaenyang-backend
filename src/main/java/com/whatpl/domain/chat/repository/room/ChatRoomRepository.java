package com.whatpl.domain.chat.repository.room;

import com.whatpl.domain.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQueryRepository {
}
