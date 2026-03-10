package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
    UUID channelId,

    @JsonProperty("authorId")
    UUID authorId,

    String content,

    @JsonIgnore
    List<UUID> attachmentIds
) {

}