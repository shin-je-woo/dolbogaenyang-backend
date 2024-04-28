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
    ATTACHMENT("Attachments"),
    DOMAIN("Domains");

    private final String tag;
}
