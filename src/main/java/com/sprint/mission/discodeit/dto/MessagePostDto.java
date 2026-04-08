package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessagePostDto(
    @NotBlank
    String content,

    @NotNull
    UUID channelId,

    @NotNull
    UUID authorId
) {

}
