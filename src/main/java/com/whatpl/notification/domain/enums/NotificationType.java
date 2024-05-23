package com.whatpl.notification.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    PROJECT_APPLY(""),
    PROJECT_OFFER;

    private final String getUri;
}
