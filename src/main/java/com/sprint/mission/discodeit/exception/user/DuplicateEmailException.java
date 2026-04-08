package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateEmailException extends UserException {

  public DuplicateEmailException() {
    super(ErrorCode.DUPLICATE_EMAIL);
  }
}
