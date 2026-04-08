package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class PublicChannelUpdateRequest {

  private final ChannelType type = ChannelType.PUBLIC;

  private final String name;

  private final String description;

  @JsonCreator
  public PublicChannelUpdateRequest(
      @JsonProperty("name") String name,
      @JsonProperty("description") String description) {
    this.name = name;
    this.description = description;
  }
}
