package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;

@Getter
public final class UserStatusUpdateRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1234L;

  @NotNull
  private final Instant lastActiveAt;

  @JsonCreator
  public UserStatusUpdateRequest(
      @JsonProperty("newLastActiveAt") Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  @Override
  public String toString() {
    return "UserStatusUpdateRequest[" +
        "lastActiveAt=" + lastActiveAt + ']';
  }
}
