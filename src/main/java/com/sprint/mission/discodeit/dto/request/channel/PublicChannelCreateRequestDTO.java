package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PublicChannelCreateRequestDTO {
    @NotNull
    private UUID userId;

    @NotNull
    private String channelName;

    @NotBlank
    private String description;
}
