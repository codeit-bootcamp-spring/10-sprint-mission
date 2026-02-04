package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public record ChannelCreatePrivateDTO(
        List<UUID> userList,
        List<UUID> messageList
) {
}
