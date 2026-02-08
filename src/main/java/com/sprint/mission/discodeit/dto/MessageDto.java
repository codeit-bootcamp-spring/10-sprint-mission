package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    // 요청 DTO
    public record MessageRequest(
            String content,
            UUID userId,
            UUID channelId
    ) {
    }

    public record MessageResponse(
            UUID messageId,
            String content,
            UUID userId,
            UUID channelId,
            List<UUID> binaryContentIds,
            Instant createdAt
    ) {
        public static MessageResponse from(Message message) {
            return new MessageResponse(
                    message.getId(),
                    message.getContent(),
                    message.getUserId(),
                    message.getChannelId(),
                    message.getBinaryContentIds(),
                    message.getCreatedAt()
            ) ;
        }
    }
}
