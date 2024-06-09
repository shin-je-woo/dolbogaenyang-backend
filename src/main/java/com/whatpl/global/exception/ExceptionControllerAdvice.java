package com.whatpl.global.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.GLOBAL_EXCEPTION);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ErrorResponse> handleBizException(BizException e) {
        log.error("handleBizException", e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("handleMissingServletRequestParameterException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MISSING_PARAMETER);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("handleMaxUploadSizeExceededException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FILE_SIZE_EXCEED);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND_API);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CONSTRAINT")
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DetailErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REQUEST_VALUE_INVALID);
        List<DetailErrorResponse.DetailError> detailErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> DetailErrorResponse.DetailError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue())
                        .reason(buildReason(error))
                        .build())
                .toList();

        return new ResponseEntity<>(new DetailErrorResponse(errorResponse, detailErrors), errorResponse.getStatus());
    }

    /**
     * FieldError 가 BindingFailure 이면 type-mismatch 와 같은 에러
     * 아니면 validation 에러
     * (like a type mismatch); else, it is a validation failure
     *
     * @param fieldError the filedError
     */
    private String buildReason(FieldError fieldError) {
        return fieldError.isBindingFailure() ? "입력 값을 확인해 주세요." : fieldError.getDefaultMessage();
    }

    /**
     * HttpMessageConverter 에서 컨버팅 실패하면 발생
     * Thrown by HttpMessageConverter implementations when the HttpMessageConverter.read(java.lang.Class<? extends T>, org.springframework.http.HttpInputMessage) method fails.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(HttpMessageNotReadableException e) {
        if (!(e.getRootCause() instanceof BizException bizException)) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
            return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
        }
        return handleBizException(bizException);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        ErrorCode requiredParameterMissing = ErrorCode.REQUIRED_PARAMETER_MISSING;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(requiredParameterMissing.getCode())
                .message(String.format("%s {%s}", requiredParameterMissing.getMessage(), e.getRequestPartName()))
                .status(requiredParameterMissing.getStatus())
                .build();
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ACCESS_DENIED);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_SUPPORTED_METHOD);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}
