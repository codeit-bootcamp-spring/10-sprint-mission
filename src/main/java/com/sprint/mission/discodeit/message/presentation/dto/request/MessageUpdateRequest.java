package com.sprint.mission.discodeit.message.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MessageUpdateRequest {

  @NotEmpty
  private final String content;

  @JsonCreator
  public MessageUpdateRequest(@JsonProperty("newContent") String content) {
    this.content = content;
  }
}
