package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public final class UserStatusUpdateRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1234L;

  @DateTimeFormat
  @NotNull
  private final LocalDateTime lastActiveAt;

  @JsonCreator
  public UserStatusUpdateRequest(
      @JsonProperty("lastActiveAt") LocalDateTime lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  @Override
  public String toString() {
    return "UserStatusUpdateRequest[" +
        "lastActiveAt=" + lastActiveAt + ']';
  }
}
