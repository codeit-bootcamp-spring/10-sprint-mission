package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PrivateChannelCreateRequestDTO {
    private UUID userId;
    private ChannelType channelType;
    private List<UUID> memberIds;
}
