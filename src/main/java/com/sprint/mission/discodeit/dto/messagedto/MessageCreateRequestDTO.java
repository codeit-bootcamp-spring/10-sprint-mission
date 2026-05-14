package com.sprint.mission.discodeit.dto.messagedto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequestDTO(
    String content,
    @NotNull(message = "채널 ID는 공백일 수 없습니다!")
    UUID channelId,

    @NotNull(message = "작성자 ID는 공백일 수 없습니다!")
    UUID authorId
) {

}
