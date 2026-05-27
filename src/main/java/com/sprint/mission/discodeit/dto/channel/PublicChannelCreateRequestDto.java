package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateRequestDto(
        @NotBlank
        String name,

        String description
){}
