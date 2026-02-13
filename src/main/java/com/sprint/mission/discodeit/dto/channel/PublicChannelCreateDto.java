package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotEmpty;

public record PublicChannelCreateDto(
        @NotEmpty
        String name,
        String description
) {
}
