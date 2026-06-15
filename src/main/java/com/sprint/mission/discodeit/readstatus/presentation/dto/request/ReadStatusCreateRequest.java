package com.sprint.mission.discodeit.readstatus.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatusCreateRequest {

  @NotNull
  private final UUID userId;

  @NotNull
  private final UUID channelId;

  @NotNull
  private final Instant lastReadAt;

  public ReadStatusCreateRequest(UUID userId,
      UUID channelId,
      Instant lastReadAt) {
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }
}
