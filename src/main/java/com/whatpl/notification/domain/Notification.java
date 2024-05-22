package com.whatpl.notification.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.member.domain.Member;
import com.whatpl.notification.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @Builder
    public Notification(NotificationType type, Boolean isRead, Member receiver) {
        this.type = type;
        this.isRead = isRead != null && isRead;
        this.receiver = receiver;
    }

    public static Notification of(NotificationType type, Member receiver) {
        return new Notification(type, false, receiver);
    }
}
