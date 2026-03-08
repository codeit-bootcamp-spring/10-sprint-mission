package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {

    ReadStatusRepository readStatusRepository;
    MessageRepository messageRepository;
    UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            readStatusRepository
                .findAllByChannelId(channel.getId())
                .stream()
                .map(
                    rs -> userMapper.toDto(rs.getUser())
                ).toList(),

            messageRepository
                .findTopByChannelIdOrderByCreatedAtDesc(channel.getId())
                .getCreatedAt()
        );
    }
}
