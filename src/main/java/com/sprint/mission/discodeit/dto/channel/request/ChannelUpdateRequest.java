package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChannelUpdateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID ownerId,

        @Size(min = 1, max = 20)
        @Pattern(regexp = "^\\S.*$", message = "channelName은 공백으로 시작할 수 없습니다.")
        String channelName,

        @Size(max = 100)
        @Pattern(regexp = "^\\S.*$", message = "channelDescription은 공백으로 시작할 수 없습니다.")
        String channelDescription
) {
}
