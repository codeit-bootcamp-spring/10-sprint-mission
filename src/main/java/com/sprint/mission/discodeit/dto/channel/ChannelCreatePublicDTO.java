package com.sprint.mission.discodeit.dto.channel;

import java.util.List;
import java.util.UUID;

public record ChannelCreatePublicDTO(
        String channelName,
        String description,
        List<UUID> userList,
        List<UUID> messageList
) {
}
