package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelRequestDTO(
        @NotNull(message = "participantIds는 null값일 수 없습니다.")
        @NotEmpty(message = "비공개 채널엔 최소 1명 이상 입장하여야 합니다.")
        List<UUID> participantIds
) { }
