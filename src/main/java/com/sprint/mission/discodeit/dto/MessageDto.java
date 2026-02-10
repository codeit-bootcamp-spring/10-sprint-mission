package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record Create(
            String content,
            UUID authorId,
            UUID channelId,
            List<MultipartFile> attachments
    ) {}

    public record Response(
            UUID id,
            String content,
            UUID authorId,
            UUID channelId,
            Instant createdAt,
            List<UUID> attachmentIds
    ) {
        public static Response of(Message message) {
            return new Response(
                    message.getId(),
                    message.getContent(),
                    message.getAuthorId(),
                    message.getChannelId(),
                    message.getCreatedAt(),
                    message.getAttachmentIds()
            );
        }
    }

    public record Update(
            UUID id,
            String content,
            UUID authorId
    ) {}
}
