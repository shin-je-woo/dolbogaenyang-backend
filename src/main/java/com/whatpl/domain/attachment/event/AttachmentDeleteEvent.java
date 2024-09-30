package com.whatpl.domain.attachment.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttachmentDeleteEvent {

    private final String storedName;

    public static AttachmentDeleteEvent from(final String storedName) {
        return new AttachmentDeleteEvent(storedName);
    }
}
