package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
public class MessageMapper {
    public MessageResponseDto toDto(Message message) {
        return new MessageResponseDto(message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
    public Message toEntity(MessageCreateDto dto, List<UUID> attachmentIds) {
        return new Message(dto.content(),dto.channelId(),dto.authorId(),attachmentIds);
    }
}
