package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChannelMapper {
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelMapper(MessageRepository messageRepository, ReadStatusRepository readStatusRepository) {
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
        this.userMapper = new UserMapper();
    }

    public ChannelDto toDto(Channel channel) {
        if (channel == null)
            return null;

        Instant lastMessageAt = Instant.MIN;
        messageRepository.findAllByChannelId(channel.getId());
        for (Message message : messageRepository.findAllByChannelId(channel.getId())) {
            if (message.getCreatedAt().isAfter(lastMessageAt))
                lastMessageAt = message.getUpdatedAt();
        }

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                readStatusRepository.findAllByChannelId(channel.getId()).stream()
                        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
                        .toList(),
                lastMessageAt
        );
    }
}
