package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelRequestUpdateDto(UUID id,
                                      String channelName,
                                      String channelDescription) {
}
