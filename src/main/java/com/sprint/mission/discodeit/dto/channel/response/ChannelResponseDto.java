package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.entity.type.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelResponseDto {
    UUID getId();
    String getChannelName();
    List<UUID> getMessageList();
    ChannelType getChannelType();
}
