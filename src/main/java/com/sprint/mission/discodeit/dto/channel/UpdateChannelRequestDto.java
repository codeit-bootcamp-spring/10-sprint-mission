package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record UpdateChannelRequestDto(UUID id,
                                      ChannelType type,
                                      String channelName,
                                      String channelDescription) {
}
