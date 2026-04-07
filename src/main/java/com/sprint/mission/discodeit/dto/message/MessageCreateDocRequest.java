package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateDocRequest(

    @Size(max = 255, message = "메시지는 255자 이하입니다.")
    String content,

    @NotNull(message = "채널ID는 필수입니다.")
    UUID channelId,

    @NotNull(message = "사용자ID는 필수입니다.")
    UUID authorId
) {

}
