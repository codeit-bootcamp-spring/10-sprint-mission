package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Message 응답 정보")
public record MessageResponse(
        @Schema(description = "Message ID", example = "9b0712f3-7dbf-4e9f-9f33-63cdb028b07a")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-02-20T11:00:00Z")
        Instant createdAt,
        @Schema(description = "수정 시각", example = "2026-02-20T11:05:00Z")
        Instant updatedAt,
        @Schema(description = "Message 본문", example = "hello world")
        String content,
        @Schema(description = "Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        UUID channelId,
        @Schema(description = "작성자 ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        UUID authorId,
        @Schema(
                description = "첨부 파일 ID 목록",
                example = "[\"a1636f5f-72ab-497f-a6ff-72e4eb6f9f54\", \"af1645f1-94f3-4d62-867f-8827e4c5fa42\"]"
        )
        List<UUID> attachmentIds
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
}
