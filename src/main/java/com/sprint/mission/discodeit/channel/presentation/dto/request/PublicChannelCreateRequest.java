package com.sprint.mission.discodeit.channel.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class PublicChannelCreateRequest {

  @NotNull
  private final ChannelType type = ChannelType.PUBLIC;

  @NotEmpty
  private final String name;

  @NotEmpty
  private final String description;

  @JsonCreator
  public PublicChannelCreateRequest(
      @JsonProperty("name") String name,
      @JsonProperty("description") String description) {
    this.name = name;
    this.description = description;
  }
}
