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
public enum Subject {

    SOCIAL_MEDIA("소셜 미디어"),
    HEALTH("건강/운동");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Subject from(String value) {
        return Arrays.stream(Subject.values())
                .filter(subject -> subject.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.SUBJECT_NOT_VALID));
    }
}
