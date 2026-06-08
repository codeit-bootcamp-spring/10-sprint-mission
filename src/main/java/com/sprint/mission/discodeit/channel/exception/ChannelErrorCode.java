package com.sprint.mission.discodeit.channel.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChannelErrorCode implements ErrorCode {
  CHANNELID_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "Channel id not found"),
  NO_MESSAGE_IN_CHANNEL(HttpStatus.NOT_FOUND, "C002", "channel have no messages"),
  PRIVATE_CHANNEL_CANT_BE_UPDATED(HttpStatus.BAD_REQUEST, "C003",
      "Private Channel can't be updated"),
  PUBLIC_CHANNEL_CANT_ADD_USER(HttpStatus.BAD_REQUEST, "C004", "Public Channel can't add user"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}
