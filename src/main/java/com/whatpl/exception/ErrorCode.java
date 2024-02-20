package com.whatpl.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // COMMON
    GLOBAL_EXCEPTION("CMM1", 500, "서버에서 에러가 발생하였습니다."),
    NOT_FOUND_DATA("CMM1", 400, "데이터가 존재하지 않습니다.");

    private final String code;
    private final int status;
    private final String message;
}
