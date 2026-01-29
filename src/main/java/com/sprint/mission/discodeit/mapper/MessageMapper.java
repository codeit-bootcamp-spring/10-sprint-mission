package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public class MessageMapper {
    public static MessageResponseDto toDto(Message message) {
        return new MessageResponseDto(message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
    public static Message toEntity(MessageCreateDto dto, List<UUID> attachmentIds) {
        return new Message(dto.content(),dto.channelId(),dto.authorId(),attachmentIds);
    }
}
