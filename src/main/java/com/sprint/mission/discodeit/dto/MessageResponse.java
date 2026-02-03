package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        List<byte[]> attachments
) {
    public static MessageResponse of(Message message, List<byte[]> attachments) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                attachments
        );
    }
}
