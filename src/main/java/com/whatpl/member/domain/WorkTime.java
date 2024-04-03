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
public enum WorkTime {

    LESS_THAN_TEN("10시간 미만"),
    TEN_TO_TWENTY("10시간 이상 20시간 미만"),
    TWENTY_TO_THIRTY("20시간 이상 30시간 미만"),
    THIRTY_TO_FORTY("30시간 이상 40시간 미만");

    @JsonValue
    private final String value;

    @JsonCreator
    public static WorkTime from(String value) {
        return Arrays.stream(WorkTime.values())
                .filter(workTime -> workTime.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.WORK_TIME_NOT_VALID));
    }
}
