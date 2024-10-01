package com.whatpl.domain.chat.service;

import com.whatpl.domain.attachment.domain.AttachmentUrlParseDelegator;
import com.whatpl.domain.attachment.domain.AttachmentUrlParseType;
import com.whatpl.domain.chat.domain.ChatRoom;
import com.whatpl.domain.chat.dto.ChatRoomResponse;
import com.whatpl.domain.chat.repository.room.ChatRoomRepository;
import com.whatpl.global.util.PaginationUtils;
import com.whatpl.domain.project.domain.Apply;
import com.whatpl.domain.project.model.ApplyType;
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
    private final ChatMessageService chatMessageService;
    private final AttachmentUrlParseDelegator attachmentUrlParseDelegator;

    @Transactional
    public long createChatRoom(final Apply apply, final String content) {
        ChatRoom chatRoom = ChatRoom.from(apply);
        chatRoomRepository.save(chatRoom);
        // 메시지 발신자 = 프로젝트 지원일 경우 지원자 : 프로젝트 참여 제안일 경우 모집자
        long senderId = apply.getType() == ApplyType.APPLY ?
                apply.getApplicant().getId() : apply.getProject().getWriter().getId();
        chatMessageService.sendMessage(chatRoom.getId(), senderId, content);

        return chatRoom.getId();
    }

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
