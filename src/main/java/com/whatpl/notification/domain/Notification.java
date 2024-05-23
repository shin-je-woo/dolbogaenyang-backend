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

    private String relatedUri;

    @Builder
    public Notification(NotificationType type, Member receiver, String relatedUri) {
        this.type = type;
        this.isRead = false;
        this.receiver = receiver;
        this.relatedUri = relatedUri;
    }

    public static Notification of(NotificationType type, Member receiver, String relatedUri) {
        return Notification.builder()
                .type(type)
                .receiver(receiver)
                .relatedUri(relatedUri)
                .build();
    }
}
