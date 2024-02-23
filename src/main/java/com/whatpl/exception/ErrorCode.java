package com.whatpl.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // TOKEN
    INVALID_TOKEN("TKN1", 401, "토큰정보가 유효하지 않습니다."),
    EXPIRED_TOKEN("TKN2", 401, "만료된 토큰입니다."),

    // ACCOUNT
    NOT_FOUND_ACCOUNT("ACC1", 400, "사용자를 찾을 수 없습니다."),

    // COMMON
    GLOBAL_EXCEPTION("CMM1", 500, "서버에서 에러가 발생하였습니다."),
    NOT_FOUND_DATA("CMM2", 400, "데이터가 존재하지 않습니다.");

    private final String code;
    private final int status;
    private final String message;
}
