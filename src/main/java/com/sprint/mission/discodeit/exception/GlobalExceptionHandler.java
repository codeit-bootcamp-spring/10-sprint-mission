package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        log.warn("비즈니스 예외 발생 - {}: {}", e.getErrorCode().name(), e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(e.getClass().getSimpleName()) // 예: UserNotFoundException
            .status(e.getErrorCode().getStatus().value())
            .build();

        return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
    }

    // 입력값 검증 예외: @Valid 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("입력값 검증 실패: {}", fieldErrors);

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.INVALID_INPUT_VALUE.name())
            .message(ErrorCode.INVALID_INPUT_VALUE.getMessage())
            .details(fieldErrors)
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()) // 400
            .build();

        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()).body(response);
    }

    // JSON 파싱 에러
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("잘못된 HTTP 요청 메세지: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.INVALID_INPUT_VALUE.name())
            .message("잘못된 요청 형식입니다. 데이터를 확인해주세요.")
            .exceptionType(e.getClass().getSimpleName())
            .status(HttpStatus.BAD_REQUEST.value()) // 400
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 지원하지 않는 HTTP 메서드 예외
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("지원하지 않는 HTTP 메서드 요청: {}", e.getMethod());

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.METHOD_NOT_ALLOWED.name())
            .message(ErrorCode.METHOD_NOT_ALLOWED.getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus().value()) // 405
            .build();

        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus()).body(response);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        log.warn("리소스를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.RESOURCE_NOT_FOUND.name())
            .message(e.getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus().value()) // 404
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 인자값 전달: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.INVALID_INPUT_VALUE.name())
            .message(e.getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()) // 400
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 404 not found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("요청 경로를 찾을 수 없음: {}", e.getResourcePath());

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.RESOURCE_NOT_FOUND.name())
            .message("요청하신 URL을 찾을 수 없습니다. 경로를 확인해주세요.")
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus().value()) // 404
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 권한 부족 예외: @PreAuthorize 등에서 인가 실패 시
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("권한 부족으로 접근 거부: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.ACCESS_DENIED.name())
            .message(ErrorCode.ACCESS_DENIED.getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.ACCESS_DENIED.getStatus().value()) // 403
            .build();

        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getStatus()).body(response);
    }

    // 서버 에러: 최상위 예외 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("서버 내부 미처리 오류 발생: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.INTERNAL_SERVER_ERROR.name())
            .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value()) // 500
            .build();

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }

}