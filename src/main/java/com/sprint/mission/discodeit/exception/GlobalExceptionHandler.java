package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserNameExists(UserNameAlreadyExistsException e) {
        // 강사님 피드백 반영: 여기서 로깅!
        log.warn("사용자 생성 실패 - 이미 존재하는 닉네임: {}", e.getMessage(), e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e));
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistException e) {
        log.warn("사용자 생성 실패 - 이미 존재하는 이메일: {}", e.getMessage(), e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(UserNotFoundException e) {
        log.warn("사용자 삭제 실패 - 존재하지 않는 사용자: {}", e.getMessage(), e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e));
    }

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> details.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        Instant.now(),
                        "VALIDATION_FAILED",
                        "유효성 검증에 실패했습니다.",
                        details,
                        e.getClass().getSimpleName(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("예상치 못한 서버 에러 발생: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        "INTERNAL_SERVER_ERROR",
                        e.getMessage(),
                        null,
                        e.getClass().getSimpleName(),
                        500));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("권한이 없습니다: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        Instant.now(),
                        "FORBIDDEN"
                        , e.getMessage(),
                        null,
                        e.getClass().getSimpleName(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }
}
