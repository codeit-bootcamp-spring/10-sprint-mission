package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "content는 필수입니다.")
    String content,
    @NotNull(message = "channelId는 필수입니다.")
    UUID channelId,
    @NotNull(message = "authorId는 필수입니다.")
    UUID authorId
) {

}
