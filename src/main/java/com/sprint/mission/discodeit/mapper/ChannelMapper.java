package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ChannelMapper {

    public ChannelResponse toResponse(Channel channel) {
        List<UUID> participantIds = null;
        
        if ("PRIVATE".equals(channel.getType())) {
            participantIds = channel.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
        }

        Instant lastMessageAt = channel.getMessages().stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(channel.getCreatedAt());

        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getType(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }
}