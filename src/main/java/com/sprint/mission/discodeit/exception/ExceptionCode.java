package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

  USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
  DUPLICATE_USERNAME(409, "이미 사용중인 유저 이름입니다."),
  DUPLICATE_EMAIL(409, "이미 사용중인 이메일입니다."),
  PASSWORD_MISMATCH(400, "비밀번호가 일치하지 않습니다."),

  CHANNEL_NOT_FOUND(404, "채널을 찾을 수 없습니다."),
  CANNOT_UPDATE_PRIVATE_CHANNEL(400, "PRIVATE 채널은 수정할 수 없습니다."),
  NOT_A_CHANNEL_PARTICIPANT(403, "비공개 채널은 참여자만 메시지를 작성할 수 있습니다."),

  MESSAGE_NOT_FOUND(404, "메시지를 찾을 수 없습니다."),
  READ_STATUS_NOT_FOUND(404, "수신 정보를 찾을 수 없습니다."),
  READ_STATUS_ALREADY_EXISTS(409, "이미 수신 정보가 존재합니다."),
  USER_STATUS_NOT_FOUND(404, "유저 상태를 찾을 수 없습니다."),
  USER_STATUS_ALREADY_EXISTS(409, "이미 유저 상태가 존재합니다."),
  BINARY_CONTENT_NOT_FOUND(404, "첨부 파일이 존재하지 않습니다."),
  BINARY_CONTENT_UPLOAD_FAILED(500, "첨부 파일 저장 중 오류가 발생했습니다."),
  BINARY_CONTENT_DOWNLOAD_FAILED(500, "첨부 파일을 읽는 도중 오류가 발생했습니다."),
  STORAGE_INITIALIZATION_FAILED(500, "저장소 시스템을 초기화할 수 없습니다.");

  private final int statusCode;
  private final String message;

  ExceptionCode(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }
}
