package com.whatpl.domain.chat.domain;

import com.whatpl.global.common.domain.BaseTimeEntity;
import com.whatpl.domain.project.domain.Apply;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_id")
    private Apply apply;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages;

    public ChatRoom(Apply apply) {
        this.apply = apply;
    }

    public static ChatRoom from(final Apply apply) {
        return new ChatRoom(apply);
    }
}
