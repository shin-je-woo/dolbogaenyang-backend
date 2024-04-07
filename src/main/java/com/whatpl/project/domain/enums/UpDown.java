package com.whatpl.project.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UpDown {

    UP("up"),
    DOWN("down");

    @JsonValue
    private final String value;

    @JsonCreator
    public static UpDown from(String value) {
        return Arrays.stream(UpDown.values())
                .filter(upDown -> upDown.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.UP_DOWN_NOT_VALID));
    }
}
