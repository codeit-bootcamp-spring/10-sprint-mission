package com.sprint.mission.discodeit.dto.message.input;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID channelId,
        UUID authorId,
        String content,
        BinaryContentCreateRequest[] attachments
) {
    public MessageCreateInput {
        if (attachments != null) {
            attachments = attachments.clone();
        }
    }
}
