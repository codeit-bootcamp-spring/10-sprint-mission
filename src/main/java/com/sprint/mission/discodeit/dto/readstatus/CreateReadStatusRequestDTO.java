package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record CreateReadStatusRequestDTO(
        UUID userId,
        UUID channelId
) { }
