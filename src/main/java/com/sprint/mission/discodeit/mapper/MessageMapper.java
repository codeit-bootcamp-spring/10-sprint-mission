package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageInfoDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    // Message -> MessageInfoDto
    public MessageInfoDto toMessageInfoDto(Message message) {
        return new MessageInfoDto(message.getId(), message.getSenderId(), message.getChannelId(),
                message.getContent(), message.getUpdatedAt(), message.getAttachmentIds());
    }
}
