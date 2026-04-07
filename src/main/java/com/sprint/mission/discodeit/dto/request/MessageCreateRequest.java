package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(

    String content,

    @NotNull
    UUID channelId,

    UUID authorId
) {

}
