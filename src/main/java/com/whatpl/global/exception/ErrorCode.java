package com.whatpl.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // TOKEN
    INVALID_TOKEN("TKN1", 401, "토큰정보가 유효하지 않습니다."),
    EXPIRED_TOKEN("TKN2", 401, "만료된 토큰입니다."),

    // MEMBER
    NOT_FOUND_MEMBER("MBR1", 404, "사용자를 찾을 수 없습니다."),
    LOGIN_FAILED("MBR2", 401, "로그인에 실패했습니다."),
    NO_AUTHENTICATION("MBR3", 401, "인증되지 않은 사용자입니다."),
    NO_AUTHORIZATION("MBR4", 403, "접근권한이 없습니다."),

    // FILE
    NOT_FOUND_FILE("FILE1", 404, "파일을 찾을 수 없습니다."),
    FILE_SIZE_EXCEED("FILE2", 400, "파일 사이즈를 초과하였습니다."),
    FILE_TYPE_NOT_ALLOWED("FILE3", 400, "허용되지 않은 파일 타입입니다."),

    // COMMON
    GLOBAL_EXCEPTION("CMM1", 500, "서버에서 에러가 발생하였습니다."),
    NOT_FOUND_DATA("CMM2", 404, "데이터가 존재하지 않습니다."),
    MISSING_PARAMETER("CMM3", 400, "필수 파라미터가 존재하지 않습니다."),
    NOT_FOUND_API("CMM4", 404, "요청하신 API를 찾을 수 없습니다."),
    REQUEST_VALUE_INVALID("CMM5", 400, "입력값이 올바르지 않습니다."),
    SKILL_NOT_VALID("CMM6", 400, "기술스택에 유효하지 않은 값이 존재합니다."),
    JOB_NOT_VALID("CMM7", 400, "직무에 유효하지 않은 값이 존재합니다."),
    CAREER_NOT_VALID("CMM7", 400, "경력에 유효하지 않은 값이 존재합니다.");

    private final String code;
    private final int status;
    private final String message;
}
