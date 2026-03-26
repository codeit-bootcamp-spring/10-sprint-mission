package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAlreadyExistsException extends UserException {
  public UserAlreadyExistsException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  public static UserAlreadyExistsException byEmail(String email) {
    return new UserAlreadyExistsException(ErrorCode.DUPLICATE_USER_EMAIL, Map.of("email", email));
  }

  public static UserAlreadyExistsException byUsername(String username) {
    return new UserAlreadyExistsException(
        ErrorCode.DUPLICATE_USER_USERNAME, Map.of("username", username));
  }
}
