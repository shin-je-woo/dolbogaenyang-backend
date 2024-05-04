package com.whatpl.chat.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    private String content;

    @Setter
    private LocalDateTime readAt;

    @Builder
    public ChatMessage(Member sender, ChatRoom chatRoom, String content) {
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.content = content;
    }
}
