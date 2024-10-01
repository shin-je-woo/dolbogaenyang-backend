package com.whatpl.domain.chat.dto;

import com.whatpl.global.pagination.SliceElement;
import com.whatpl.domain.project.model.ApplyType;
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
    private Long opponentPictureId;
    private String opponentPictureUrl;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private boolean lastMessageRead;

    public void assignOpponentPictureUrl(String opponentPictureUrl) {
        this.opponentPictureUrl = opponentPictureUrl;
    }
}
