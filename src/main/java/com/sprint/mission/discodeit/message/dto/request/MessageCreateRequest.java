package com.sprint.mission.discodeit.message.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MessageCreateRequest {

  @NotEmpty
  private final String content;

  @NotNull
  private final UUID channelId;

  @NotNull
  private final UUID authorId;

  private final List<UUID> attachmentIds;

  @JsonCreator
  public MessageCreateRequest(
      @JsonProperty("content") String content,
      @JsonProperty("channelId") UUID channelId,
      @JsonProperty("authorId") UUID authorId,
      @JsonProperty("attachmentIds") List<UUID> attachmentIds) {
    this.content = content;
    this.channelId = channelId;
    this.authorId = authorId;
    this.attachmentIds = attachmentIds;
  }
}
