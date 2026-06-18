package com.sprint.mission.discodeit.sse;

import lombok.Getter;

@Getter
public enum SseMessageType {
  // 공용
  CONNECTED("connected"),
  PING("ping"),

  NOTIFICATION_CREATED("notifications.created"),
  BINARY_CONTENTS_UPDATED("binaryContents.updated"),
  CHANNELS_CREATED("channels.created"),
  CHANNELS_UPDATED("channels.updated"),
  CHANNELS_DELETED("channels.deleted"),
  USERS_CREATED("users.created"),
  USERS_UPDATED("users.updated"),
  USERS_DELETED("users.deleted");

  private final String value;

  SseMessageType(String value) {
    this.value = value;
  }
}
