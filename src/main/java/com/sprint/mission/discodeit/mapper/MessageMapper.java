package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    // Message -> MessageInfoDto
    public MessageResponseDto toMessageInfoDto(Message message) {
        return new MessageResponseDto(message.getId(), message.getSenderId(), message.getChannelId(),
                message.getContent(), message.getUpdatedAt(), message.getAttachmentIds());
    }
}
