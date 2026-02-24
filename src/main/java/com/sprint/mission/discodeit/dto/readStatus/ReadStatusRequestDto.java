package com.sprint.mission.discodeit.dto.readStatus;

import java.util.UUID;

public record ReadStatusRequestDto(
        UUID userId,
        UUID channelId

) {}
