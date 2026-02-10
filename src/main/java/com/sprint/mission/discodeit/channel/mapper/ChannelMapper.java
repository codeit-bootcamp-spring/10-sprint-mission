package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.dto.ChannelResponse;
import com.sprint.mission.discodeit.channel.entity.Channel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ChannelMapper {
    public ChannelResponse convertToResponse(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getType().name(),
                channel.getName(),
                channel.getDescription(),
                Instant.now(),
                channel.getParticipantUserIds()

        );
    }
}