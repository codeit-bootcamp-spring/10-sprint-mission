package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;

public class MessageMapper {
    public static MessageResponseDto messageToDto(Message message) {
        return new MessageResponseDto(message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
