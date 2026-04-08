package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatusCreateRequest {

  @NotEmpty
  private final UUID userId;

  @NotEmpty
  private final UUID channelId;

  @NotEmpty
  private final Instant lastReadAt;

  public ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }
}
