package com.sprint.mission.discodeit.common.exception;

public interface ErrorCode {

  int getStatus();

  String getCode();

  String getMessage();
}
