package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponseDto toDto(Message message){
        if(message == null) return null;

        return new MessageResponseDto(message.getId(),
                message.getUserId(),
                message.getChannelId(),
                message.getText());
    }
}
