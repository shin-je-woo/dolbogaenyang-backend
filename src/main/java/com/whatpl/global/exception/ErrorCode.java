package com.whatpl.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // TOKEN
    INVALID_TOKEN("TKN1", 401, "토큰정보가 유효하지 않습니다."),
    EXPIRED_TOKEN("TKN2", 401, "만료된 토큰입니다."),
    MALFORMED_TOKEN("TKN3", 401, "잘못된 형식의 토큰입니다."),
    INVALID_SIGNATURE("TKN4", 401, "변조된 토큰입니다."),

    // MEMBER
    NOT_FOUND_MEMBER("MBR1", 404, "사용자를 찾을 수 없습니다."),
    LOGIN_FAILED("MBR2", 401, "로그인에 실패했습니다."),
    NO_AUTHENTICATION("MBR3", 401, "인증되지 않은 사용자입니다."),
    NO_AUTHORIZATION("MBR4", 403, "접근권한이 없습니다."),
    HAS_NO_PROFILE("MBR5", 401, "프로필이 작성되지 않은 사용자입니다."),
    MAX_PORTFOLIO_SIZE_EXCEED("MBR6", 400, "포트폴리오는 최대 5개 첨부 가능합니다."),
    MAX_REFERENCE_SIZE_EXCEED("MBR7", 400, "참고링크는 최대 5개 첨부 가능합니다."),
    MAX_SUBJECT_SIZE_EXCEED("MBR8", 400, "관심주제는 최대 6개 입력 가능합니다."),
    MAX_SKILL_SIZE_EXCEED("MBR9", 400, "기술스택은 최대 10개 입력 가능합니다."),

    // PROJECT
    NOT_FOUND_PROJECT("PRJ1", 404, "프로젝트를 찾을 수 없습니다."),
    WRITER_NOT_APPLY("PRJ2", 400, "프로젝트 등록자는 본인이 등록한 프로젝트에 지원할 수 없습니다."),
    NOT_MATCH_APPLY_JOB_WITH_PROJECT("PRJ3", 400, "지원한 직무가 프로젝트 모집직군에 등록되어 있지 않습니다."),
    RECRUIT_COMPLETED_APPLY_JOB("PRJ4", 400, "해당 직무는 모집이 완료된 직무입니다."),
    DUPLICATED_APPLY("PRJ5", 400, "이미 지원한 프로젝트입니다."),
    COMPLETED_RECRUITMENT("PRJ6", 400, "모집완료된 프로젝트입니다."),
    DELETED_PROJECT("PRJ7", 400, "삭제된 프로젝트입니다."),
    NOT_MATCH_PROJECT_APPLY("PRJ8", 400, "프로젝트 ID와 지원서 ID가 일치하지 않습니다."),
    ALREADY_PROCESSED_APPLY("PRJ9", 400, "이미 승인 또는 거절된 지원서입니다."),
    CANT_PROCESS_WAITING("PRJ10", 400, "프로젝트 지원서를 대기 상태로 변경할 수 없습니다."),
    NOT_FOUND_PARENT_PROJECT_COMMENT("PRJ11", 404, "상위 댓글을 찾을 수 없습니다."),
    NOT_FOUND_PROJECT_COMMENT("PRJ12", 404, "댓글을 찾을 수 없습니다."),
    NOT_MATCH_PROJECT_LIKE("PRJ13", 400, "프로젝트 ID와 좋아요 ID가 일치하지 않습니다."),
    NOT_FOUND_PROJECT_PARTICIPANT("PRJ14", 404, "프로젝트 참여자를 찾을 수 없습니다."),
    NOT_MATCH_PROJECT_PARTICIPANT("PRJ15", 400, "프로젝트 ID와 참여자 ID가 일치하지 않습니다."),
    CANT_PROCESS_EXCLUDED("PRJ16", 400, "프로젝트 지원서를 제외 상태로 변경할 수 없습니다."),

    // APPLY
    NOT_FOUND_APPLY("APL1", 404, "지원정보를 찾을 수 없습니다."),

    // FILE
    NOT_FOUND_FILE("FILE1", 404, "파일을 찾을 수 없습니다."),
    FILE_SIZE_EXCEED("FILE2", 400, "파일 사이즈를 초과하였습니다."),
    FILE_TYPE_NOT_ALLOWED("FILE3", 400, "허용되지 않은 파일 타입입니다."),
    NOT_IMAGE_FILE("FILE4", 400, "이미지 파일이 아닙니다."),

    // CHAT
    NOT_FOUND_CHAT_ROOM("CHT1", 404, "채팅방을 찾을 수 없습니다."),

    // COMMON
    GLOBAL_EXCEPTION("CMM1", 500, "서버에서 에러가 발생하였습니다."),
    NOT_FOUND_DATA("CMM2", 404, "데이터가 존재하지 않습니다."),
    MISSING_PARAMETER("CMM3", 400, "필수 파라미터가 존재하지 않습니다."),
    NOT_FOUND_API("CMM4", 404, "요청하신 API를 찾을 수 없습니다."),
    REQUEST_VALUE_INVALID("CMM5", 400, "입력값이 올바르지 않습니다."),
    REQUIRED_PARAMETER_MISSING("CMM6", 400, "필수 파라미터가 존재하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE("CMM7", 400, "요청 메시지를 읽을 수 없습니다. 요청 형식을 확인해 주세요."),
    ACCESS_DENIED("CMM8", 403, "해당 요청에 대한 접근 권한이 없습니다."),
    NOT_SUPPORTED_METHOD("CMM9", 400, "지원하지 않는 HTTP METHOD 입니다."),

    // DOMAIN
    SKILL_NOT_VALID("DMN1", 400, "기술스택에 유효하지 않은 값이 존재합니다."),
    JOB_NOT_VALID("DMN2", 400, "직무에 유효하지 않은 값이 존재합니다."),
    CAREER_NOT_VALID("DMN3", 400, "경력에 유효하지 않은 값이 존재합니다."),
    SUBJECT_NOT_VALID("DMN4", 400, "주제에 유효하지 않은 값이 존재합니다."),
    WORK_TIME_NOT_VALID("DMN5", 400, "작업시간에 유효하지 않은 값이 존재합니다."),
    UP_DOWN_NOT_VALID("DMN6", 400, "이상/이하에 유효하지 않은 값이 존재합니다."),
    MEETING_TYPE_NOT_VALID("DMN7", 400, "모임 방식에 유효하지 않은 값이 존재합니다."),
    APPLY_STATUS_NOT_VALID("DMN8", 400, "프로젝트 지원 상태에 유효하지 않은 값이 존재합니다."),
    PROJECT_STATUS_NOT_VALID("DMN9", 400, "프로젝트 상태에 유효하지 않은 값이 존재합니다.");

    private final String code;
    private final int status;
    private final String message;
}
