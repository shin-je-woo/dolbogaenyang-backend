package com.whatpl.domain.chat.dto;

import com.whatpl.global.pagination.SliceElement;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto implements SliceElement {
    private long messageId;
    private String content;
    private long senderId;
    private String senderNickname;
    private Long senderPictureId;
    private String senderPictureUrl;
    private LocalDateTime sendAt;
    private LocalDateTime readAt;

    public void assignSenderPictureUrl(String senderPictureUrl) {
        this.senderPictureUrl = senderPictureUrl;
    }
}
