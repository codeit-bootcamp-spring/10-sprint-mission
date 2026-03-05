package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageMapper {
    public static Message toEntity(CreateMessageRequestDTO dto, List<UUID> attachments) {
        return new Message(
                dto.authorId(),
                dto.channelId(),
                dto.content(),
                attachments
        );
    }

    public static MessageDto toResponse(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getSentChannelId(),
                message.getSentUserId(),
                message.getAttachmentIds()
        );
    }

    public static List<MessageDto> toResponseList(List<Message> messages) {
        List<MessageDto> dtos = new ArrayList<>();

        for (Message message : messages) {
            dtos.add(MessageMapper.toResponse(message));
        }

        return dtos;
    }
}
