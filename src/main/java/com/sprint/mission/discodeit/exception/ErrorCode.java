package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "애플리케이션 공통 에러 코드")
public enum ErrorCode {

  // 400
  @Schema(description = "요청이 올바르지 않습니다. (400)")
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다."),

  @Schema(description = "검증에 실패했습니다. (400)")
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "검증에 실패했습니다."),

  @Schema(description = "사용 중인 이름입니다. (400)")
  USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "사용 중인 이름입니다."),

  @Schema(description = "사용 중인 이메일입니다. (400)")
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "사용 중인 이메일입니다."),

  @Schema(description = "유저 상태 정보가 이미 존재합니다. (400)")
  USER_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "유저 상태 정보가 이미 존재합니다."),

  @Schema(description = "메시지 읽음 상태가 이미 존재합니다. (400)")
  READ_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "메시지 읽음 상태가 이미 존재합니다."),

  @Schema(description = "유저 이름 또는 비밀번호가 올바르지 않습니다. (400)")
  INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "유저 이름 또는 비밀번호가 올바르지 않습니다."),

  @Schema(description = "비공개 채널은 수정할 수 없습니다. (400)")
  PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "비공개 채널은 수정할 수 없습니다."),

  // 401
  @Schema(description = "인증이 필요합니다. (401)")
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

  // 403
  @Schema(description = "접근 권한이 없습니다. (403)")
  FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

  // 404
  @Schema(description = "유저를 찾을 수 없습니다. (404)")
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

  @Schema(description = "채널을 찾을 수 없습니다. (404)")
  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),

  @Schema(description = "메시지를 찾을 수 없습니다. (404)")
  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),

  @Schema(description = "파일을 찾을 수 없습니다. (404)")
  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

  @Schema(description = "유저 상태 정보를 찾을 수 없습니다. (404)")
  USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 상태 정보를 찾을 수 없습니다."),

  @Schema(description = "메시지 읽음 정보를 찾을 수 없습니다. (404)")
  READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지 읽음 정보를 찾을 수 없습니다."),

  // 409
  @Schema(description = "요청이 충돌했습니다. (409)")
  CONFLICT(HttpStatus.CONFLICT, "요청이 충돌했습니다."),

  // 500
  @Schema(description = "로컬 파일 저장소 초기화에 실패했습니다. (500)")
  BINARY_CONTENT_STORAGE_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로컬 파일 저장소 초기화에 실패했습니다."),

  @Schema(description = "파일 저장에 실패했습니다. (500)")
  BINARY_CONTENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),

  @Schema(description = "파일 조회에 실패했습니다. (500)")
  BINARY_CONTENT_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 조회에 실패했습니다."),

  @Schema(description = "파일 삭제에 실패했습니다. (500)")
  BINARY_CONTENT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),

  @Schema(description = "서버 내부 오류가 발생했습니다. (500)")
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String defaultMessage;

  ErrorCode(HttpStatus httpStatus, String defaultMessage) {
    this.httpStatus = httpStatus;
    this.defaultMessage = defaultMessage;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }
}