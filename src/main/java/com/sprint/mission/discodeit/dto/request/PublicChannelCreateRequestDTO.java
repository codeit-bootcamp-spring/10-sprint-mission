package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PublicChannelCreateRequestDTO {
    private UUID userId;
    private String channelName;
    private ChannelType channelType;
    private String description;
}
