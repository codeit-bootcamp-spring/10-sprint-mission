package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    public ChannelResponse toResponse(
            Channel channel,
            Instant lastMessageAt,
            List<UUID> participantUserIdsOrNull
    ) {
        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelType(),
                lastMessageAt,
                // PUBLIC이면 null 들어오게 하고, PRIVATE면 list 들어오게
                channel.getChannelType() == ChannelType.PRIVATE ? participantUserIdsOrNull : null
        );

    }
}

