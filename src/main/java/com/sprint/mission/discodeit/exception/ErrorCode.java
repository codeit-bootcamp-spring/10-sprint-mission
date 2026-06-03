package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // === 인증 및 인가 관련 ===
  UNAUTHORIZED("A001", "로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED),
  LOGIN_FAILED("A002", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  ACCESS_DENIED("A003", "해당 리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  INVALID_TOKEN("A004", "유효하지 않거나 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
  TOKEN_GENERATION_FAILED("A005", "토큰 생성 중 서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  TOKEN_PARSE_ERROR("A006", "토큰 식별자 추출 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // ==== 유저 관련 ====
  USER_NOT_FOUND("U001", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER("U002", "이미 존재하는 사용자명입니다.", HttpStatus.CONFLICT),
  DUPLICATE_EMAIL("U003", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),

  // ==== 채널 및 참여 관련 ====
  CHANNEL_NOT_FOUND("C001", "해당 채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PRIVATE_CHANNEL_UPDATE("C002", "PRIVATE 채널은 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),
  CHANNEL_ACCESS_DENIED("C003", "채널 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
  ALREADY_PARTICIPATING("C004", "이미 해당 채널에 참여 중인 유저입니다.", HttpStatus.CONFLICT),

  READ_STATUS_NOT_FOUND("C005", "해당 참여 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // ==== 메시지 관련 ====
  MESSAGE_NOT_FOUND("M001", "해당 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // ==== 바이너리 콘텐츠 관련 ====
  FILE_IS_EMPTY("F001", "파일이 비어있습니다.", HttpStatus.BAD_REQUEST),
  BINARY_CONTENT_NOT_FOUND("F002", "해당 바이너리 콘텐츠를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  FILE_UPLOAD_ERROR("F003", "파일 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_DOWNLOAD_ERROR("F004", "파일 다운로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // ==== 공통 ====
  INVALID_INPUT_VALUE("G001", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("G002", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  GLOBAL_ACCESS_DENIED("G003", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

  private final String code;
  private final String message;
  private final HttpStatus status;

  ErrorCode(String code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }

}
