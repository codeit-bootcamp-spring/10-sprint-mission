package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Message 생성 정보")
public record MessageCreateRequest(
    UUID channelId,

    @JsonProperty("authorId")
    UUID authorId,

    String content,

    @JsonIgnore
    List<UUID> attachmentIds
) {

}