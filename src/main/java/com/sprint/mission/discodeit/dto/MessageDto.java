package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateRequest(
            String content,
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID authorId,
            @NotNull(message = "채널 ID는 필수입니다.")
            UUID channelId,
            List<UUID> attachmentIds

    ) {
        @AssertTrue(message = "메시지 내용이나 첨부파일 중 하나는 포함되어야 합니다.")
        public boolean isContentOrAttachmentsPresent() {
            boolean hasText = StringUtils.hasText(content);
            boolean hasFile = attachmentIds != null && !attachmentIds.isEmpty();

            return hasText || hasFile;
        }
    }

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) {}

    public record UpdateRequest(
            String content
    ) {}
}
