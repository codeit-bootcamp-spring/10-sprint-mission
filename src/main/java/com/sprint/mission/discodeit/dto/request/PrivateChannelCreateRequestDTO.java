package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PrivateChannelCreateRequestDTO {
    @NotNull
    private UUID userId;

    @NotNull
    private ChannelType channelType;

    private List<UUID> memberIds;
}
