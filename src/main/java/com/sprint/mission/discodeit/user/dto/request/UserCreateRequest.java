package com.sprint.mission.discodeit.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.global.validation.UpdateSafe;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO for {@link User}
 */
@Getter
@ToString
public final class UserCreateRequest {

  @UpdateSafe
  @NotEmpty
  private final String username;

  @UpdateSafe
  @NotEmpty
  @Email
  private final String email;

  @UpdateSafe
  @NotEmpty
  private final String password;

  private final UserRole role = UserRole.USER;

  @JsonCreator
  public UserCreateRequest(
      @JsonProperty("username") String username,
      @JsonProperty("email") String email,
      @JsonProperty("password") String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

}
