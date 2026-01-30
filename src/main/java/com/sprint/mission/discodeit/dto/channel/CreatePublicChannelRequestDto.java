package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

public record CreatePublicChannelRequestDto(String channelName,
                                            String channelDescription) {
}
