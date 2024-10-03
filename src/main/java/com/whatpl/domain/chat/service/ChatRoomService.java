package com.whatpl.domain.chat.service;

import com.whatpl.domain.attachment.domain.AttachmentUrlParseDelegator;
import com.whatpl.domain.attachment.domain.AttachmentUrlParseType;
import com.whatpl.domain.chat.dto.ChatRoomResponse;
import com.whatpl.domain.chat.repository.room.ChatRoomRepository;
import com.whatpl.global.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AttachmentUrlParseDelegator attachmentUrlParseDelegator;

    @Transactional(readOnly = true)
    public Slice<ChatRoomResponse> readChatRooms(final Pageable pageable, final Long memberId) {
        List<ChatRoomResponse> chatRooms = chatRoomRepository.findChatRooms(pageable, memberId);
        chatRooms.forEach(chatRoom -> {
            String opponentPictureId = attachmentUrlParseDelegator.parseUrl(
                    AttachmentUrlParseType.MEMBER_PICTURE,
                    chatRoom.getOpponentPictureId()
            );
            chatRoom.assignOpponentPictureUrl(opponentPictureId);
        });
        return new SliceImpl<>(chatRooms, pageable, PaginationUtils.hasNext(chatRooms, pageable.getPageSize()));
    }
}
