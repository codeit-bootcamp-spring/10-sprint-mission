package com.sprint.mission.discodeit.readstatus.presentation.dto.request;

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
  @NotNull
  private final boolean notificationEnabled;

  @JsonCreator
  public ReadStatusUpdateRequest(
      @JsonProperty("newLastReadAt") Instant lastReadAt,
      @JsonProperty("newNotificationEnabled") boolean notificationEnabled) {
    this.lastReadAt = lastReadAt;
    this.notificationEnabled = notificationEnabled;
  }
}
