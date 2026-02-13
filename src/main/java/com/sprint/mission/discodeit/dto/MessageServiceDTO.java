package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreateRequest;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface MessageServiceDTO {
    record MessageCreateRequest(@NonNull UUID authorId, @NonNull UUID channelId, @NonNull String content,
                                @Nonnull List<BinaryContentCreateRequest> attachments) {
    }

    record MessageUpdateRequest(@NonNull UUID messageId, String newContent, List<UUID> attachmentIds) {
    }

    @Builder
    record MessageResponse(@NonNull UUID id, @NonNull UUID authorId, @NonNull UUID channelId, @NonNull String content,
                           List<UUID> attachmentIds, long createdAt) {
    }
}
