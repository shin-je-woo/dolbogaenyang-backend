package com.whatpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiDocTag {

    AUTHENTICATION("Authentication"),
    MEMBER("Members"),
    PROJECT("Projects"),
    PROJECT_COMMENT("Project Comments"),
    PROJECT_LIKE("Project Likes"),
    PROJECT_APPLY("Project Apply"),
    PROJECT_PARTICIPANT("Project Participants"),
    ATTACHMENT("Attachments"),
    CHAT_ROOM("Chat Rooms"),
    CHAT_MESSAGE("Chat Messages"),
    DOMAIN("Domains"),
    IMAGE("Images");

    private final String tag;
}
