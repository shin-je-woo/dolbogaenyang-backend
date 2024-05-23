package com.whatpl.notification.dto;

import com.whatpl.notification.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationEvent {

    private final long receiverId;
    private final NotificationType notificationType;
    private final long relatedId;

    @Builder
    public NotificationEvent(long receiverId, NotificationType notificationType, long relatedId) {
        this.receiverId = receiverId;
        this.notificationType = notificationType;
        this.relatedId = relatedId;
    }

    public static NotificationEvent of(long receiverId, NotificationType notificationType, long relatedId) {
        return NotificationEvent.builder()
                .receiverId(receiverId)
                .notificationType(notificationType)
                .relatedId(relatedId)
                .build();
    }
}
