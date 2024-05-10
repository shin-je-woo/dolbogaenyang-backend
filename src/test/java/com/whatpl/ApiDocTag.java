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
    ATTACHMENT("Attachments"),
    CHAT("Chats"),
    DOMAIN("Domains");

    private final String tag;
}
