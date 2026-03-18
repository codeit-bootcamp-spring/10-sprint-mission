package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "공통 에러 응답")
public record ErrorDto(

    @Schema(description = "HTTP 상태 코드", example = "401")
    int status,

    @Schema(description = "에러 코드", example = "INVALID_CREDENTIALS")
    String error,

    @Schema(description = "에러 메시지", example = "유저 이름 또는 비밀번호가 올바르지 않습니다.")
    String message,

    @Schema(description = "에러 발생 시각", example = "2026-02-23T05:23:49.657764500Z")
    Instant time

) {

  public static ErrorDto of(int status, String error, String message) {
    return new ErrorDto(status, error, message, Instant.now());
  }

  public static ErrorDto of(ErrorCode code, String message) {
    String msg = (message == null || message.isBlank()) ? code.getDefaultMessage() : message;
    return of(code.getHttpStatus().value(), code.name(), msg);
  }

  public static ErrorDto of(ErrorCode code) {
    return of(code, null);
  }
}
