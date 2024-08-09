package com.whatpl.domain.chat.repository.message;

import com.whatpl.domain.chat.dto.ChatMessageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageQueryRepository {
    Slice<ChatMessageDto> findMessagesByChatRoomId(Long chatRoomId, Pageable pageable);
}
