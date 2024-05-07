package com.whatpl.chat.dto;

import com.whatpl.global.pagination.SliceElement;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageDto implements SliceElement {
    private long messageId;
    private String content;
    private long senderId;
    private String senderNickname;
    private String senderProfileImgUri; // TODO 보낸 사람 프로필 이미지 URI
    private LocalDateTime sendAt;
    private LocalDateTime readAt;

    @Builder
    public ChatMessageDto(long messageId, String content, long senderId, String senderNickname, String senderProfileImgUri, LocalDateTime sendAt, LocalDateTime readAt) {
        this.messageId = messageId;
        this.content = content;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImgUri = senderProfileImgUri;
        this.sendAt = sendAt;
        this.readAt = readAt;
    }
}
