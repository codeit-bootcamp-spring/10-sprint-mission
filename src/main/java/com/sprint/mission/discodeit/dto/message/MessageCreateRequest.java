package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메시지를 입력해주세요.")
    String content,

    @NotNull(message = "채널을 확인할 수 없습니다.")
    UUID channelId,

    @NotNull(message = "작성자를 확인할 수 없습니다.")
    UUID authorId
) {

}
