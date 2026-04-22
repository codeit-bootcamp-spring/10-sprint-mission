package com.sprint.mission.discodeit.exception;

public interface ErrorCode {
  int getStatus();
  String getCode();
  String getMessage();
}
