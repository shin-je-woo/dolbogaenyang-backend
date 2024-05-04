package com.whatpl.chat.service;

import com.whatpl.chat.domain.ChatMessage;
import com.whatpl.chat.domain.ChatRoom;
import com.whatpl.chat.domain.enums.ChatRoomType;
import com.whatpl.chat.dto.ChatRoomDto;
import com.whatpl.chat.repository.ChatMessageRepository;
import com.whatpl.chat.repository.ChatRoomRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public long createChatRoom(ChatRoomDto chatRoomDto, String content) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .project(chatRoomDto.getProject())
                .applicant(chatRoomDto.getApplicant())
                .type(chatRoomDto.getChatRoomType())
                .job(chatRoomDto.getJob())
                .build());
        // 메시지 발신자 = 프로젝트 지원일 경우 지원자 : 프로젝트 참여 제안일 경우 모집자
        long senderId = chatRoomDto.getChatRoomType() == ChatRoomType.APPLY ?
                chatRoomDto.getApplicant().getId() : chatRoomDto.getProject().getWriter().getId();
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
}
