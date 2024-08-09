package com.whatpl.domain.chat.repository;

import com.whatpl.domain.chat.dto.ChatMessageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageQueryRepository {
    Slice<ChatMessageDto> findMessagesByChatRoomId(Long chatRoomId, Pageable pageable);
}
