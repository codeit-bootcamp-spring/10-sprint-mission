package com.sprint.mission.discodeit.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  //User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
  USER_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 유저입니다."),

  //Channel
  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채널입니다."),
  CHANNEL_CANT_UPDATE_PRIVATE(HttpStatus.BAD_REQUEST, "private 채널은 수정할 수 없습니다."),

  //Message
  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다."),

  //BinaryContent
  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다"),

  //Auth
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");


  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }


}
