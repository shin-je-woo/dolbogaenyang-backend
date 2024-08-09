package com.whatpl.global.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum Career implements WhatplGlobalDomain {

    NONE("취업 준비중"),
    ONE("1년차"),
    TWO("2년차"),
    THREE("3년차"),
    FOUR("4년차"),
    FIVE("5년차"),
    SIX("6년차");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Career from(String value) {
        return Arrays.stream(Career.values())
                .filter(career -> career.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.CAREER_NOT_VALID));
    }

    public static Career ifNotMatched(String value, Supplier<Career> careerSupplier) {
        return Arrays.stream(Career.values())
                .filter(career -> career.getValue().equals(value))
                .findFirst()
                .orElseGet(careerSupplier);
    }
}
