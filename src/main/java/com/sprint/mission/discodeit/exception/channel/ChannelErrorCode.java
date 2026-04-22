package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelErrorCode implements ErrorCode {
  CHANNELID_NOT_FOUND(404, "C001", "Channel id not found"),
  NO_MESSAGE_IN_CHANNEL(404, "C002", "channel have no messages"),
  PRIVATE_CHANNEL_CANT_BE_UPDATED(400, "C003", "Private Channel can't be updated"),
  PUBLIC_CHANNEL_CANT_ADD_USER(400, "C004", "Public Channel can't add user"),
  ;

  private final int status;
  private final String code;
  private final String message;
}
