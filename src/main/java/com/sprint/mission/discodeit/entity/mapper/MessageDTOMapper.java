package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageDTOMapper {
    public MessageResponseDTO messageToResponseDTO(Message message){
        return new MessageResponseDTO(
                message.getId(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds()
        );
    }
}
