package com.whatpl.global.common.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Skill implements WhatplGlobalDomain {

    JAVA_SCRIPT("JavaScript"),
    TYPE_SCRIPT("TypeScript"),
    REACT("React"),
    VUE("Vue"),
    NEXT("Next.js"),
    NUXT("Nuxt.js"),
    JAVA("Java"),
    KOTLIN("Kotlin"),
    SPRING("Spring"),
    NODE("Node.js"),
    NEST("Nest.js"),
    PYTHON("Python"),
    DJANGO("Django"),
    MYSQL("MySQL"),
    MARIADB("MariaDB"),
    MONGO("MongoDB"),
    FIREBASE("Firebase"),
    SWIFT("Swift"),
    FLUTTER("Flutter"),
    REACT_NATIVE("Reactive Native (RN)"),
    FIGMA("Figma"),
    ZEPLIN("Zeplin"),
    NOTION("Notion"),
    JIRA("Jira"),
    ADOBE_XD("Adobe XD"),
    SLACK("Slack");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Skill from(String value) {
        return Arrays.stream(Skill.values())
                .filter(skill -> skill.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.SKILL_NOT_VALID));
    }
}
