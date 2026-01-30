package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.type.ChannelType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChannelUpdateDto {
    private UUID id;
    private String channelName;
    private ChannelType channelType;
}
