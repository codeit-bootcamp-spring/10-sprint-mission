package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.common.validator.UpdateSafe;
import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.constraints.Email;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;

/**
 * DTO for {@link User}
 */
@Getter
public final class UserUpdateRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1234L;

  @UpdateSafe
  private final String username;

  @UpdateSafe
  @Email
  private final String email;

  @UpdateSafe
  private final String password;

  @JsonCreator
  public UserUpdateRequest(
      @JsonProperty("username") String username,
      @JsonProperty("email") String email,
      @JsonProperty("password") String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  @Override
  public String toString() {
    return "UserUpdateRequest[" +
        "username=" + username + ", " +
        "email=" + email + ", " +
        "password=" + password + ']';
  }

}
