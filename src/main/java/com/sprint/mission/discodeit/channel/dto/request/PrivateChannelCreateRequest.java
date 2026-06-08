package com.sprint.mission.discodeit.channel.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.channel.entity.constant.ChannelType;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class PrivateChannelCreateRequest {

  private final ChannelType type = ChannelType.PRIVATE;

  @NotEmpty
  private final List<UUID> participantIds;

  @JsonCreator
  public PrivateChannelCreateRequest(
      @JsonProperty("participantIds") List<UUID> participantIds) {
    this.participantIds = participantIds;
  }
}
