package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // User
  USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
  DUPLICATE_USERNAME("중복된 username입니다"),
  DUPLICATE_EMAIL("중복된 email입니다"),

  // UserStatus
  USER_STATUS_NOT_FOUND("userStatus를 찾을 수 없습니다"),
  USER_STATUS_ALREADY_EXISTS("이미 존재하는 userStatus입니다"),

  // Channel
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다"),
  DUPLICATE_NAME("중복된 채널명 입니다"),
  PRIVATE_CHANNEL_NOT_EDITABLE("Private 채널은 수정할 수 없습니다"),

  // BinaryContent
  BINARY_CONTENT_NOT_FOUND("존재하지 않는 binaryContent입니다"),

  // Message
  MESSAGE_NOT_FOUND("존재하지 않는 메시지입니다"),

  // ReadStatus
  READ_STATUS_ALREADY_EXISTS("이미 존재하는 readStatus입니다"),
  READ_STATUS_NOT_FOUND("존재하지 않는 readStatus입니다");


  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getCode() {
    return this.name();
  }
}
