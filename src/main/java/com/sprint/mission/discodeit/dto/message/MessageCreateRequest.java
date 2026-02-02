package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record MessageCreateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID channelId,

        @NotNull(message = "ID가 null입니다.")
        UUID authorId,

        @NotBlank(message = "content가 입력되지 않았습니다.")
        String content,
        BinaryContentCreateRequest[] attachments
) {
    public MessageCreateRequest {
        if (attachments != null) {
            attachments = attachments.clone();
        }
    }
}
