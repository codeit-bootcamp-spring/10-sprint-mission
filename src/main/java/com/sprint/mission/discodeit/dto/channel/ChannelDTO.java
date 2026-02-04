package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDTO(
        UUID channelId,
        String channelName,
        String description,
        boolean isPrivate,
        List<UUID> userList,
        List<UUID> messageList,
        Instant lastMessageTime
) {}
