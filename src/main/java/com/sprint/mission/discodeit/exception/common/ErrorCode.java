package com.sprint.mission.discodeit.exception.common;

public interface ErrorCode {
  int getStatus();
  String getCode();
  String getMessage();
}
