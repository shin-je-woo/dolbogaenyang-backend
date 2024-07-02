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
public enum ApplyStatus implements WhatplGlobalDomain {

    WAITING("승인 대기"),
    ACCEPTED("승인 완료"),
    REJECTED("승인 거절"),
    EXCLUDED("제외");

    @JsonValue
    private final String value;

    @JsonCreator
    public static ApplyStatus from(String value) {
        return Arrays.stream(ApplyStatus.values())
                .filter(applyStatus -> applyStatus.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.APPLY_STATUS_NOT_VALID));
    }
}
