package com.sprint.mission.discodeit.message.mapper;

import com.sprint.mission.discodeit.message.dto.MessageResponse;
import com.sprint.mission.discodeit.message.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachments()
        );
    }

}
