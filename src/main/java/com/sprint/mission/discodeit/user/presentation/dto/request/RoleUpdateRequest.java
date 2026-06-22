package com.sprint.mission.discodeit.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RoleUpdateRequest {

  @NotNull
  private final UUID id;
  @NotNull
  private final UserRole role;

  public RoleUpdateRequest(
      @JsonProperty("userId") UUID id,
      @JsonProperty("newRole") UserRole role) {
    this.id = id;
    this.role = role;
  }
}
