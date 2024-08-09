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
public enum Job implements WhatplGlobalDomain {

    PLANNER("기획"),
    PM("PM"),
    DESIGNER("디자인"),
    FRONTEND_DEVELOPER("프론트엔드"),
    BACKEND_DEVELOPER("백엔드"),
    ANDROID_DEVELOPER("Android"),
    IOS_DEVELOPER("iOS");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Job from(String value) {
        return Arrays.stream(Job.values())
                .filter(job -> job.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.JOB_NOT_VALID));
    }

    public static Job ifNotMatched(String value, Supplier<Job> jobSupplier) {
        return Arrays.stream(Job.values())
                .filter(job -> job.getValue().equals(value))
                .findFirst()
                .orElseGet(jobSupplier);
    }
}
