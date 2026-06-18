package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUsernameException extends UserException {

  public DuplicateUsernameException() {
    super(ErrorCode.DUPLICATE_USERNAME);
  }
}
