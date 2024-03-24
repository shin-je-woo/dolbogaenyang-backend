package com.whatpl.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Skill {

    JAVA("자바!_!"),
    KOTLIN("코틀린~~"),
    SPRING("스프링!!");

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
