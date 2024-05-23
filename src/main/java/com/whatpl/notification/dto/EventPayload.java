package com.whatpl.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EventPayload {

    private final int unreadCount;
    private final String title;
    private final String relatedUri;

    @Builder
    public EventPayload(int unreadCount, String title, String relatedUri) {
        this.unreadCount = unreadCount;
        this.title = title;
        this.relatedUri = relatedUri;
    }

    public static EventPayload of(int unreadCount, String title, String relatedUri) {
        return EventPayload.builder()
                .unreadCount(unreadCount)
                .title(title)
                .relatedUri(relatedUri)
                .build();
    }
}
