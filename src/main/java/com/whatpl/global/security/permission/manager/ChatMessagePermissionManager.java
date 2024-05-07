package com.whatpl.global.security.permission.manager;

import com.whatpl.chat.domain.ChatRoom;
import com.whatpl.chat.repository.ChatRoomRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChatMessagePermissionManager implements WhatplPermissionManager {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        return switch (permission) {
            case "CREATE" -> hasCreatePrivilege(memberPrincipal, targetId);
            case "READ" -> hasReadPrivilege(memberPrincipal, targetId);
            default -> false;
        };
    }

    /**
     * 대화 메시지 쓰기 권한
     * 프로젝트 모집자, 지원자(참여 제안자)
     */
    private boolean hasCreatePrivilege(MemberPrincipal memberPrincipal, Long chatRoomId) {
        long loginUserId = memberPrincipal.getId();
        return isRecruiterOrApplicant(chatRoomId, loginUserId);
    }

    /**
     * 대화 메시지 읽기 권한
     * 프로젝트 모집자, 지원자(참여 제안자)
     */
    private boolean hasReadPrivilege(MemberPrincipal memberPrincipal, Long chatRoomId) {
        long loginUserId = memberPrincipal.getId();
        return isRecruiterOrApplicant(chatRoomId, loginUserId);
    }

    private boolean isRecruiterOrApplicant(long chatRoomId, long loginUserId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithAllById(chatRoomId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_CHAT_ROOM));
        Long recruiterId = chatRoom.getApply().getProject().getWriter().getId();
        Long applicantId = chatRoom.getApply().getApplicant().getId();
        return loginUserId == recruiterId || loginUserId == applicantId;
    }
}
