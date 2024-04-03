package com.whatpl.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DetailErrorResponse extends ErrorResponse {

    private final List<DetailError> detailErrors;

    public DetailErrorResponse(ErrorResponse errorResponse, List<DetailError> detailErrors) {
        super(errorResponse.getMessage(), errorResponse.getCode(), errorResponse.getStatus().value());
        this.detailErrors = detailErrors;
    }

    @Getter
    public static class DetailError {

        private final String field;
        private final Object value;
        private final String reason;

        @Builder
        public DetailError(String field, Object value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }
    }

}
