package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageMapper() {
        this.binaryContentMapper = new BinaryContentMapper();
        this.userMapper = new UserMapper();
    }

    public MessageDto toDto(Message message) {
        if (message == null)
            return null;

        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(),
                userMapper.toDto(message.getAuthor()),
                message.getAttachments().stream()
                        .map(binaryContentMapper::toDto)
                        .toList()
        );
    }
}
