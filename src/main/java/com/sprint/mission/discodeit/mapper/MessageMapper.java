package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
