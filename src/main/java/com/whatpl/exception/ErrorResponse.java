package com.whatpl.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final HttpStatus status;

    @Builder
    public ErrorResponse(String message, String code, int status) {
        this.message = message;
        this.code = code;
        this.status = HttpStatus.valueOf(status);
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus())
                .build();
    }
}
