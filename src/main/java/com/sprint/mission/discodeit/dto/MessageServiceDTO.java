package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageServiceDTO {
    record MessageCreateRequest(@Nonnull UUID authorId, @Nonnull UUID channelId, @Nonnull String content) {
    }

    record MessageCreateCommand(UUID authorId, UUID channelId, String content, List<BinaryContentCreateRequest> attachments) {
        public MessageCreateCommand(MessageCreateRequest request, List<MultipartFile> attachments) {
            this(request.authorId(), request.channelId(), request.content(),
                    attachments.stream().map(BinaryContentCreateRequest::from).toList());
        }
    }

    record MessageUpdateRequest(@JsonProperty("newContent") @Nullable String content) {
    }

    record MessageUpdateCommand(UUID id, String content) {
        public MessageUpdateCommand(UUID id, MessageUpdateRequest request) {
            this(id, request.content());
        }
    }

    // todo: error log
    @Builder
    record MessageResponse(UUID id, UUID authorId, UUID channelId, String content,
                           List<UUID> attachmentIds, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
    }
}
