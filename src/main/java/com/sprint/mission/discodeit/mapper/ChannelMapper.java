package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {

    // Channel -> ChannelInfoDto
    public ChannelInfoDto toChannelInfoDto(Channel channel, MessageRepository messageRepository) {
        Instant lastMessageAt = messageRepository.findById(channel.getLastMessageId())
                .map(m -> m.getCreatedAt())
                .orElse(channel.getCreatedAt());

        List<UUID> memberIds = null;
        String name = channel.getName();
        String description = channel.getDescription();
        if (channel.getIsPrivate() == IsPrivate.PRIVATE) {
            memberIds = channel.getUserIds();
            description = null;
            name = null;
        }

        return new ChannelInfoDto(
                channel.getId(),
                name,
                channel.getIsPrivate(),
                description,
                channel.getOwnerId(),
                lastMessageAt,
                memberIds
        );
    }


}
