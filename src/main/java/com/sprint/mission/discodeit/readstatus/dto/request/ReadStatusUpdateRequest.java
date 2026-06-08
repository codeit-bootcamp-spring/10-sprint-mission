package com.sprint.mission.discodeit.readstatus.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatusUpdateRequest {

  @NotNull
  private final Instant lastReadAt;

  @JsonCreator
  public ReadStatusUpdateRequest(@JsonProperty("newLastReadAt") Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }
}
