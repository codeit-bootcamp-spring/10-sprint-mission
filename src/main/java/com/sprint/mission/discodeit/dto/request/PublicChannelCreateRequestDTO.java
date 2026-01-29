package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
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

    @NotNull
    private ChannelType channelType;

    @NotBlank
    private String description;
}
