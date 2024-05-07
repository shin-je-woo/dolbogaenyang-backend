package com.whatpl.chat.service;

import com.whatpl.chat.domain.ChatMessage;
import com.whatpl.chat.domain.ChatRoom;
import com.whatpl.chat.dto.ChatMessageDto;
import com.whatpl.chat.repository.ChatMessageRepository;
import com.whatpl.chat.repository.ChatRoomRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.enums.ApplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public long createChatRoom(final Apply apply, final String content) {
        ChatRoom chatRoom = ChatRoom.from(apply);
        chatRoomRepository.save(chatRoom);
        // 메시지 발신자 = 프로젝트 지원일 경우 지원자 : 프로젝트 참여 제안일 경우 모집자
        long senderId = apply.getType() == ApplyType.APPLY ?
                apply.getApplicant().getId() : apply.getProject().getWriter().getId();
        this.sendMessage(chatRoom.getId(), senderId, content);

        return chatRoom.getId();
    }

    @Transactional
    public void sendMessage(final Long chatRoomId, final Long senderId, final String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_CHAT_ROOM));
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();
        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public Slice<ChatMessageDto> readMessages(final long chatRoomId, final Pageable pageable, long receiverId) {
        Slice<ChatMessageDto> chatMessages = chatMessageRepository.findMessagesByChatRoomId(chatRoomId, pageable);
        // 수신자 읽은 시간 update (리스트에 포함되어 있지 않아도 한번 읽으면 일괄적으로 읽음 처리)
        chatMessageRepository.updateReadAtByReceiverId(receiverId, LocalDateTime.now().withNano(0));
        return chatMessages;
    }
}
