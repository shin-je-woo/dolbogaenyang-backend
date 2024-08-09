package com.whatpl.domain.chat.dto;

import com.whatpl.global.pagination.SliceElement;
import com.whatpl.domain.project.domain.enums.ApplyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse implements SliceElement {

    private long chatRoomId;
    private ApplyType applyType;
    private long projectId;
    private String projectTitle;
    private long opponentId;
    private String opponentNickname;
    private String opponentProfileImgUri; // TODO 상대방 프로필 이미지 URI
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private boolean lastMessageRead;
}
