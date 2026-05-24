package com.sprint.mission.discodeit.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RoleUpdateRequest {

  @NotNull
  private final UUID userId;
  @NotNull
  private final UserRole role;

  public RoleUpdateRequest(
      @JsonProperty("userId") UUID userId,
      @JsonProperty("newRole") UserRole role) {
    this.userId = userId;
    this.role = role;
  }
}
