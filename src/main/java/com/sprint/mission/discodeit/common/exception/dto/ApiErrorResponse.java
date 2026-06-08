package com.sprint.mission.discodeit.common.exception.dto;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@ToString
@Builder
public final class ApiErrorResponse {

  private HttpStatus status;
  private String exceptionType;
  private String code;
  private String message;
  private Map<String, Object> details;
  private LocalDateTime timestamp;

  public static ApiErrorResponse from(DiscodeitException exception) {
    return ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .code(exception.getCode())
        .message(exception.getMessage())
        .details(exception.getDetails())
        .exceptionType(exception.getExceptionType())
        .status(exception.getStatus())
        .build();
  }

  public static ApiErrorResponse from(MethodArgumentNotValidException exception) {
    return ApiErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST)
        .exceptionType(exception.getClass().getSimpleName())
        .code("INVALID_INPUT")
        .message("잘못된 입력값입니다. 확인 후 다시 시도해주세요.")
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static ApiErrorResponse internalServerError() {
    return ApiErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .exceptionType("UnknownException")
        .code("SERVER_ERROR")
        .message("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        .timestamp(LocalDateTime.now())
        .build();
  }
}
