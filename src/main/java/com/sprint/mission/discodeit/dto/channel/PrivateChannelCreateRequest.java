package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @Size(min = 1, message = "참여자가 1명 이상 필요합니다.")
        List<@NotNull UUID> participantIds
) {
}
