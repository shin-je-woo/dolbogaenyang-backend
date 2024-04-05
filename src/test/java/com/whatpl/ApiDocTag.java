package com.whatpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiDocTag {

    MEMBER("Members"),
    PROJECT("Projects");

    private final String tag;
}
