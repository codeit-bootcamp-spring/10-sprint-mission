package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PublicChannelCreateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID ownerId,

        @NotBlank(message = "channelName이 입력되지 않았습니다.")
        @Size(min = 1, max = 20)
        String channelName,

        @NotBlank(message = "channelDescription이 입력되지 않았습니다.")
        @Size(max = 100)
        String channelDescription
) {
}
