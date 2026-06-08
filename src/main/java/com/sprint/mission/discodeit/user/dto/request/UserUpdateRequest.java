package com.sprint.mission.discodeit.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.global.validation.UpdateSafe;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.validation.constraints.Email;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO for {@link User}
 */
@Getter
@ToString
public final class UserUpdateRequest {

  @UpdateSafe
  private final String username;

  @UpdateSafe
  @Email
  private final String email;

  @UpdateSafe
  private final String password;

  private final UUID profileId;

  @JsonCreator
  public UserUpdateRequest(
      @JsonProperty("newUsername") String username,
      @JsonProperty("newEmail") String email,
      @JsonProperty("newPassword") String password,
      @JsonProperty("newProfileId") UUID profileId) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }

}
