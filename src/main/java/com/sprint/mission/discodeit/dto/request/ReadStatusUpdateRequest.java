package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatusUpdateRequest {

  @NotEmpty
  private final Instant lastReadAt;

  public ReadStatusUpdateRequest(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }
}
