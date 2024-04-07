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
public enum Job {

    PLANNER("기획자"),
    DESIGNER("UX/UI 디자이너"),
    WEB_DEVELOPER("웹 개발자"),
    MOBILE_DEVELOPER("모바일 개발자"),
    BACKEND_DEVELOPER("백엔드 개발자"),
    DEVOPS_DEVELOPER("DevOps 개발자"),
    DATA_SCIENTIST("데이터 사이언티스트");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Job from(String value) {
        return Arrays.stream(Job.values())
                .filter(job -> job.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.JOB_NOT_VALID));
    }
}
