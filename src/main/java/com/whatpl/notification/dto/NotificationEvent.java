package com.whatpl.notification.dto;

import com.whatpl.notification.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationEvent {

    private final String title;
    private final NotificationType notificationType;

    @Builder
    public NotificationEvent(String title, NotificationType notificationType) {
        this.title = title;
        this.notificationType = notificationType;
    }

    public static NotificationEvent of(String title, NotificationType notificationType) {
        return NotificationEvent.builder()
                .title(title)
                .notificationType(notificationType)
                .build();
    }
}
