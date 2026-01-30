package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.SendMessageRequestDTO;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageMapper {
    public static Message toEntity(SendMessageRequestDTO dto, List<UUID> attachments) {
        return new Message(
                dto.sentUserId(),
                dto.sentChannelId(),
                dto.content(),
                attachments
        );
    }

    public static MessageResponseDTO toResponse(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getCreatedAt(),
                message.getSentUserId(),
                message.getSentChannelId(),
                message.getContent(),
                message.getAttachmentIds()
        );
    }

    public static List<MessageResponseDTO> toResponseList(List<Message> messages) {
        List<MessageResponseDTO> dtos = new ArrayList<>();

        for (Message message : messages) {
            dtos.add(MessageMapper.toResponse(message));
        }

        return dtos;
    }
}
